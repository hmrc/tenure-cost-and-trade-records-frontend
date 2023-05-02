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
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.TotalPayrollCostForm.totalPayrollCostForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.TotalPayrollCost
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TotalPayrollCostId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.totalPayrollCosts

import javax.inject.{Inject, Named, Singleton}

@Singleton
class TotalPayrollCostsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  totalPayrollCostsView: totalPayrollCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      totalPayrollCostsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.totalPayrollCost) match {
          case Some(totalPayrollCost) => totalPayrollCostForm.fillAndValidate(totalPayrollCost)
          case _                      => totalPayrollCostForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TotalPayrollCost](
      totalPayrollCostForm,
      formWithErrors => BadRequest(totalPayrollCostsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(totalPayrollCost = Some(data)))
        Redirect(navigator.nextPage(TotalPayrollCostId).apply(updatedData))
      }
    )
  }

}
