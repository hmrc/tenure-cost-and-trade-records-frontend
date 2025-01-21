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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.TradingNamePayingRentForm.tradingNamePayingRentForm
import models.audit.ChangeLinkAudit
import models.submissions.common.AnswersYesNo
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.TradingNamePayingRentPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tradingNamePayingRent

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradingNamePayingRentController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  tradingNamePayingRentView: tradingNamePayingRent,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>

    Future.successful(
      Ok(
        tradingNamePayingRentView(
          request.sessionData.stillConnectedDetails.flatMap(_.tradingNamePayingRent) match {
            case Some(tradingNamePayingRent) => tradingNamePayingRentForm.fill(tradingNamePayingRent)
            case _                           => tradingNamePayingRentForm
          },
          getBackLink,
          request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      tradingNamePayingRentForm,
      formWithErrors =>
        BadRequest(
          tradingNamePayingRentView(
            formWithErrors,
            getBackLink,
            request.sessionData.stillConnectedDetails.get.tradingNameOperatingFromProperty.get.tradingName,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(tradingNamePayingRent = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(request) == "CYA")
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
}
