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
import controllers.aboutthetradinghistory
import controllers.aboutthetradinghistory.routes
import models.submissions.common.{AnswerNo, AnswerYes}
import models.ForType.*
import models.Session
import navigation.identifiers.*
import play.api.Logging
import play.api.mvc.{AnyContent, Call, Request}
import uk.gov.hmrc.http.HeaderCarrier
import controllers.toOpt

import javax.inject.Inject

class AboutTheTradingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show())

  def cyaPageForTentingPitches: Call = aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show()

  def cyaPageForOtherHolidayAccommodation: Call =
    aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()

  def cyaPageForAdditionalActivities: Call =
    aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()

  def nextPage6045(id: Identifier, session: Session, call: Call)(implicit
    hc: HeaderCarrier,
    request: Request[AnyContent]
  ): Session => Call =
    if (from == "CYA") { _ =>
      call
    } else {
      nextWithoutRedirectToCYA(id, session)
    }

  override def nextPage(id: Identifier, session: Session)(implicit
    hc: HeaderCarrier,
    request: Request[AnyContent]
  ): Session => Call =
    if (from == "IES") {
      _.forType match {
        case FOR6076 => ies6076SpecificRoute
        case _       => iesSpecificRoute
      }
    } else {
      super.nextPage(id, session)
    }

  private def iesSpecificRoute: Call =
    routes.IncomeExpenditureSummaryController.show()

  private def ies6076SpecificRoute: Call =
    routes.IncomeExpenditureSummary6076Controller.show()

  override val postponeCYARedirectPages: Set[String] = Set(
    aboutthetradinghistory.routes.FinancialYearEndController.show(),
    aboutthetradinghistory.routes.FinancialYearEndDatesController.show(),
    aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show(),
    aboutthetradinghistory.routes.ElectricityGeneratedController.show(),
    aboutthetradinghistory.routes.BunkeredFuelSoldController.show(),
    aboutthetradinghistory.routes.BunkerFuelCardDetailsController.show(),
    aboutthetradinghistory.routes.PercentageFromFuelCardsController.show(),
    aboutthetradinghistory.routes.TurnoverController.show(),
    aboutthetradinghistory.routes.CostOfSalesController.show()
  ).map(_.url)

  private def financialYearEndRouting: Session => Call = { s =>
    s.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation.flatMap(_.yearEndChanged)) match {
      case Some(true) =>
        s.forType match {
          case FOR6020 | FOR6045 | FOR6046 | FOR6048 | FOR6076 =>
            aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show()
          case _                                               => aboutthetradinghistory.routes.FinancialYearEndDatesController.show()
        }

      case _ =>
        s.forType match {
          case FOR6020                     => aboutthetradinghistory.routes.TotalFuelSoldController.show()
          case FOR6030                     => aboutthetradinghistory.routes.Turnover6030Controller.show()
          case FOR6045 | FOR6046 | FOR6048 => aboutthetradinghistory.routes.FinancialYearsController.show
          case FOR6076                     => aboutthetradinghistory.routes.ElectricityGeneratedController.show()
          case _                           => aboutthetradinghistory.routes.TurnoverController.show()
        }
    }
  }

  private def bunkeredFuelQuestionRouting: Session => Call = answers =>
    answers.aboutTheTradingHistory.flatMap(_.bunkeredFuelQuestion.map(_.bunkeredFuelQuestion)) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.BunkeredFuelSoldController.show()
      case Some(AnswerNo)  => aboutthetradinghistory.routes.CustomerCreditAccountsController.show()
      case _               =>
        logger.warn(
          s"Navigation for bunkered fuel question reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for bunkered fuel question")
    }

  private def financialYearEndDatesRouting: Session => Call =
    _.forType match {
      case FOR6020                     => aboutthetradinghistory.routes.TotalFuelSoldController.show()
      case FOR6030                     => aboutthetradinghistory.routes.Turnover6030Controller.show()
      case FOR6045 | FOR6046 | FOR6048 => aboutthetradinghistory.routes.FinancialYearsController.show
      case FOR6076                     => aboutthetradinghistory.routes.ElectricityGeneratedController.show()
      case _                           => aboutthetradinghistory.routes.TurnoverController.show()
    }

  private def financialYearsRouting: Session => Call =
    _.forType match {
      case FOR6020           => aboutthetradinghistory.routes.TotalFuelSoldController.show()
      case FOR6030           => aboutthetradinghistory.routes.Turnover6030Controller.show()
      case FOR6045 | FOR6046 => aboutthetradinghistory.routes.StaticCaravansController.show()
      case FOR6048           => aboutthetradinghistory.routes.Income6048Controller.show
      case FOR6076           => aboutthetradinghistory.routes.ElectricityGeneratedController.show()
      case _                 => aboutthetradinghistory.routes.TurnoverController.show()
    }

  private def turnoverRouting: Session => Call =
    _.forType match {
      case FOR6015 => aboutthetradinghistory.routes.CostOfSalesController.show()
      case FOR6020 => aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show()
      case FOR6030 => aboutthetradinghistory.routes.UnusualCircumstancesController.show()
      case _       => aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

  private def getAddAnotherBunkerFuelCardsDetailRouting(answers: Session): Call = {
    val currentIndex: Option[Int] = answers.aboutTheTradingHistory.flatMap(_.bunkerFuelCardsDetails) match {
      case Some(details) if details.nonEmpty => Some(details.size - 1) // Assuming the last entry is the current one
      case _                                 => None
    }
    currentIndex match {
      case Some(idx) =>
        aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(
          idx
        ) // Assuming there's a 'show' method to edit
      case None      =>
        aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0) // Fallback or start new
    }
  }

  private def acceptLowMarginFuelCardsRouting: Session => Call =
    _.aboutTheTradingHistory.flatMap(_.doYouAcceptLowMarginFuelCard) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.PercentageFromFuelCardsController.show()
      case _               => aboutthetradinghistory.routes.NonFuelTurnoverController.show()
    }

  private def staticCaravansRouting: Session => Call =
    _.aboutTheTradingHistoryPartOne.flatMap(_.caravans).flatMap(_.anyStaticLeisureCaravansOnSite) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.CaravansOpenAllYearController.show()
      case _               => aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

  private def getAddAnotherLowMarginFuelCardsDetailRouting(answers: Session): Call = {
    val currentIndex: Option[Int] = answers.aboutTheTradingHistory.flatMap(_.lowMarginFuelCardsDetails) match {
      case Some(details) if details.nonEmpty => Some(details.size - 1) // Assuming the last entry is the current one
      case _                                 => None
    }
    currentIndex match {
      case Some(idx) =>
        aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(
          idx
        ) // Assuming there's a 'show' method to edit
      case None      =>
        aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0) // Fallback or start new
    }
  }

  private def otherHolidayAccommodationRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.otherHolidayAccommodation.flatMap(_.otherHolidayAccommodation)
    ) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.OtherHolidayAccommodationDetailsController.show()
      case Some(AnswerNo)  => aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
      case _               =>
        logger.warn(
          s"Navigation for other holiday accommodation reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for other holiday accommodation")
    }

  private def tentingPitchesOnSiteRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.touringAndTentingPitches.flatMap(_.tentingPitchesOnSite)
    ) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.TentingPitchesAllYearController.show()
      case Some(AnswerNo)  => aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show()
      case _               =>
        logger.warn(
          s"Navigation for tenting pitches on site reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for tenting pitches all year")
    }

  private def additionalActivitiesOnSiteRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.additionalActivities.flatMap(_.additionalActivitiesOnSite)
    ) match {
      case Some(AnswerYes) => aboutthetradinghistory.routes.AdditionalActivitiesAllYearController.show()
      case Some(AnswerNo)  => aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()
      case _               =>
        logger.warn(
          s"Navigation for additional activities on site reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for additional activities all year")
    }

  private def intermittentRouting: Session => Call = answers =>
    val intermittent = answers.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))
    intermittent match {
      case Some("intermittent") => aboutthetradinghistory.routes.CostOfSales6076IntermittentController.show()
      case Some("baseload")     => aboutthetradinghistory.routes.CostOfSales6076Controller.show()
      case _                    =>
        logger.warn(
          s"Navigation for intermittent routing reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for intermittent routing")
    }

  private def grossReceiptsRouting: Session => Call = answers =>
    val intermittent = answers.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))
    intermittent match {
      case Some("intermittent") => aboutthetradinghistory.routes.OtherIncomeController.show()
      case Some("baseload")     => aboutthetradinghistory.routes.GrossReceiptsForBaseLoadController.show()
      case _                    =>
        logger.warn(
          s"Navigation for gross receipts reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for gross receipts all year")
    }

  private def whatYouWillNeedRouting: Session => Call =
    _.forType match {
      case FOR6048 => aboutthetradinghistory.routes.AreYouVATRegisteredController.show
      case _       => aboutthetradinghistory.routes.AboutYourTradingHistoryController.show()
    }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYourTradingHistoryPageId               -> (_ => aboutthetradinghistory.routes.FinancialYearEndController.show()),
    FinancialYearEndPageId                      -> financialYearEndRouting,
    FinancialYearEndDatesPageId                 -> financialYearEndDatesRouting,
    FinancialYearsPageId                        -> financialYearsRouting,
    TurnoverPageId                              -> turnoverRouting,
    CostOfSalesId                               -> (_ => aboutthetradinghistory.routes.TotalPayrollCostsController.show()),
    TotalPayrollCostId                          -> (_ => aboutthetradinghistory.routes.VariableOperatingExpensesController.show()),
    TotalFuelSoldId                             -> (_ => aboutthetradinghistory.routes.BunkeredFuelQuestionController.show()),
    VariableOperatingExpensesId                 -> (_ => aboutthetradinghistory.routes.FixedOperatingExpensesController.show()),
    FixedOperatingExpensesId                    -> (_ => aboutthetradinghistory.routes.OtherCostsController.show()),
    OtherCostsId                                -> (_ => aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show()),
    BunkeredFuelQuestionId                      -> bunkeredFuelQuestionRouting,
    BunkeredFuelSoldId                          -> (_ => aboutthetradinghistory.routes.BunkerFuelCardDetailsController.show(None)),
    CustomerCreditAccountsId                    -> (_ => aboutthetradinghistory.routes.AcceptLowMarginFuelCardController.show()),
    PercentageFromFuelCardsId                   -> (_ => aboutthetradinghistory.routes.LowMarginFuelCardDetailsController.show()),
    BunkerFuelCardsDetailsId                    -> getAddAnotherBunkerFuelCardsDetailRouting,
    AddAnotherBunkerFuelCardsDetailsId          -> (_ => aboutthetradinghistory.routes.CustomerCreditAccountsController.show()),
    AcceptLowMarginFuelCardsId                  -> acceptLowMarginFuelCardsRouting,
    AddAnotherLowMarginFuelCardsDetailsId       -> (_ => aboutthetradinghistory.routes.NonFuelTurnoverController.show()),
    LowMarginFuelCardsDetailsId                 -> getAddAnotherLowMarginFuelCardsDetailRouting,
    IncomeExpenditureSummaryId                  -> (_ => aboutthetradinghistory.routes.UnusualCircumstancesController.show()),
    IncomeExpenditureSummary6076Id              -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    ),
    UnusualCircumstancesId                      -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    ),
    ElectricVehicleChargingPointsId             -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    ),
    ElectricityGeneratedId                      -> (_ => aboutthetradinghistory.routes.GrossReceiptsExcludingVATController.show()),
    GrossReceiptsExcludingVatId                 -> grossReceiptsRouting,
    OtherIncomeId                               -> intermittentRouting,
    CostOfSales6076IntermittentId               -> (_ => aboutthetradinghistory.routes.StaffCostsController.show()),
    CostOfSales6076Id                           -> (_ => aboutthetradinghistory.routes.StaffCostsController.show()),
    StaffCostsId                                -> (_ => aboutthetradinghistory.routes.PremisesCostsController.show()),
    PremisesCostsId                             -> (_ => aboutthetradinghistory.routes.OperationalExpensesController.show()),
    GrossReceiptsForBaseLoadId                  -> (_ => aboutthetradinghistory.routes.OtherIncomeController.show()),
    OperationalExpensesId                       -> (_ => aboutthetradinghistory.routes.HeadOfficeExpensesController.show()),
    HeadOfficeExpensesId                        -> (_ => aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show()),
    StaticCaravansId                            -> staticCaravansRouting,
    CaravansOpenAllYearId                       -> (_ => aboutthetradinghistory.routes.GrossReceiptsCaravanFleetHireController.show()),
    GrossReceiptsCaravanFleetHireId             -> (_ =>
      aboutthetradinghistory.routes.SingleCaravansOwnedByOperatorController.show()
    ),
    SingleCaravansOwnedByOperatorId             -> (_ => aboutthetradinghistory.routes.SingleCaravansSubletController.show()),
    SingleCaravansSubletId                      -> (_ => aboutthetradinghistory.routes.SingleCaravansAgeCategoriesController.show()),
    SingleCaravansAgeCategoriesId               -> (_ =>
      aboutthetradinghistory.routes.TwinUnitCaravansOwnedByOperatorController.show()
    ),
    TwinCaravansOwnedByOperatorId               -> (_ => aboutthetradinghistory.routes.TwinUnitCaravansSubletController.show()),
    TwinCaravansSubletId                        -> (_ => aboutthetradinghistory.routes.TwinUnitCaravansAgeCategoriesController.show()),
    TwinCaravansAgeCategoriesId                 -> (_ => aboutthetradinghistory.routes.CaravansTotalSiteCapacityController.show()),
    CaravansTotalSiteCapacityId                 -> (_ => aboutthetradinghistory.routes.CaravansPerServiceController.show()),
    CaravansPerServiceId                        -> (_ => aboutthetradinghistory.routes.CaravansAnnualPitchFeeController.show()),
    CaravansAnnualPitchFeeId                    -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    ),
    OtherHolidayAccommodationId                 -> otherHolidayAccommodationRouting,
    OtherHolidayAccommodationDetailsId          -> (_ =>
      aboutthetradinghistory.routes.GrossReceiptsLettingUnitsController.show()
    ),
    GrossReceiptsHolidayUnitsId                 -> (_ => aboutthetradinghistory.routes.GrossReceiptsSubLetUnitsController.show()),
    GrossReceiptsSubLetUnitsId                  -> (_ => aboutthetradinghistory.routes.TotalSiteCapacity6045Controller.show()),
    TotalSiteCapacityId                         -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    ),
    TentingPitchesOnSiteId                      -> tentingPitchesOnSiteRouting,
    TentingPitchesAllYearId                     -> (_ => aboutthetradinghistory.routes.PitchesForCaravansController.show()),
    PitchesForCaravansId                        -> (_ => aboutthetradinghistory.routes.PitchesForGlampingController.show()),
    PitchesForGlampingId                        -> (_ => aboutthetradinghistory.routes.RallyAreasController.show()),
    RallyAreasId                                -> (_ => aboutthetradinghistory.routes.TentingPitchesTotalController.show()),
    AdditionalActivitiesOnSiteId                -> additionalActivitiesOnSiteRouting,
    AdditionalActivitiesAllYearId               -> (_ => aboutthetradinghistory.routes.AdditionalShopsController.show()),
    AdditionalShopsId                           -> (_ => controllers.aboutthetradinghistory.routes.AdditionalCateringController.show()),
    AdditionalCateringId                        -> (_ => controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show()),
    AdditionalBarsClubsId                       -> (_ => controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show()),
    AdditionalAmusementsId                      -> (_ => controllers.aboutthetradinghistory.routes.AdditionalMiscController.show()),
    AdditionalMiscId                            -> (_ =>
      controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()
    ),
    WhatYouWillNeedPageId                       -> whatYouWillNeedRouting,
    TentingPitchesTotalId                       -> (_ => aboutthetradinghistory.routes.TentingPitchesCertificatedController.show()),
    TentingPitchesCertificatedId                -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show()
    ),
    CheckYourAnswersOtherHolidayAccommodationId -> (_ =>
      controllers.routes.TaskListController.show().withFragment("tradingHistory")
    ),
    CheckYourAnswersTentingPitchesId            -> (_ =>
      controllers.routes.TaskListController.show().withFragment("tradingHistory")
    ),
    CheckYourAnswersAdditionalActivitiesId      -> (_ =>
      controllers.routes.TaskListController.show().withFragment("tradingHistory")
    ),
    CheckYourAnswersAboutTheTradingHistoryId    -> (_ =>
      controllers.routes.TaskListController.show().withFragment("tradingHistory")
    ),
    ChangeOccupationAndAccountingId             -> (_ => aboutthetradinghistory.routes.AboutYourTradingHistoryController.show()),
    AreYouVATRegisteredId                       -> (_ => aboutthetradinghistory.routes.FinancialYearEndController.show()),
    Income6048Id                                -> (_ => aboutthetradinghistory.routes.FixedCosts6048Controller.show),
    FixedCosts6048Id                            -> (_ => aboutthetradinghistory.routes.AccountingCosts6048Controller.show),
    AccountingCosts6048Id                       -> (_ => aboutthetradinghistory.routes.AdministrativeCosts6048Controller.show),
    AdministrativeCosts6048Id                   -> (_ => aboutthetradinghistory.routes.OperationalCosts6048Controller.show),
    OperationalCosts6048Id                      -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    )
  )
}
