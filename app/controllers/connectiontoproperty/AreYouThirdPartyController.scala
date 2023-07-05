/*
 * Copyright 2023 HM Revenue & Customs
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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.connectiontoproperty.AreYouThirdPartyForm.areYouThirdPartyForm
import models.Session
import models.submissions.common.{AnswerYes, AnswersYesNo}
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AreYouThirdPartyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.areYouThirdParty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AreYouThirdPartyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  areYouThirdPartyView: areYouThirdParty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        areYouThirdPartyView(
          request.sessionData.stillConnectedDetails.flatMap(_.areYouThirdParty) match {
            case Some(enforcementAction) => areYouThirdPartyForm.fillAndValidate(enforcementAction)
            case _                       => areYouThirdPartyForm
          },
          getBackLink(request.sessionData),
          request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      areYouThirdPartyForm,
      formWithErrors =>
        BadRequest(
          areYouThirdPartyView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(areYouThirdParty = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(AreYouThirdPartyPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty.map(_.name)) match {
      case Some("yes") => controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      case Some("no")  => controllers.connectiontoproperty.routes.TradingNamePayingRentController.show().url
      case _           =>
        logger.warn(s"Back link for are you third party reached with unknown trade services value")
        controllers.connectiontoproperty.routes.ConnectionToThePropertyController.show().url
    }
}
