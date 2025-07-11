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

package navigation

import connectors.Audit
import models.ForType.*
import models.Session
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne, AdditionalActivities, BunkerFuelCardDetails, BunkerFuelCardsDetails, LowMarginFuelCardDetail, LowMarginFuelCardsDetails, OtherHolidayAccommodation, TouringAndTentingPitches}
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.RenewablesPlantType.*
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.AddressConnectionType.*
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.{TestBaseSpec, toOpt}

import scala.concurrent.ExecutionContext

class AboutTheTradingHistoryNavigatorSpec extends TestBaseSpec {

  val audit: Audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

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
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection
    )

  val sessionAboutYouIntermittent: Session =
    Session(
      "99996076004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection,
      aboutYouAndTheProperty = AboutYouAndTheProperty(
        renewablesPlant = Some(Intermittent)
      )
    )

  val sessionAboutYouBaseload: Session =
    Session(
      "99996076004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection,
      aboutYouAndTheProperty = AboutYouAndTheProperty(
        renewablesPlant = Some(Baseload)
      )
    )

  val sessionAboutYou6010             = sessionAboutYou.copy(referenceNumber = "99996010004", forType = FOR6010)
  val sessionAboutYou6015             = sessionAboutYou.copy(referenceNumber = "99996015004", forType = FOR6015)
  val sessionAboutYou6020             = sessionAboutYou.copy(referenceNumber = "99996020004", forType = FOR6020)
  val sessionAboutYou6030             = sessionAboutYou.copy(referenceNumber = "99996030004", forType = FOR6030)
  val sessionAboutYou6045             = sessionAboutYou.copy(referenceNumber = "99996045004", forType = FOR6045)
  val sessionAboutYou6048             = sessionAboutYou.copy(referenceNumber = "99996048004", forType = FOR6048)
  val sessionAboutYou6076             = sessionAboutYou.copy(referenceNumber = "99996076004", forType = FOR6076)
  val sessionAboutYouIntermittent6076 =
    sessionAboutYouIntermittent.copy(referenceNumber = "99996076004", forType = FOR6076)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About the trading history navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes financial-year-end page when what you will need has been completed" in {
      navigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController
        .show()
    }

    "return a function that goes financial-year-end page when what you will need has been completed 6010" in {
      navigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6010)
        .apply(sessionAboutYou6010) shouldBe controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController
        .show()
    }

    "return a function that goes vat registerd page when what you will need has been completed" in {
      navigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.AreYouVATRegisteredController.show
    }

    "return a function that goes financial-year-end page when about your trading history has been completed" in {
      navigator
        .nextPage(AboutYourTradingHistoryPageId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
        .show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed 6020" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
        .show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed 6030" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6030)
        .apply(
          sessionAboutYou6030
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
        .show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed 6045" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6045)
        .apply(
          sessionAboutYou6045
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
        .show()
    }

    "return a function that goes the turnover page when financial-year-end has been completed 6076" in {
      navigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
        .show()
    }

    "return a function that goes the total payroll costs page when about your cost of sales has been completed" in {
      navigator
        .nextPage(CostOfSalesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show()
    }

    "return a function that goes the variable operating expenses page when total payroll page has been completed" in {
      navigator
        .nextPage(TotalPayrollCostId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show()
    }

    "return a function that goes the fixed operating expenses page when variable operating expenses has been completed" in {
      navigator
        .nextPage(VariableOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.FixedOperatingExpensesController
        .show()
    }

    "return a function that goes other costs page when fixed operating expenses has been completed" in {
      navigator
        .nextPage(FixedOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.OtherCostsController.show()
    }

    "return a function that goes other costs page when fixed1 operating expenses has been completed" in {
      navigator
        .nextPage(ElectricVehicleChargingPointsId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController
        .show()
    }

    "return a function that goes the non fuel turnover page when total payroll cost has been completed" in {
      navigator
        .nextPage(AcceptLowMarginFuelCardsId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }

    // 6020 specific

    "return a function that goes the bunkered fuel question page when total fuel sold has been completed" in {
      navigator
        .nextPage(TotalFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController
        .show()
    }
    "return a function that goes income expenditure page when other costs has been completed" in {
      navigator
        .nextPage(OtherCostsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController
        .show()
    }
    "return a function that goes bunker fuel card details  page when bunkered fuel sold has been completed" in {
      navigator
        .nextPage(BunkeredFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.BunkerFuelCardDetailsController
        .show(None)
    }
    "return a function that goes the percentage from fuel cards page when customer credit account has been completed" in {
      navigator
        .nextPage(CustomerCreditAccountsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.AcceptLowMarginFuelCardController
        .show()
    }
    "return a function that goes the low margin fuel card details page when percentage from fuel has been completed" in {
      navigator
        .nextPage(PercentageFromFuelCardsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.LowMarginFuelCardDetailsController
        .show()
    }
    "return a function that goes the add another bunker card page when bunker card detail has been completed" in {
      navigator
        .nextPage(BunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }
    "return a function that goes the customer credit account page when add another bunker fuel card has been completed" in {
      navigator
        .nextPage(AddAnotherBunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController
        .show()
    }
    "return a function that goes the non fuel turnover page when add another low margin fuel card has been completed" in {
      navigator
        .nextPage(AddAnotherLowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }
    "return a function that goes the add another low-margin fuel card page when lm fuel card details has been completed" in {
      navigator
        .nextPage(LowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }
    "return edit page with last index when non-empty list exists for bunkered fuel cards" in {
      val cards   = IndexedSeq(
        BunkerFuelCardsDetails(
          BunkerFuelCardDetails("test1", 123.45),
          Some(AnswerYes)
        ),
        BunkerFuelCardsDetails(
          BunkerFuelCardDetails("test2", 456.78),
          Some(AnswerYes)
        )
      )
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(bunkerFuelCardsDetails = Some(cards)))
      )

      navigator
        .nextPage(BunkerFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController
          .show(1)
    }

    "return index 0 when only one card exists for bunkered fuel cards" in {
      val singleCard = IndexedSeq(
        BunkerFuelCardsDetails(BunkerFuelCardDetails("test1", 123.45), Some(AnswerYes))
      )
      val session    = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(bunkerFuelCardsDetails = Some(singleCard)))
      )

      navigator
        .nextPage(BunkerFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }

    "return index 0 when list is empty  for bunkered fuel cards" in {
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(bunkerFuelCardsDetails = Some(IndexedSeq.empty)))
      )

      navigator
        .nextPage(BunkerFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }

    "return edit page with last index when non-empty list exists  for low margin fuel cards " in {
      val cards   = IndexedSeq(
        LowMarginFuelCardsDetails(
          LowMarginFuelCardDetail("test1", 123.45),
          Some(AnswerYes)
        ),
        LowMarginFuelCardsDetails(
          LowMarginFuelCardDetail("test2", 456.78),
          Some(AnswerYes)
        )
      )
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(lowMarginFuelCardsDetails = Some(cards)))
      )

      navigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(1)
    }

    "return index 0 when only one card exists for low margin fuel cards" in {
      val singleCard = IndexedSeq(
        LowMarginFuelCardsDetails(LowMarginFuelCardDetail("test1", 123.45), Some(AnswerYes))
      )
      val session    = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(lowMarginFuelCardsDetails = Some(singleCard)))
      )

      navigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }

    "return index 0 when list is empty for low margin fuel cards" in {
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = Some(AboutTheTradingHistory(lowMarginFuelCardsDetails = Some(IndexedSeq.empty)))
      )

      navigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }

    "return a function that goes the unusual circumstances page when income expenditure has been completed" in {
      navigator
        .nextPage(IncomeExpenditureSummaryId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.UnusualCircumstancesController.show()
    }

    "return a function that goes the correct turnover page when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "return a function that goes the total fuel sold page if the form is 6020 when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "return a function that goes the correct turnover page if the form is 6030 when financial end year has been completed" in {
      navigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6030)
        .apply(
          sessionAboutYou6030
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "return a function that goes the CYA page when turnover page has been completed form 6010" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }
    "return a function that goes the cost of sales  page when turnover page has been completed form 6015" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6015)
        .apply(sessionAboutYou6015) shouldBe controllers.aboutthetradinghistory.routes.CostOfSalesController.show()
    }
    "return a function that goes the EV  page when turnover page has been completed form 6020" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show()
    }
    "return a function that goes the unusual circumstances  page when turnover page has been completed form 6030" in {
      navigator
        .nextPage(TurnoverPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) shouldBe controllers.aboutthetradinghistory.routes.UnusualCircumstancesController
        .show()
    }

    // 6045

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
        .nextPage6045(TentingPitchesOnSiteId, sessionWithNoTentingPitchesOnSite, navigator.cyaPageForTentingPitches)
        .apply(
          sessionWithNoTentingPitchesOnSite
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController
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
        .nextPage6045(TentingPitchesOnSiteId, sessionWithYesTentingPitchesOnSite, navigator.cyaPageForTentingPitches)
        .apply(
          sessionWithYesTentingPitchesOnSite
        ) shouldBe controllers.aboutthetradinghistory.routes.PitchesForCaravansController.show()
    }

    "return a function that goes the cya additional activities page when additional activities on site completed with no" in {
      val sessionWithNoAdditionalActivitiesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            additionalActivities = Some(
              AdditionalActivities(
                additionalActivitiesOnSite = Some(AnswerNo)
              )
            )
          )
        )
      )
      navigator
        .nextPage6045(
          AdditionalActivitiesOnSiteId,
          sessionWithNoAdditionalActivitiesOnSite,
          navigator.cyaPageForAdditionalActivities
        )
        .apply(
          sessionWithNoAdditionalActivitiesOnSite
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController
        .show()
    }

    "return a function that goes the additional activities all year page when additional activities on site completed with yes" in {
      val sessionWithYesAdditionalActivitiesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            additionalActivities = Some(
              AdditionalActivities(
                additionalActivitiesOnSite = Some(AnswerYes)
              )
            )
          )
        )
      )
      navigator
        .nextPage6045(
          AdditionalActivitiesOnSiteId,
          sessionWithYesAdditionalActivitiesOnSite,
          navigator.cyaPageForAdditionalActivities
        )
        .apply(sessionWithYesAdditionalActivitiesOnSite) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalShopsController.show()
    }

    "return a function that goes to other holiday accommodation details page when other holiday accommodation completed with yes" in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            otherHolidayAccommodation = Some(
              OtherHolidayAccommodation(
                otherHolidayAccommodation = Some(AnswerYes)
              )
            )
          )
        )
      )
      navigator
        .nextPage6045(
          OtherHolidayAccommodationId,
          session,
          navigator.cyaPageForAdditionalActivities
        )
        .apply(session) shouldBe
        controllers.aboutthetradinghistory.routes.GrossReceiptsLettingUnitsController.show()
    }

    "return a function that goes to bunkered fuel sold page when benefits given has been completed for yes answer " in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistory = Some(
          sessionAboutYou6045.aboutTheTradingHistory.getOrElse(
            AboutTheTradingHistory(bunkeredFuelQuestion = Some(AnswerYes))
          )
        )
      )
      navigator
        .nextPage(BunkeredFuelQuestionId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.BunkeredFuelSoldController.show()
    }

    "return a function that goes to bunkered fuel sold page when benefits given has been completed for no answer " in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistory = Some(
          sessionAboutYou6045.aboutTheTradingHistory.getOrElse(
            AboutTheTradingHistory(bunkeredFuelQuestion = Some(AnswerNo))
          )
        )
      )
      navigator
        .nextPage(BunkeredFuelQuestionId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show()
    }

    "return a function that goes to other holiday accommodation details page when other holiday accommodation completed with no" in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne = Some(
          AboutTheTradingHistoryPartOne(
            otherHolidayAccommodation = Some(
              OtherHolidayAccommodation(
                otherHolidayAccommodation = Some(AnswerNo)
              )
            )
          )
        )
      )
      navigator
        .nextPage6045(
          OtherHolidayAccommodationId,
          session,
          navigator.cyaPageForAdditionalActivities
        )
        .apply(session) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    }

    "return a function that goes to Pitches For Glamping  page when Pitches For Caravans is completed" in {
      navigator
        .nextPage(PitchesForCaravansId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.PitchesForGlampingController.show()
    }

    "return a function that goes to Rally Areas  page when Pitches For Glamping is completed" in {
      navigator
        .nextPage(PitchesForGlampingId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.RallyAreasController.show()
    }

    "return a function that goes to Tenting Pitches Total  page when Pitches For Rally areas is completed" in {
      navigator
        .nextPage(RallyAreasId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.TentingPitchesTotalController.show()
    }

    "return a function that goes to Tenting Pitches Certificated page when Tenting Pitches Total is completed" in {
      navigator
        .nextPage(TentingPitchesTotalId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.TentingPitchesCertificatedController.show()
    }

    "return a function that goes to CYA Tenting Pitches  page when Tenting Pitches Certificated is completed" in {
      navigator
        .nextPage(TentingPitchesCertificatedId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show()
    }

    "return a function that goes to Task List  page when CYA Tenting Pitches is completed" in {
      navigator
        .nextPage(CheckYourAnswersOtherHolidayAccommodationId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.routes.TaskListController.show().withFragment("tradingHistory")
    }

    "return a function that goes to catering  page when additional activities shops is completed" in {
      navigator
        .nextPage(AdditionalShopsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalCateringController.show()
    }

    "return a function that goes to bars and club  page when additional activities catering is completed" in {
      navigator
        .nextPage(AdditionalCateringId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show()
    }

    "return a function that goes to amusements page when additional activities bars and clubs is completed" in {
      navigator
        .nextPage(AdditionalBarsClubsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show()
    }

    "return a function that goes to misc  page when additional activities amusements is completed" in {
      navigator
        .nextPage(AdditionalAmusementsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalMiscController.show()
    }

    "return a function that goes to CYA page when additional activities misc is completed" in {
      navigator
        .nextPage(AdditionalMiscId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()
    }

    "return a function that goes to task list  page when additional activities cya completed" in {
      navigator
        .nextPage(CheckYourAnswersAdditionalActivitiesId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe controllers.routes.TaskListController
        .show()
        .withFragment("tradingHistory")
    }

    // end of 6045

    "return a function that goes to gross receipts for base load page when  gross receipts excluding vat baseload completed" in {
      navigator
        .nextPage(GrossReceiptsExcludingVatId, sessionAboutYouBaseload)
        .apply(
          sessionAboutYouBaseload
        ) shouldBe controllers.aboutthetradinghistory.routes.GrossReceiptsForBaseLoadController
        .show()
    }

    "return a function that goes to gross receipts for base load page when  gross receipts excluding vat completed" in {
      navigator
        .nextPage(GrossReceiptsExcludingVatId, sessionAboutYouIntermittent6076)
        .apply(
          sessionAboutYouIntermittent6076
        ) shouldBe controllers.aboutthetradinghistory.routes.OtherIncomeController
        .show()
    }

    "return a function that goes to cost of sales 6076 page when  other income completed" in {
      navigator
        .nextPage(OtherIncomeId, sessionAboutYouIntermittent6076)
        .apply(
          sessionAboutYouIntermittent6076
        ) shouldBe controllers.aboutthetradinghistory.routes.CostOfSales6076IntermittentController.show()
    }

    "return a function that goes to cost of sales 6076 baseload page when  other income completed" in {
      navigator
        .nextPage(OtherIncomeId, sessionAboutYouBaseload)
        .apply(
          sessionAboutYouBaseload
        ) shouldBe controllers.aboutthetradinghistory.routes.CostOfSales6076Controller.show()
    }

    "return a function that goes to staff costs page when  cost of sales 6076 completed" in {
      navigator
        .nextPage(CostOfSales6076Id, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.StaffCostsController.show()
    }

    "return a function that goes to staff costs page when cost of sales 6076 intermittent completed" in {
      navigator
        .nextPage(CostOfSales6076IntermittentId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.StaffCostsController.show()
    }

    "return a function that goes to premises  costs page when  staff costs completed" in {
      navigator
        .nextPage(StaffCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.PremisesCostsController.show()
    }

    "return a function that goes to operational expenses page when  premises  costs completed" in {
      navigator
        .nextPage(PremisesCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.OperationalExpensesController
        .show()
    }
    "return a function that goes to other income page when operational cost completed" in {
      navigator
        .nextPage(GrossReceiptsForBaseLoadId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.OtherIncomeController.show()
    }

    "return a function that goes to total fuel page when financial years 6020 completed" in {
      navigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show()
    }

    "return a function that goes to total fuel page when financial years 6030 completed" in {
      navigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) shouldBe controllers.aboutthetradinghistory.routes.Turnover6030Controller.show()
    }

    "return a function that goes to total fuel page when financial years 6076 completed" in {
      navigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.ElectricityGeneratedController
        .show()
    }

    "return a function that goes to total fuel page when financial years 6010 completed" in {
      navigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.TurnoverController.show()
    }

    "return a function that goes to expenditure page when head office expenses completed" in {
      navigator
        .nextPage(HeadOfficeExpensesId, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show()
    }

    "return a function that goes to CYA, when income expenditure finished" in {
      navigator
        .nextPage(IncomeExpenditureSummary6076Id, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

    "return a function that goes to financial year end page when are you vat registered page completed" in {
      navigator
        .nextPage(AreYouVATRegisteredId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show()
    }

    "return a function that goes to twin unit caravans owned by operator page when single caravans age page completed" in {
      navigator
        .nextPage(SingleCaravansAgeCategoriesId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.TwinUnitCaravansOwnedByOperatorController.show()
    }

    "return a function that goes to CYA Other Holiday page when total site capacity page completed" in {
      navigator
        .nextPage(TotalSiteCapacityId, sessionAboutYou6045)
        .apply(
          sessionAboutYou6045
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    }

    "return a function that goes to TaskList trading history, when CYA for tenting pitches ready " in {
      navigator
        .nextPage(CheckYourAnswersTentingPitchesId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe controllers.routes.TaskListController
        .show()
        .withFragment("tradingHistory")
    }
    "financialYearEndRouting is called" should {

      "redirect to CheckYourAnswersAccountingInfoController for FOR6010 when financialYearEndHasChanged is false" in {
        val session = sessionAboutYou6010.copy(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory))
        navigator
          .nextPage(FinancialYearEndPageId, session)
          .apply(
            session
          ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }

      "redirect to /warning-check-accounting-info for FOR6020" in {
        val session = sessionAboutYou6020.copy(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6020))
        navigator
          .nextPage(FinancialYearEndPageId, session)
          .apply(
            session
          ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }

      "redirect to /warning-check-accounting-info for FOR6030" in {
        val session = sessionAboutYou6030.copy(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030))
        navigator
          .nextPage(FinancialYearEndPageId, session)
          .apply(
            session
          ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }

      "redirect to CheckYourAnswersAccountingInfoController for FOR6045" in {
        val session = sessionAboutYou6045.copy(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
          aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
        )
        navigator
          .nextPage(FinancialYearEndPageId, session)
          .apply(
            session
          ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }

      "redirect to /warning-check-accounting-info for FOR6076" in {
        val session = sessionAboutYou6076.copy(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
          aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
        )
        navigator
          .nextPage(FinancialYearEndPageId, session)
          .apply(
            session
          ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }

    }
  }
}
