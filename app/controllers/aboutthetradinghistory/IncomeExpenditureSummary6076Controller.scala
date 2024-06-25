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
import models.submissions.aboutthetradinghistory.{IncomeAndExpenditureSummary6076, IncomeExpenditure6076Entry, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.IncomeExpenditureSummary6076Id
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import util.NumberUtil.zeroBigDecimal
import views.html.aboutthetradinghistory.incomeExpenditureSummary6076

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
    runWithSessionCheck { turnoverSections6076 =>
      val entries = createEntries(turnoverSections6076)

      Future.successful(
        Ok(
          view(
            request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.incomeExpenditureConfirmation6076) match {
              case Some(incomeExpenditureConfirmation) =>
                incomeExpenditureSummary6076Form.fill(incomeExpenditureConfirmation)
              case _                                   => incomeExpenditureSummary6076Form
            },
            request.sessionData.toSummary,
            entries
          )
        )
      )
    }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val entries = createEntries(turnoverSections6076)

      continueOrSaveAsDraft[String](
        incomeExpenditureSummary6076Form,
        formWithErrors => BadRequest(view(formWithErrors, request.sessionData.toSummary, entries)),
        data => {
          val updatedSections =
            (entries zip turnoverSections6076).map { case (entry, previousSection) =>
              previousSection.copy(
                incomeAndExpenditureSummary = Some(
                  IncomeAndExpenditureSummary6076(
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
                )
              )
            }

          val updatedData = updateAboutTheTradingHistoryPartOne {
            _.copy(
              turnoverSections6076 = Some(updatedSections),
              incomeExpenditureConfirmation6076 = Some(data)
            )
          }

          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(IncomeExpenditureSummary6076Id, updatedData).apply(updatedData)))
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6076] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def createEntries(
    turnoverSections6076: Seq[TurnoverSection6076]
  ): Seq[IncomeExpenditure6076Entry] =
    turnoverSections6076.map { section =>
      val grossReceipts       = section.grossReceiptsExcludingVAT.map(_.total).getOrElse(zeroBigDecimal)
      val baseLoadReceipts    = section.grossReceiptsForBaseLoad.map(_.total).getOrElse(zeroBigDecimal)
      val otherIncome         = section.otherIncome.getOrElse(zeroBigDecimal)
      val costOfSales         = section.costOfSales6076Sum.map(_.total).getOrElse(zeroBigDecimal)
      val staffCosts          = section.staffCosts.map(_.total).getOrElse(zeroBigDecimal)
      val premisesCosts       = section.premisesCosts.map(_.total).getOrElse(zeroBigDecimal)
      val operationalExpenses = section.operationalExpenses.map(_.total).getOrElse(zeroBigDecimal)
      val headOfficeExpenses  = section.headOfficeExpenses.getOrElse(zeroBigDecimal)

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
