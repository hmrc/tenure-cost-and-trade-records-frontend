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

package navigation

import connectors.Audit
import models.Session
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, TouringAndTentingPitches}
import models.submissions.common.{AnswerNo, AnswerYes, ContactDetails}
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutTheTradingHistoryNavigatorSpec extends TestBaseSpec {

  val audit: Audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutTheTradingHistoryNavigator(audit)

  val stillConnectedDetailsYes: Option[StillConnectedDetails] = Some(
    StillConnectedDetails(Some(AddressConnectionTypeYes))
  )
  val removeConnection: Option[RemoveConnectionDetails]       = Some(
    RemoveConnectionDetails(
      Some(
        RemoveConnectionsDetails(
          "John Smith",
          ContactDetails("12345678909", "test@email.com"),
          Some("Additional Information is here")
        )
      )
    )
  )

  val sessionAboutYou: Session =
    Session(
      "99996010004",
      "FOR6010",
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      stillConnectedDetailsYes,
      removeConnection
    )

  val sessionAboutYou6015 = sessionAboutYou.copy(referenceNumber = "99996015004", forType = "FOR6015")
  val sessionAboutYou6020 = sessionAboutYou.copy(referenceNumber = "99996020004", forType = "FOR6020")
  val sessionAboutYou6030 = sessionAboutYou.copy(referenceNumber = "99996030004", forType = "FOR6030")
  val sessionAboutYou6045 = sessionAboutYou.copy(referenceNumber = "99996045004", forType = "FOR6045")
  val sessionAboutYou6076 = sessionAboutYou.copy(referenceNumber = "99996076004", forType = "FOR6076")

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About the trading history navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.routes.LoginController.show
    }

    "return a function that goes financial-year-end page when about your trading history has been completed" in {
      navigator
        .nextPage(AboutYourTradingHistoryPageId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.TurnoverController.show()
    }

    "return a function that goes the total payroll costs page when about your cost of sales has been completed" in {
      navigator
        .nextPage(CostOfSalesId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show()
    }

    "return a function that goes the variable operating expenses page when total payroll page has been completed" in {
      navigator
        .nextPage(TotalPayrollCostId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) mustBe controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show()
    }

    "return a function that goes the fixed operating expenses page when variable operating expenses has been completed" in {
      navigator
        .nextPage(VariableOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.FixedOperatingExpensesController.show()
    }

    "return a function that goes other costs page when fixed operating expenses has been completed" in {
      navigator
        .nextPage(FixedOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.OtherCostsController.show()
    }

    "return a function that goes other costs page when fixed1 operating expenses has been completed" in {
      navigator
        .nextPage(ElectricVehicleChargingPointsId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) mustBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController
        .show()
    }

    "return a function that goes the task lists page when total payroll cost has been completed" in {
      navigator
        .nextPage(CheckYourAnswersAboutTheTradingHistoryId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.routes.TaskListController.show()
    }

    // 6020 specific

    "return a function that goes the bunkered fuel question page when total fuel sold has been completed" in {
      navigator
        .nextPage(TotalFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController
        .show()
    }
    "return a function that goes income expenditure page when other costs has been completed" in {
      navigator
        .nextPage(OtherCostsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController
        .show()
    }
    "return a function that goes bunker fuel card details  page when bunkered fuel sold has been completed" in {
      navigator
        .nextPage(BunkeredFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.BunkerFuelCardDetailsController
        .show(None)
    }
    "return a function that goes the percentage from fuel cards page when customer credit account has been completed" in {
      navigator
        .nextPage(CustomerCreditAccountsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.AcceptLowMarginFuelCardController
        .show()
    }
    "return a function that goes the low margin fuel card details page when percentage from fuel has been completed" in {
      navigator
        .nextPage(PercentageFromFuelCardsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.LowMarginFuelCardDetailsController
        .show()
    }
    "return a function that goes the add another bunker card page when bunker card detail has been completed" in {
      navigator
        .nextPage(BunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) mustBe controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }
    "return a function that goes the customer credit account page when add another bunker fuel card has been completed" in {
      navigator
        .nextPage(AddAnotherBunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController
        .show()
    }
    "return a function that goes the non fuel turnover page when add another low margin fuel card has been completed" in {
      navigator
        .nextPage(AddAnotherLowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }
    "return a function that goes the add another low-margin fuel card page when lm fuel card details has been completed" in {
      navigator
        .nextPage(LowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) mustBe controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }
    "return a function that goes the unusual circumstances page when income expenditure has been completed" in {
      navigator
        .nextPage(IncomeExpenditureSummaryId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.UnusualCircumstancesController.show()
    }

    "return a function that goes the correct turnover page when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.TurnoverController.show()
    }

    "return a function that goes the total fuel sold page if the form is 6020 when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) mustBe controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show()
    }

    "return a function that goes the correct turnover page if the form is 6030 when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) mustBe controllers.aboutthetradinghistory.routes.Turnover6030Controller.show()
    }

    "return a function that goes the CYA page when turnover page has been completed form 6010" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) mustBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }
    "return a function that goes the cost of sales  page when turnover page has been completed form 6015" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6015)
        .apply(sessionAboutYou6015) mustBe controllers.aboutthetradinghistory.routes.CostOfSalesController.show()
    }
    "return a function that goes the EV  page when turnover page has been completed form 6020" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) mustBe controllers.aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show()
    }
    "return a function that goes the unusual circumstances  page when turnover page has been completed form 6030" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) mustBe controllers.aboutthetradinghistory.routes.UnusualCircumstancesController
        .show()
    }

    //6045

    "return a function that goes the cya tenting pitches page when tenting pitches on site completed with no" in {
      val sessionWithNoTentingPitchesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            touringAndTentingPitches = Some(
              TouringAndTentingPitches(
                tentingPitchesOnSite = Some(AnswerNo)
              )
            )
          )
        )
      )

      navigator
        .nextPageForTentingPitches(TentingPitchesOnSiteId, sessionWithNoTentingPitchesOnSite)
        .apply(
          sessionWithNoTentingPitchesOnSite
        ) mustBe controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController
        .show()
    }
    "return a function that goes the tenting pitches all year page when tenting pitches on site completed with yes" in {
      val sessionWithYesTentingPitchesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            touringAndTentingPitches = Some(
              TouringAndTentingPitches(
                tentingPitchesOnSite = Some(AnswerYes)
              )
            )
          )
        )
      )

      navigator
        .nextPageForTentingPitches(TentingPitchesOnSiteId, sessionWithYesTentingPitchesOnSite)
        .apply(
          sessionWithYesTentingPitchesOnSite
        ) mustBe controllers.aboutthetradinghistory.routes.TentingPitchesAllYearController
        .show()
    }

    "return a function that goes to lettings  page when tenting pitches all year completed" in {
      navigator
        .nextPage(TentingPitchesAllYearId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) mustBe controllers.aboutthetradinghistory.routes.PitchesForCaravansController.show()
    }

    "return a function that goes to gross receipts for base load page when  gross receipts excluding vat completed" in {
      navigator
        .nextPage(GrossReceiptsExcludingVatId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.GrossReceiptsForBaseLoadController
        .show()
    }

    "return a function that goes to cost of sales 6076 page when  other income completed" in {
      navigator
        .nextPage(OtherIncomeId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.CostOfSales6076Controller.show()
    }
    "return a function that goes to staff costs page when  cost of sales 6076 completed" in {
      navigator
        .nextPage(CostOfSales6076Id, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.StaffCostsController.show()
    }
    "return a function that goes to premises  costs page when  staff costs completed" in {
      navigator
        .nextPage(StaffCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.PremisesCostsController.show()
    }

    "return a function that goes to operational expenses page when  premises  costs completed" in {
      navigator
        .nextPage(PremisesCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.OperationalExpensesController
        .show()
    }
    "return a function that goes to other income page when operational cost completed" in {
      navigator
        .nextPage(GrossReceiptsForBaseLoadId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) mustBe controllers.aboutthetradinghistory.routes.OtherIncomeController.show()
    }
    "return a function that goes to expenditure page when head office expenses completed" in {
      navigator
        .nextPage(HeadOfficeExpensesId, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) mustBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show()
    }

    "return a function that starts new section, when CYA for tenting pitches ready " in {
      navigator
        .nextPage(CheckYourAnswersTentingPitchesId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) mustBe controllers.routes.TaskListController.show()

      // TODO change when section tenting pitches ready
    }

  }
}
