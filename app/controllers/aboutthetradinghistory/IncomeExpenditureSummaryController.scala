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
import form.aboutthetradinghistory.IncomeExpenditureSummaryForm.incomeExpenditureSummaryForm
import models.pages.IncomeExpenditureEntry
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, IncomeExpenditureSummary, IncomeExpenditureSummaryData, TotalPayrollCost}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.IncomeExpenditureSummaryId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.incomeExpenditureSummary
import util.NumberUtil.zeroBigDecimal

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class IncomeExpenditureSummaryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  incomeExpenditureSummaryView: incomeExpenditureSummary,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val entries = request.sessionData.aboutTheTradingHistory.map(createIncomeExpenditureEntries).getOrElse(Seq.empty)

    Ok(
      incomeExpenditureSummaryView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.incomeExpenditureSummary) match {
          case Some(incomeExpenditureSummary) => incomeExpenditureSummaryForm.fill(incomeExpenditureSummary)
          case _                              => incomeExpenditureSummaryForm
        },
        request.sessionData.toSummary,
        entries
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[IncomeExpenditureSummary](
      incomeExpenditureSummaryForm,
      formWithErrors => {
        val entries =
          request.sessionData.aboutTheTradingHistory.map(createIncomeExpenditureEntries).getOrElse(Seq.empty)
        BadRequest(incomeExpenditureSummaryView(formWithErrors, request.sessionData.toSummary, entries))
      },
      data => {
        val tradingHistoryData =
          request.sessionData.aboutTheTradingHistory.map(createIncomeExpenditureEntries).getOrElse(Seq.empty)

        val incomeExpenditureSummaryData: Seq[IncomeExpenditureSummaryData] = tradingHistoryData.map { entry =>
          IncomeExpenditureSummaryData(
            financialYearEnd = entry.financialYearEnd,
            totalTurnover = entry.totalTurnover,
            totalCostOfSales = entry.totalCostOfSales,
            totalGrossProfits = entry.totalGrossProfits,
            totalPayrollCost = entry.totalPayrollCost,
            variableExpenses = entry.variableExpenses,
            fixedExpenses = entry.fixedExpenses,
            otherCost = entry.otherCost,
            totalNetProfit = entry.totalNetProfit,
            profitMargin = entry.profitMargin
          )
        }

        val updatedData = updateAboutTheTradingHistory { currentAboutTheTradingHistory =>
          currentAboutTheTradingHistory.copy(
            incomeExpenditureSummary = Some(data),
            incomeExpenditureSummaryData = incomeExpenditureSummaryData
          )
        }

        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(IncomeExpenditureSummaryId, updatedData).apply(updatedData)))
      }
    )
  }

  private def createIncomeExpenditureEntries(
    aboutTheTradingHistory: AboutTheTradingHistory
  ): Seq[IncomeExpenditureEntry] =
    aboutTheTradingHistory.turnoverSections.map { turnoverSection =>
      val finYearEnd       = turnoverSection.financialYearEnd
      val costOfSalesEntry = aboutTheTradingHistory.costOfSales.find(_.financialYearEnd == finYearEnd).get
      val totalCostOfSales = costOfSalesEntry.total

      val payrollCostEntry  = aboutTheTradingHistory.totalPayrollCostSections
        .find(_.financialYearEnd == finYearEnd)
        .getOrElse(TotalPayrollCost(finYearEnd, None, None))
      val totalPayrollCosts = payrollCostEntry.total

      val variableExpenses = aboutTheTradingHistory.variableOperatingExpenses
        .flatMap(_.variableOperatingExpenses.find(_.financialYearEnd == finYearEnd).map(_.total))
        .getOrElse(zeroBigDecimal)

      val fixedExpensesEntry =
        aboutTheTradingHistory.fixedOperatingExpensesSections.find(_.financialYearEnd == finYearEnd).get
      val totalFixedExpenses = fixedExpensesEntry.total

      val otherCosts =
        aboutTheTradingHistory.otherCosts.flatMap(_.otherCosts.find(_.financialYearEnd == finYearEnd)).map(_.total).sum

      val totalTurnover    = turnoverSection.total
      val totalGrossProfit = totalTurnover - totalCostOfSales
      val totalNetProfit   = totalGrossProfit - (totalPayrollCosts + variableExpenses + totalFixedExpenses + otherCosts)
      val profitMargin     = if (totalTurnover > BigDecimal(0)) (totalNetProfit / totalTurnover) * 100 else BigDecimal(0)

      IncomeExpenditureEntry(
        financialYearEnd = turnoverSection.financialYearEnd.toString,
        totalTurnover = totalTurnover,
        turnoverUrl = routes.TurnoverController.show().url + "?from=IES",
        totalCostOfSales = totalCostOfSales,
        costOfSalesUrl = routes.CostOfSalesController.show().url + "?from=IES",
        totalGrossProfits = totalGrossProfit,
        totalPayrollCost = totalPayrollCosts,
        totalPayrollCostURL = routes.TotalPayrollCostsController.show().url + "?from=IES",
        variableExpenses = variableExpenses,
        variableExpensesURL = routes.VariableOperatingExpensesController.show().url + "?from=IES",
        fixedExpenses = totalFixedExpenses,
        fixedExpensesUrl = routes.FixedOperatingExpensesController.show().url + "?from=IES",
        otherCost = otherCosts,
        otherCostsUrl = routes.OtherCostsController.show().url + "?from=IES",
        totalNetProfit = totalNetProfit,
        profitMargin = profitMargin
      )
    }
}
