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
import form.connectiontoproperty.AreYouThirdPartyForm.theForm
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AreYouThirdPartyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.areYouThirdParty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AreYouThirdPartyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theView: areYouThirdParty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AreYouThirdParty")
    val freshForm  = theForm
    val filledForm =
      for
        stillConnectedDetails <- request.sessionData.stillConnectedDetails
        areYouThirdParty      <- stillConnectedDetails.areYouThirdParty
      yield theForm.fill(areYouThirdParty)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        getBackLink,
        request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            getBackLink,
            request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
            request.sessionData.toSummary,
            isReadOnly
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(areYouThirdParty = Some(data)))
        repo.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(using request) == "CYA")
          val nextPage      =
            redirectToCYA.getOrElse(navigator.nextPage(AreYouThirdPartyPageId, updatedData).apply(updatedData))
          Redirect(nextPage)
        }
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      case _     =>
        request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty) match {
          case Some(AnswerYes) => controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
          case _               => controllers.connectiontoproperty.routes.TradingNamePayingRentController.show().url
        }
    }
