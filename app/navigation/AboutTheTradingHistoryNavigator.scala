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
import controllers.aboutthetradinghistory.routes
import models.submissions.common.AnswerYes
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
    Some(routes.CheckYourAnswersAboutTheTradingHistoryController.show())

  def cyaPageForTentingPitches: Call = routes.CheckYourAnswersTentingPitchesController.show()

  def cyaPageForOtherHolidayAccommodation: Call =
    routes.CheckYourAnswersOtherHolidayAccommodationController.show()

  def cyaPageForAdditionalActivities: Call =
    routes.CheckYourAnswersAdditionalActivitiesController.show()

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
    routes.FinancialYearEndController.show(),
    routes.FinancialYearEndDatesController.show(),
    routes.FinancialYearEndDatesSummaryController.show(),
    routes.ElectricityGeneratedController.show(),
    routes.BunkeredFuelSoldController.show(),
    routes.BunkerFuelCardDetailsController.show(),
    routes.PercentageFromFuelCardsController.show(),
    routes.TurnoverController.show(),
    routes.CostOfSalesController.show()
  ).map(_.url)

  private def financialYearEndRouting: Session => Call = { s =>
    if s.financialYearEndDates.isEmpty
    then controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController.show()
    else
      s.aboutTheTradingHistory.flatMap(
        _.occupationAndAccountingInformation.flatMap(_.financialYearEndHasChanged)
      ) match {
        case Some(true) =>
          s.forType match {
            case FOR6010 | FOR6011 | FOR6015 | FOR6016 | FOR6020 | FOR6030 | FOR6045 | FOR6046 | FOR6048 | FOR6076 =>
              routes.FinancialYearEndDatesSummaryController.show()
          }
        case _          => controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show
      }
  }

  private def bunkeredFuelQuestionRouting: Session => Call = answers =>
    answers.aboutTheTradingHistory.flatMap(_.bunkeredFuelQuestion.map(_.bunkeredFuelQuestion)) match {
      case Some(AnswerYes) => routes.BunkeredFuelSoldController.show()
      case _               => routes.CustomerCreditAccountsController.show()
    }

  private def financialYearEndDatesRouting: Session => Call =
    _ => controllers.aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show

  def checkYourAnswersAccountInfoRouting: Session => Call =
    _.forType match {
      case FOR6020           => routes.TotalFuelSoldController.show()
      case FOR6030           => routes.Turnover6030Controller.show()
      case FOR6045 | FOR6046 => routes.StaticCaravansController.show()
      case FOR6048           => routes.Income6048Controller.show
      case FOR6076           => routes.ElectricityGeneratedController.show()
      case _                 => routes.TurnoverController.show()
    }

  private def turnoverRouting: Session => Call =
    _.forType match {
      case FOR6015 => routes.CostOfSalesController.show()
      case FOR6020 => routes.ElectricVehicleChargingPointsController.show()
      case FOR6030 => routes.UnusualCircumstancesController.show()
      case _       => routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

  private def getAddAnotherBunkerFuelCardsDetailRouting(answers: Session): Call = {
    val currentIndex: Option[Int] = answers.aboutTheTradingHistory.flatMap(_.bunkerFuelCardsDetails) match {
      case Some(details) if details.nonEmpty => Some(details.size - 1) // Assuming the last entry is the current one
      case _                                 => None
    }
    currentIndex match {
      case Some(idx) =>
        routes.AddAnotherBunkerFuelCardsDetailsController.show(
          idx
        ) // Assuming there's a 'show' method to edit
      case None      =>
        routes.AddAnotherBunkerFuelCardsDetailsController.show(0) // Fallback or start new
    }
  }

  private def acceptLowMarginFuelCardsRouting: Session => Call =
    _.aboutTheTradingHistory.flatMap(_.doYouAcceptLowMarginFuelCard) match {
      case Some(AnswerYes) => routes.PercentageFromFuelCardsController.show()
      case _               => routes.NonFuelTurnoverController.show()
    }

  private def staticCaravansRouting: Session => Call =
    _.aboutTheTradingHistoryPartOne.flatMap(_.caravans).flatMap(_.anyStaticLeisureCaravansOnSite) match {
      case Some(AnswerYes) => routes.GrossReceiptsCaravanFleetHireController.show()
      case _               => routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    }

  private def getAddAnotherLowMarginFuelCardsDetailRouting(answers: Session): Call = {
    val currentIndex: Option[Int] = answers.aboutTheTradingHistory.flatMap(_.lowMarginFuelCardsDetails) match {
      case Some(details) if details.nonEmpty => Some(details.size - 1) // Assuming the last entry is the current one
      case _                                 => None
    }
    currentIndex match {
      case Some(idx) =>
        routes.AddAnotherLowMarginFuelCardsDetailsController.show(
          idx
        ) // Assuming there's a 'show' method to edit
      case None      =>
        routes.AddAnotherLowMarginFuelCardsDetailsController.show(0) // Fallback or start new
    }
  }

  private def otherHolidayAccommodationRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.otherHolidayAccommodation.flatMap(_.otherHolidayAccommodation)
    ) match {
      case Some(AnswerYes) => routes.GrossReceiptsLettingUnitsController.show()
      case _               => routes.CheckYourAnswersOtherHolidayAccommodationController.show()
    }

  private def tentingPitchesOnSiteRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.touringAndTentingPitches.flatMap(_.tentingPitchesOnSite)
    ) match {
      case Some(AnswerYes) => routes.PitchesForCaravansController.show()
      case _               => routes.CheckYourAnswersTentingPitchesController.show()
    }

  private def additionalActivitiesOnSiteRouting(answers: Session): Call =
    answers.aboutTheTradingHistoryPartOne.flatMap(
      _.additionalActivities.flatMap(_.additionalActivitiesOnSite)
    ) match {
      case Some(AnswerYes) => routes.AdditionalShopsController.show()
      case _               => routes.CheckYourAnswersAdditionalActivitiesController.show()
    }

  private def intermittentRouting: Session => Call = answers =>
    val intermittent = answers.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))
    intermittent match {
      case Some("intermittent") => routes.CostOfSales6076IntermittentController.show()
      case _                    => routes.CostOfSales6076Controller.show()
    }

  private def grossReceiptsRouting: Session => Call = answers =>
    val intermittent = answers.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))
    intermittent match {
      case Some("intermittent") => routes.OtherIncomeController.show()
      case _                    => routes.GrossReceiptsForBaseLoadController.show()
    }

  private def whatYouWillNeedRouting: Session => Call =
    _.forType match {
      case FOR6048 => routes.AreYouVATRegisteredController.show
      case _       => routes.WhenDidYouFirstOccupyController.show()
    }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYourTradingHistoryPageId               -> (_ => routes.FinancialYearEndController.show()),
    FinancialYearEndPageId                      -> financialYearEndRouting,
    CheckYourAnswersAccountingInfoPageId        -> checkYourAnswersAccountInfoRouting,
    FinancialYearEndDatesPageId                 -> financialYearEndDatesRouting,
    TurnoverPageId                              -> turnoverRouting,
    CostOfSalesId                               -> (_ => routes.TotalPayrollCostsController.show()),
    TotalPayrollCostId                          -> (_ => routes.VariableOperatingExpensesController.show()),
    TotalFuelSoldId                             -> (_ => routes.BunkeredFuelQuestionController.show()),
    VariableOperatingExpensesId                 -> (_ => routes.FixedOperatingExpensesController.show()),
    FixedOperatingExpensesId                    -> (_ => routes.OtherCostsController.show()),
    OtherCostsId                                -> (_ => routes.IncomeExpenditureSummaryController.show()),
    BunkeredFuelQuestionId                      -> bunkeredFuelQuestionRouting,
    BunkeredFuelSoldId                          -> (_ => routes.BunkerFuelCardDetailsController.show(None)),
    CustomerCreditAccountsId                    -> (_ => routes.AcceptLowMarginFuelCardController.show()),
    PercentageFromFuelCardsId                   -> (_ => routes.LowMarginFuelCardDetailsController.show()),
    BunkerFuelCardsDetailsId                    -> getAddAnotherBunkerFuelCardsDetailRouting,
    AddAnotherBunkerFuelCardsDetailsId          -> (_ => routes.CustomerCreditAccountsController.show()),
    AcceptLowMarginFuelCardsId                  -> acceptLowMarginFuelCardsRouting,
    AddAnotherLowMarginFuelCardsDetailsId       -> (_ => routes.NonFuelTurnoverController.show()),
    LowMarginFuelCardsDetailsId                 -> getAddAnotherLowMarginFuelCardsDetailRouting,
    IncomeExpenditureSummaryId                  -> (_ => routes.UnusualCircumstancesController.show()),
    IncomeExpenditureSummary6076Id              -> (_ => routes.CheckYourAnswersAboutTheTradingHistoryController.show()),
    UnusualCircumstancesId                      -> (_ => routes.CheckYourAnswersAboutTheTradingHistoryController.show()),
    ElectricVehicleChargingPointsId             -> (_ => routes.CheckYourAnswersAboutTheTradingHistoryController.show()),
    ElectricityGeneratedId                      -> (_ => routes.GrossReceiptsExcludingVATController.show()),
    GrossReceiptsExcludingVatId                 -> grossReceiptsRouting,
    OtherIncomeId                               -> intermittentRouting,
    CostOfSales6076IntermittentId               -> (_ => routes.StaffCostsController.show()),
    CostOfSales6076Id                           -> (_ => routes.StaffCostsController.show()),
    StaffCostsId                                -> (_ => routes.PremisesCostsController.show()),
    PremisesCostsId                             -> (_ => routes.OperationalExpensesController.show()),
    GrossReceiptsForBaseLoadId                  -> (_ => routes.OtherIncomeController.show()),
    OperationalExpensesId                       -> (_ => routes.HeadOfficeExpensesController.show()),
    HeadOfficeExpensesId                        -> (_ => routes.IncomeExpenditureSummary6076Controller.show()),
    StaticCaravansId                            -> staticCaravansRouting,
    GrossReceiptsCaravanFleetHireId             -> (_ => routes.SingleCaravansOwnedByOperatorController.show()),
    SingleCaravansOwnedByOperatorId             -> (_ => routes.SingleCaravansSubletController.show()),
    SingleCaravansSubletId                      -> (_ => routes.SingleCaravansAgeCategoriesController.show()),
    SingleCaravansAgeCategoriesId               -> (_ => routes.TwinUnitCaravansOwnedByOperatorController.show()),
    TwinCaravansOwnedByOperatorId               -> (_ => routes.TwinUnitCaravansSubletController.show()),
    TwinCaravansSubletId                        -> (_ => routes.TwinUnitCaravansAgeCategoriesController.show()),
    TwinCaravansAgeCategoriesId                 -> (_ => routes.CaravansTotalSiteCapacityController.show()),
    CaravansTotalSiteCapacityId                 -> (_ => routes.CaravansPerServiceController.show()),
    CaravansPerServiceId                        -> (_ => routes.CaravansAnnualPitchFeeController.show()),
    CaravansAnnualPitchFeeId                    -> (_ => routes.CheckYourAnswersAboutTheTradingHistoryController.show()),
    OtherHolidayAccommodationId                 -> otherHolidayAccommodationRouting,
    GrossReceiptsHolidayUnitsId                 -> (_ => routes.GrossReceiptsSubLetUnitsController.show()),
    GrossReceiptsSubLetUnitsId                  -> (_ => routes.TotalSiteCapacity6045Controller.show()),
    TotalSiteCapacityId                         -> (_ => routes.CheckYourAnswersOtherHolidayAccommodationController.show()),
    TentingPitchesOnSiteId                      -> tentingPitchesOnSiteRouting,
    PitchesForCaravansId                        -> (_ => routes.PitchesForGlampingController.show()),
    PitchesForGlampingId                        -> (_ => routes.RallyAreasController.show()),
    RallyAreasId                                -> (_ => routes.TentingPitchesTotalController.show()),
    AdditionalActivitiesOnSiteId                -> additionalActivitiesOnSiteRouting,
    AdditionalShopsId                           -> (_ => routes.AdditionalCateringController.show()),
    AdditionalCateringId                        -> (_ => routes.AdditionalBarsClubsController.show()),
    AdditionalBarsClubsId                       -> (_ => routes.AdditionalAmusementsController.show()),
    AdditionalAmusementsId                      -> (_ => routes.AdditionalMiscController.show()),
    AdditionalMiscId                            -> (_ => routes.CheckYourAnswersAdditionalActivitiesController.show()),
    WhatYouWillNeedPageId                       -> whatYouWillNeedRouting,
    TentingPitchesTotalId                       -> (_ => routes.TentingPitchesCertificatedController.show()),
    TentingPitchesCertificatedId                -> (_ => routes.CheckYourAnswersTentingPitchesController.show()),
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
    ChangeOccupationAndAccountingId             -> (_ => routes.WhenDidYouFirstOccupyController.show()),
    AreYouVATRegisteredId                       -> (_ => routes.FinancialYearEndController.show()),
    Income6048Id                                -> (_ => routes.FixedCosts6048Controller.show),
    FixedCosts6048Id                            -> (_ => routes.AccountingCosts6048Controller.show),
    AccountingCosts6048Id                       -> (_ => routes.AdministrativeCosts6048Controller.show),
    AdministrativeCosts6048Id                   -> (_ => routes.OperationalCosts6048Controller.show),
    OperationalCosts6048Id                      -> (_ => routes.CheckYourAnswersAboutTheTradingHistoryController.show())
  )
}
