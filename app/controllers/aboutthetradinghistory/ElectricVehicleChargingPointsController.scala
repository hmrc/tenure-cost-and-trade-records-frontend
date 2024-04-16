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

package controllers.aboutthetradinghistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.ElectricVehicleChargingPointsForm.electricVehicleChargingPointsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.ElectricVehicleChargingPoints
import views.html.aboutthetradinghistory.electricVehicleChargingPoints
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.ElectricVehicleChargingPointsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ElectricVehicleChargingPointsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  electricVehicleChargingPointsView: electricVehicleChargingPoints,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        electricVehicleChargingPointsView(
          request.sessionData.aboutTheTradingHistory.flatMap(_.electricVehicleChargingPoints) match {
            case Some(electricVehicleChargingPoints) =>
              electricVehicleChargingPointsForm.fill(electricVehicleChargingPoints)
            case _                                   => electricVehicleChargingPointsForm
          },
          request.sessionData.toSummary,
          getBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ElectricVehicleChargingPoints](
      electricVehicleChargingPointsForm,
      formWithErrors =>
        BadRequest(
          electricVehicleChargingPointsView(
            formWithErrors,
            request.sessionData.toSummary,
            getBackLink
          )
        ),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(electricVehicleChargingPoints = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(ElectricVehicleChargingPointsId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#ev-charging-point"
      case _     => controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show().url
    }

}
