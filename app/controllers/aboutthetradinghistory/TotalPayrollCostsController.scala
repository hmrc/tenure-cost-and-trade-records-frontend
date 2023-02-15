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

import actions.WithSessionRefiner
import form.aboutthetradinghistory.TotalPayrollCostForm.totalPayrollCostForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.Session
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TotalPayrollCostId
import play.api.i18n.I18nSupport
import play.api.i18n.Lang.logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.totalPayrollCosts

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TotalPayrollCostsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  totalPayrollCostsView: totalPayrollCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      totalPayrollCostsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.totalPayrollCost) match {
          case Some(totalPayrollCost) => totalPayrollCostForm.fillAndValidate(totalPayrollCost)
          case _                      => totalPayrollCostForm
        },
        getBackLink(request.sessionData)
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    totalPayrollCostForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(totalPayrollCostsView(formWithErrors, getBackLink(request.sessionData)))),
        data => {
          val updatedData = updateAboutTheTradingHistory(_.copy(totalPayrollCost = Some(data)))
          Future.successful(Redirect(navigator.nextPage(TotalPayrollCostId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutTheTradingHistory.flatMap(_.costOfSalesOrGrossProfit.map(_.name)) match {
      case Some("costOfSales") => controllers.aboutthetradinghistory.routes.CostOfSalesController.show().url
      case Some("grossProfit") => controllers.aboutthetradinghistory.routes.GrossProfitsController.show().url
      case _                   =>
        logger.warn(s"Back link for tied goods page reached with unknown enforcement taken value")
        controllers.routes.TaskListController.show().url
    }
}
