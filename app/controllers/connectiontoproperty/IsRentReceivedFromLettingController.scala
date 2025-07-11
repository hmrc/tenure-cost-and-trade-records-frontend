/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.isRentReceivedFromLettingForm.isRentReceivedFromLettingForm
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.LettingIncomePageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.isRentReceivedFromLetting

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IsRentReceivedFromLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  isRentReceivedFromLettingView: isRentReceivedFromLetting,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("IsRentReceivedFromLetting")

    Future.successful(
      Ok(
        isRentReceivedFromLettingView(
          request.sessionData.stillConnectedDetails.flatMap(_.isAnyRentReceived) match {
            case Some(isAnyRentReceived) => isRentReceivedFromLettingForm.fill(isAnyRentReceived)
            case _                       => isRentReceivedFromLettingForm
          },
          getBackLink,
          request.sessionData.toSummary
        )
      )
    )

  }

  def submit: Action[AnyContent]                                        = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      isRentReceivedFromLettingForm,
      formWithErrors =>
        BadRequest(
          isRentReceivedFromLettingView(
            formWithErrors,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(isAnyRentReceived = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            TimeUnit.SECONDS.sleep(1)
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ =>
                navigator.from == "CYA" && (data == AnswerNo ||
                  request.sessionData.stillConnectedDetails.flatMap(_.isAnyRentReceived).contains(AnswerYes))
              )
              .getOrElse(navigator.nextWithoutRedirectToCYA(LettingIncomePageId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }
  private def getBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      case _     => controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show().url
    }
}
