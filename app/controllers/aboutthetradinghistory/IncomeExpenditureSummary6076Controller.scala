/*
 * Copyright 2024 HM Revenue & Customs
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
import form.aboutthetradinghistory.IncomeExpenditureSummary6076Form.incomeExpenditureSummary6076Form
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, GrossReceiptsExcludingVAT, IncomeExpenditure6076Entry, IncomeExpenditureSummary, IncomeExpenditureSummary6076Data, IncomeExpenditureSummaryData, TotalPayrollCost, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.IncomeExpenditureSummary6076Id
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.incomeExpenditureSummary6076

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeExpenditureSummary6076Controller @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: incomeExpenditureSummary6076,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { _ =>
      val entries = request.sessionData.aboutTheTradingHistoryPartOne.map(createEntries).getOrElse(Seq.empty)

      Future.successful(
        Ok(
          view(
            request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.incomeExpenditureSummary6076) match {
              case Some(incomeExpenditureSummary6076) =>
                incomeExpenditureSummary6076Form.fill(incomeExpenditureSummary6076)
              case _                                  => incomeExpenditureSummary6076Form
            },
            request.sessionData.toSummary,
            entries
          )
        )
      )
    }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[String](
      incomeExpenditureSummary6076Form,
      formWithErrors => {
        val entries =
          request.sessionData.aboutTheTradingHistoryPartOne.map(createEntries).getOrElse(Seq.empty)
        BadRequest(view(formWithErrors, request.sessionData.toSummary, entries))
      },
      data => {
        val tradingHistoryData =
          request.sessionData.aboutTheTradingHistoryPartOne.map(createEntries).getOrElse(Seq.empty)

        val incomeExpenditureSummary6076Data: Seq[IncomeExpenditureSummary6076Data] = tradingHistoryData.map { entry =>
          IncomeExpenditureSummary6076Data(
            financialYearEnd = entry.financialYearEnd,
            totalGrossReceipts = entry.totalGrossReceipts,
            totalBaseLoadReceipts = entry.totalBaseLoadReceipts,
            totalOtherIncome = entry.totalOtherIncome,
            totalCostOfSales = entry.totalCostOfSales,
            totalStaffCosts = entry.totalStaffCosts,
            totalPremisesCosts = entry.totalPremisesCosts,
            totalOperationalExpenses = entry.totalOperationalExpenses,
            headOfficeExpenses = entry.headOfficeExpenses,
            netProfitOrLoss = entry.netProfitOrLoss
          )
        }

        val updatedData = updateAboutTheTradingHistoryPartOne { currentAboutTheTradingHistory =>
          currentAboutTheTradingHistory.copy(
            incomeExpenditureSummary6076 = Some(data),
            incomeExpenditureSummary6076Data = Some(incomeExpenditureSummary6076Data)
          )
        }

        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(IncomeExpenditureSummary6076Id, updatedData).apply(updatedData)))
      }
    )
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6076] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def createEntries(
    aboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne
  ): Seq[IncomeExpenditure6076Entry] = {

    val grossReceiptsMap: Map[LocalDate, GrossReceiptsExcludingVAT] =
      aboutTheTradingHistoryPartOne.grossReceiptsExcludingVAT
        .getOrElse(Seq.empty)
        .map(receipt => receipt.financialYearEnd -> receipt)
        .toMap

    aboutTheTradingHistoryPartOne.turnoverSections6076.getOrElse(Seq.empty).map { section =>
      val grossReceipts       = grossReceiptsMap.get(section.financialYearEnd).map(_.total).getOrElse(BigDecimal(0))
      val baseLoadReceipts    = section.grossReceiptsForBaseLoad.map(_.total).getOrElse(BigDecimal(0))
      val otherIncome         = section.otherIncome.getOrElse(BigDecimal(0))
      val costOfSales         = section.costOfSales6076Sum.map(_.total).getOrElse(BigDecimal(0))
      val staffCosts          = section.staffCosts.map(_.total).getOrElse(BigDecimal(0))
      val premisesCosts       = section.premisesCosts.map(_.total).getOrElse(BigDecimal(0))
      val operationalExpenses = section.operationalExpenses.map(_.total).getOrElse(BigDecimal(0))
      val headOfficeExpenses  = section.headOfficeExpenses.getOrElse(BigDecimal(0))

      val netProfitOrLoss =
        (grossReceipts + baseLoadReceipts + otherIncome) - (costOfSales + staffCosts + premisesCosts + operationalExpenses + headOfficeExpenses)

      IncomeExpenditure6076Entry(
        financialYearEnd = section.financialYearEnd.toString,
        totalGrossReceipts = grossReceipts,
        grossReceiptsUrl = routes.GrossReceiptsExcludingVATController.show().url + "?from=IES",
        totalBaseLoadReceipts = baseLoadReceipts,
        baseLoadReceiptsUrl = routes.GrossReceiptsForBaseLoadController.show().url + "?from=IES",
        totalOtherIncome = otherIncome,
        otherIncomeUrl = routes.OtherIncomeController.show().url + "?from=IES",
        totalCostOfSales = costOfSales,
        costOfSalesUrl = routes.CostOfSales6076Controller.show().url + "?from=IES",
        totalStaffCosts = staffCosts,
        staffCostsUrl = routes.StaffCostsController.show().url + "?from=IES",
        totalPremisesCosts = premisesCosts,
        premisesCostsUrl = routes.PremisesCostsController.show().url + "?from=IES",
        totalOperationalExpenses = operationalExpenses,
        operationalExpensesUrl = routes.OperationalExpensesController.show().url + "?from=IES",
        headOfficeExpenses = headOfficeExpenses,
        headOfficeExpensesUrl = routes.HeadOfficeExpensesController.show().url + "?from=IES",
        netProfitOrLoss = netProfitOrLoss
      )
    }
  }
}
