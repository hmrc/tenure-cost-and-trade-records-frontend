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
import form.connectiontoproperty.TradingNamePayingRentForm.theForm
import models.submissions.common.AnswersYesNo
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.TradingNamePayingRentPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tradingNamePayingRent as TradingNamePayingRentView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradingNamePayingRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theView: TradingNamePayingRentView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("TradingNamePayingRent")
    val freshForm  = theForm
    val filledForm =
      for {
        stillConnectedDetails <- request.sessionData.stillConnectedDetails
        tradingNamePayingRent <- stillConnectedDetails.tradingNamePayingRent
      } yield theForm.fill(tradingNamePayingRent)

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
        val updatedData = updateStillConnectedDetails(_.copy(tradingNamePayingRent = Some(data)))
        repo.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(using request) == "CYA")
          val nextPage      =
            redirectToCYA.getOrElse(navigator.nextPage(TradingNamePayingRentPageId, updatedData).apply(updatedData))
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
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
    }
