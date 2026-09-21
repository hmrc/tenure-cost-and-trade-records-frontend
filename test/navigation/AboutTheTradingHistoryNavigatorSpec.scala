/*
 * Copyright 2026 HM Revenue & Customs
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

import models.ForType.*
import models.Session
import models.submissions.aboutthetradinghistory.*
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.RenewablesPlantType.*
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.AddressConnectionType.*
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutTheTradingHistoryNavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val stillConnectedDetailsYes: Option[StillConnectedDetails] =
    StillConnectedDetails(AddressConnectionTypeYes)

  private val removeConnection: Option[RemoveConnectionDetails] =
    RemoveConnectionDetails(
      RemoveConnectionsDetails(
        "John Smith",
        ContactDetails("12345678909", "test@email.com"),
        "Additional Information is here"
      )
    )

  private val sessionAboutYou: Session =
    Session(
      "99996010004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection
    )

  private val sessionAboutYouIntermittent: Session =
    Session(
      "99996076004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection,
      aboutYouAndTheProperty = AboutYouAndTheProperty(
        renewablesPlant = Intermittent
      )
    )

  private val sessionAboutYouBaseload: Session =
    Session(
      "99996076004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection,
      aboutYouAndTheProperty = AboutYouAndTheProperty(
        renewablesPlant = Baseload
      )
    )

  private val sessionAboutYou6010 = sessionAboutYou.copy(referenceNumber = "99996010004", forType = FOR6010)
  private val sessionAboutYou6015 = sessionAboutYou.copy(referenceNumber = "99996015004", forType = FOR6015)
  private val sessionAboutYou6020 = sessionAboutYou.copy(referenceNumber = "99996020004", forType = FOR6020)
  private val sessionAboutYou6030 = sessionAboutYou.copy(referenceNumber = "99996030004", forType = FOR6030)
  private val sessionAboutYou6045 = sessionAboutYou.copy(referenceNumber = "99996045004", forType = FOR6045)
  private val sessionAboutYou6048 = sessionAboutYou.copy(referenceNumber = "99996048004", forType = FOR6048)
  private val sessionAboutYou6076 = sessionAboutYou.copy(referenceNumber = "99996076004", forType = FOR6076)

  private val sessionAboutYouIntermittent6076 = sessionAboutYouIntermittent.copy(referenceNumber = "99996076004", forType = FOR6076)

  "Trading history navigator for 6010" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYourTradingHistoryNavigator
        .nextPage(UnknownIdentifier, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.routes.LoginController.show
    }

    "redirect to financial-year-end page when what you will need has been completed 6010" in {
      aboutYourTradingHistoryNavigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6010)
        .apply(sessionAboutYou6010) shouldBe
        controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController
          .show()
    }

    "redirect to financial-year-end page when about your trading history has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AboutYourTradingHistoryPageId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show()
    }

    "redirect to the turnover page when financial-year-end has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
          .show()
    }

    "redirect to the total payroll costs page when about your cost of sales has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CostOfSalesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show()
    }

    "redirect to the variable operating expenses page when total payroll page has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TotalPayrollCostId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show()
    }

    "redirect to the fixed operating expenses page when variable operating expenses has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(VariableOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe
        controllers.aboutthetradinghistory.routes.FixedOperatingExpensesController
          .show()
    }

    "redirect to other costs page when fixed operating expenses has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FixedOperatingExpensesId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.OtherCostsController.show()
    }

    "redirect to other costs page when fixed1 operating expenses has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(ElectricVehicleChargingPointsId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController
          .show()
    }

    "redirect to the non fuel turnover page when total payroll cost has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AcceptLowMarginFuelCardsId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }

    "redirect to the unusual circumstances page when income expenditure has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(IncomeExpenditureSummaryId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.UnusualCircumstancesController.show()
    }

    "redirect to the correct turnover page when financial end year has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to the CYA page when turnover page has been completed form 6010" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TurnoverPageId, sessionAboutYou)
        .apply(
          sessionAboutYou
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

    "redirect to to gross receipts for base load page when  gross receipts excluding vat baseload completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(GrossReceiptsExcludingVatId, sessionAboutYouBaseload)
        .apply(
          sessionAboutYouBaseload
        ) shouldBe
        controllers.aboutthetradinghistory.routes.GrossReceiptsForBaseLoadController
          .show()
    }

    "redirect to to total fuel page when financial years 6010 completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou)
        .apply(sessionAboutYou) shouldBe controllers.aboutthetradinghistory.routes.TurnoverController.show()
    }
  }

  "Trading history navigator for 6015" should {
    "redirect to the cost of sales  page when turnover page has been completed form 6015" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TurnoverPageId, sessionAboutYou6015)
        .apply(sessionAboutYou6015) shouldBe controllers.aboutthetradinghistory.routes.CostOfSalesController.show()
    }
  }

  "Trading history navigator for 6020" should {
    "redirect to the turnover page when financial-year-end has been completed 6020" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
          .show()
    }

    "redirect to the bunkered fuel question page when total fuel sold has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TotalFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe
        controllers.aboutthetradinghistory.routes.BunkeredFuelQuestionController
          .show()
    }

    "redirect to income expenditure page when other costs has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(OtherCostsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController
          .show()
    }

    "redirect to bunker fuel card details  page when bunkered fuel sold has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(BunkeredFuelSoldId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe
        controllers.aboutthetradinghistory.routes.BunkerFuelCardDetailsController
          .show(None)
    }

    "redirect to the percentage from fuel cards page when customer credit account has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CustomerCreditAccountsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe
        controllers.aboutthetradinghistory.routes.AcceptLowMarginFuelCardController
          .show()
    }

    "redirect to the low margin fuel card details page when percentage from fuel has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(PercentageFromFuelCardsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe
        controllers.aboutthetradinghistory.routes.LowMarginFuelCardDetailsController
          .show()
    }

    "redirect to the add another bunker card page when bunker card detail has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(BunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }

    "redirect to the customer credit account page when add another bunker fuel card has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AddAnotherBunkerFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe
        controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController
          .show()
    }

    "redirect to the non fuel turnover page when add another low margin fuel card has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AddAnotherLowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }

    "redirect to the add another low-margin fuel card page when lm fuel card details has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(LowMarginFuelCardsDetailsId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }

    "return edit page with last index when non-empty list exists for bunkered fuel cards" in {
      val cards   = IndexedSeq(
        BunkerFuelCardsDetails(
          BunkerFuelCardDetails("test1", 123.45),
          AnswerYes
        ),
        BunkerFuelCardsDetails(
          BunkerFuelCardDetails("test2", 456.78),
          AnswerYes
        )
      )
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(bunkerFuelCardsDetails = cards)
      )

      aboutYourTradingHistoryNavigator
        .nextPage(BunkerFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController
          .show(1)
    }

    "return index 0 when only one card exists for bunkered fuel cards" in {
      val singleCard = IndexedSeq(
        BunkerFuelCardsDetails(BunkerFuelCardDetails("test1", 123.45), AnswerYes)
      )
      val session    = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(bunkerFuelCardsDetails = singleCard)
      )

      aboutYourTradingHistoryNavigator
        .nextPage(BunkerFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
    }

    "return index 0 when list is empty  for bunkered fuel cards" in {
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(bunkerFuelCardsDetails = IndexedSeq.empty)
      )

      aboutYourTradingHistoryNavigator
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
          AnswerYes
        ),
        LowMarginFuelCardsDetails(
          LowMarginFuelCardDetail("test2", 456.78),
          AnswerYes
        )
      )
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(lowMarginFuelCardsDetails = cards)
      )

      aboutYourTradingHistoryNavigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(1)
    }

    "return index 0 when only one card exists for low margin fuel cards" in {
      val singleCard = IndexedSeq(
        LowMarginFuelCardsDetails(LowMarginFuelCardDetail("test1", 123.45), AnswerYes)
      )
      val session    = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(lowMarginFuelCardsDetails = singleCard)
      )

      aboutYourTradingHistoryNavigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }

    "return index 0 when list is empty for low margin fuel cards" in {
      val session = sessionAboutYou6020.copy(
        aboutTheTradingHistory = AboutTheTradingHistory(lowMarginFuelCardsDetails = IndexedSeq.empty)
      )

      aboutYourTradingHistoryNavigator
        .nextPage(LowMarginFuelCardsDetailsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
    }

    "redirect to the total fuel sold page if the form is 6020 when financial end year has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to the EV  page when turnover page has been completed form 6020" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TurnoverPageId, sessionAboutYou6020)
        .apply(
          sessionAboutYou6020
        ) shouldBe controllers.aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show()
    }

    "redirect to to total fuel page when financial years 6020 completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6020)
        .apply(sessionAboutYou6020) shouldBe controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show()
    }
  }

  "Trading history navigator for 6030" should {
    "redirect to the turnover page when financial-year-end has been completed 6030" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6030)
        .apply(
          sessionAboutYou6030
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
          .show()
    }

    "redirect to the correct turnover page if the form is 6030 when financial end year has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndDatesPageId, sessionAboutYou6030)
        .apply(
          sessionAboutYou6030
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to the unusual circumstances  page when turnover page has been completed form 6030" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TurnoverPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) shouldBe
        controllers.aboutthetradinghistory.routes.UnusualCircumstancesController
          .show()
    }

    "redirect to to total fuel page when financial years 6030 completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6030)
        .apply(sessionAboutYou6030) shouldBe controllers.aboutthetradinghistory.routes.Turnover6030Controller.show()
    }
  }

  "Trading history navigator for 6045" should {
    "redirect to the turnover page when financial-year-end has been completed 6045" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6045)
        .apply(
          sessionAboutYou6045
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
          .show()
    }

    "redirect to the cya tenting pitches page when tenting pitches on site completed with no" in {
      val sessionWithNoTentingPitchesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            touringAndTentingPitches =
              TouringAndTentingPitches(
                tentingPitchesOnSite = AnswerNo
              )
          )
      )

      aboutYourTradingHistoryNavigator
        .nextPage6045(TentingPitchesOnSiteId, sessionWithNoTentingPitchesOnSite, aboutYourTradingHistoryNavigator.cyaPageForTentingPitches)
        .apply(
          sessionWithNoTentingPitchesOnSite
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController
          .show()
    }

    "redirect to the tenting pitches all year page when tenting pitches on site completed with yes" in {
      val sessionWithYesTentingPitchesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            touringAndTentingPitches =
              TouringAndTentingPitches(
                tentingPitchesOnSite = AnswerYes
              )
          )
      )

      aboutYourTradingHistoryNavigator
        .nextPage6045(TentingPitchesOnSiteId, sessionWithYesTentingPitchesOnSite, aboutYourTradingHistoryNavigator.cyaPageForTentingPitches)
        .apply(
          sessionWithYesTentingPitchesOnSite
        ) shouldBe controllers.aboutthetradinghistory.routes.PitchesForCaravansController.show()
    }

    "redirect to the cya additional activities page when additional activities on site completed with no" in {
      val sessionWithNoAdditionalActivitiesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            additionalActivities =
              AdditionalActivities(
                additionalActivitiesOnSite = AnswerNo
              )
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage6045(
          AdditionalActivitiesOnSiteId,
          sessionWithNoAdditionalActivitiesOnSite,
          aboutYourTradingHistoryNavigator.cyaPageForAdditionalActivities
        )
        .apply(
          sessionWithNoAdditionalActivitiesOnSite
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController
          .show()
    }

    "redirect to the additional activities all year page when additional activities on site completed with yes" in {
      val sessionWithYesAdditionalActivitiesOnSite = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            additionalActivities =
              AdditionalActivities(
                additionalActivitiesOnSite = AnswerYes
              )
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage6045(
          AdditionalActivitiesOnSiteId,
          sessionWithYesAdditionalActivitiesOnSite,
          aboutYourTradingHistoryNavigator.cyaPageForAdditionalActivities
        )
        .apply(sessionWithYesAdditionalActivitiesOnSite) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalShopsController.show()
    }

    "redirect to to other holiday accommodation details page when other holiday accommodation completed with yes" in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            otherHolidayAccommodation =
              OtherHolidayAccommodation(
                otherHolidayAccommodation = AnswerYes
              )
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage6045(
          OtherHolidayAccommodationId,
          session,
          aboutYourTradingHistoryNavigator.cyaPageForAdditionalActivities
        )
        .apply(session) shouldBe
        controllers.aboutthetradinghistory.routes.GrossReceiptsLettingUnitsController.show()
    }

    "redirect to to bunkered fuel sold page when benefits given has been completed for yes answer " in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistory =
          sessionAboutYou6045.aboutTheTradingHistory.getOrElse(
            AboutTheTradingHistory(bunkeredFuelQuestion = AnswerYes)
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage(BunkeredFuelQuestionId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.BunkeredFuelSoldController.show()
    }

    "redirect to to bunkered fuel sold page when benefits given has been completed for no answer " in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistory =
          sessionAboutYou6045.aboutTheTradingHistory.getOrElse(
            AboutTheTradingHistory(bunkeredFuelQuestion = AnswerNo)
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage(BunkeredFuelQuestionId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show()
    }

    "redirect to to other holiday accommodation details page when other holiday accommodation completed with no" in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistoryPartOne =
          AboutTheTradingHistoryPartOne(
            otherHolidayAccommodation =
              OtherHolidayAccommodation(
                otherHolidayAccommodation = AnswerNo
              )
          )
      )
      aboutYourTradingHistoryNavigator
        .nextPage6045(
          OtherHolidayAccommodationId,
          session,
          aboutYourTradingHistoryNavigator.cyaPageForAdditionalActivities
        )
        .apply(session) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    }

    "redirect to to Pitches For Glamping  page when Pitches For Caravans is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(PitchesForCaravansId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.PitchesForGlampingController.show()
    }

    "redirect to to Rally Areas  page when Pitches For Glamping is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(PitchesForGlampingId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.RallyAreasController.show()
    }

    "redirect to to Tenting Pitches Total  page when Pitches For Rally areas is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(RallyAreasId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.TentingPitchesTotalController.show()
    }

    "redirect to to Tenting Pitches Certificated page when Tenting Pitches Total is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TentingPitchesTotalId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.TentingPitchesCertificatedController.show()
    }

    "redirect to to CYA Tenting Pitches  page when Tenting Pitches Certificated is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TentingPitchesCertificatedId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show()
    }

    "redirect to to Task List  page when CYA Tenting Pitches is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersOtherHolidayAccommodationId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.routes.TaskListController.show.withFragment("tradingHistory")
    }

    "redirect to to catering  page when additional activities shops is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AdditionalShopsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalCateringController.show()
    }

    "redirect to to bars and club  page when additional activities catering is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AdditionalCateringId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show()
    }

    "redirect to to amusements page when additional activities bars and clubs is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AdditionalBarsClubsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show()
    }

    "redirect to to misc  page when additional activities amusements is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AdditionalAmusementsId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.AdditionalMiscController.show()
    }

    "redirect to to CYA page when additional activities misc is completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AdditionalMiscId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()
    }

    "redirect to to task list  page when additional activities cya completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersAdditionalActivitiesId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe controllers.routes.TaskListController.show.withFragment("tradingHistory")
    }

    "redirect to to CYA Other Holiday page when total site capacity page completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(TotalSiteCapacityId, sessionAboutYou6045)
        .apply(
          sessionAboutYou6045
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    }

    "redirect to to TaskList trading history, when CYA for tenting pitches ready " in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersTentingPitchesId, sessionAboutYou6045)
        .apply(sessionAboutYou6045) shouldBe controllers.routes.TaskListController.show.withFragment("tradingHistory")
    }
  }

  "Trading history navigator for 6048" should {
    "redirect to vat registered page when what you will need has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.AreYouVATRegisteredController.show
    }

    "redirect to to financial year end page when are you vat registered page completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(AreYouVATRegisteredId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.FinancialYearEndController.show()
    }

    "redirect to to twin unit caravans owned by operator page when single caravans age page completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(SingleCaravansAgeCategoriesId, sessionAboutYou6048)
        .apply(
          sessionAboutYou6048
        ) shouldBe controllers.aboutthetradinghistory.routes.TwinUnitCaravansOwnedByOperatorController.show()
    }
  }

  "Trading history navigator for 6076" should {
    "redirect to financial-year-end page when what you will need has been completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(WhatYouWillNeedPageId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe
        controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController
          .show()
    }

    "redirect to the turnover page when financial-year-end has been completed 6076" in {
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe
        controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
          .show()
    }

    "redirect to to gross receipts for base load page when  gross receipts excluding vat completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(GrossReceiptsExcludingVatId, sessionAboutYouIntermittent6076)
        .apply(
          sessionAboutYouIntermittent6076
        ) shouldBe
        controllers.aboutthetradinghistory.routes.OtherIncomeController
          .show()
    }

    "redirect to to cost of sales 6076 page when  other income completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(OtherIncomeId, sessionAboutYouIntermittent6076)
        .apply(
          sessionAboutYouIntermittent6076
        ) shouldBe controllers.aboutthetradinghistory.routes.CostOfSales6076IntermittentController.show()
    }

    "redirect to to cost of sales 6076 baseload page when  other income completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(OtherIncomeId, sessionAboutYouBaseload)
        .apply(
          sessionAboutYouBaseload
        ) shouldBe controllers.aboutthetradinghistory.routes.CostOfSales6076Controller.show()
    }

    "redirect to to staff costs page when  cost of sales 6076 completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CostOfSales6076Id, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.StaffCostsController.show()
    }

    "redirect to to staff costs page when cost of sales 6076 intermittent completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CostOfSales6076IntermittentId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.StaffCostsController.show()
    }

    "redirect to to premises  costs page when  staff costs completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(StaffCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.PremisesCostsController.show()
    }

    "redirect to to operational expenses page when  premises  costs completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(PremisesCostsId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe
        controllers.aboutthetradinghistory.routes.OperationalExpensesController
          .show()
    }

    "redirect to to other income page when operational cost completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(GrossReceiptsForBaseLoadId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe controllers.aboutthetradinghistory.routes.OtherIncomeController.show()
    }

    "redirect to to total fuel page when financial years 6076 completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(CheckYourAnswersAccountingInfoPageId, sessionAboutYou6076)
        .apply(sessionAboutYou6076) shouldBe
        controllers.aboutthetradinghistory.routes.ElectricityGeneratedController
          .show()
    }

    "redirect to to expenditure page when head office expenses completed" in {
      aboutYourTradingHistoryNavigator
        .nextPage(HeadOfficeExpensesId, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show()
    }

    "redirect to to CYA, when income expenditure finished" in {
      aboutYourTradingHistoryNavigator
        .nextPage(IncomeExpenditureSummary6076Id, sessionAboutYou6076)
        .apply(
          sessionAboutYou6076
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }
  }

  "financialYearEndRouting is called" should {
    "redirect to CheckYourAnswersAccountingInfoController for FOR6010 when financialYearEndHasChanged is false" in {
      val session = sessionAboutYou6010.copy(aboutTheTradingHistory = prefilledAboutYourTradingHistory)
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to /warning-check-accounting-info for FOR6020" in {
      val session = sessionAboutYou6020.copy(aboutTheTradingHistory = prefilledAboutYourTradingHistory6020)
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to /warning-check-accounting-info for FOR6030" in {
      val session = sessionAboutYou6030.copy(aboutTheTradingHistory = prefilledAboutYourTradingHistory6030)
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to CheckYourAnswersAccountingInfoController for FOR6045" in {
      val session = sessionAboutYou6045.copy(
        aboutTheTradingHistory = prefilledAboutYourTradingHistory6045,
        aboutTheTradingHistoryPartOne = prefilledTurnoverSections6045
      )
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }

    "redirect to /warning-check-accounting-info for FOR6076" in {
      val session = sessionAboutYou6076.copy(
        aboutTheTradingHistory = prefilledAboutYourTradingHistory6076,
        aboutTheTradingHistoryPartOne = prefilledTurnoverSections6076
      )
      aboutYourTradingHistoryNavigator
        .nextPage(FinancialYearEndPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
    }
  }
