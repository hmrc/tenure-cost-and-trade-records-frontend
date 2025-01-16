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

package navigation.identifiers

import utils.TestBaseSpec

class AboutTheTradingHistoryIdentifiersSpec extends TestBaseSpec {

  "About your trading history identifiers" when {

    "Identifier for about your trading history page" in
      assert(AboutYourTradingHistoryPageId.toString.equals("aboutYourTradingHistoryPage"))

    "Identifier for what will you need page" in
      assert(WhatYouWillNeedPageId.toString.equals("whatYouWillNeedPage"))

    "Identifier for financial year end page" in
      assert(FinancialYearEndPageId.toString.equals("financialYearEndPage"))

    "Identifier for financial year end dates page" in
      assert(FinancialYearEndDatesPageId.toString.equals("financialYearEndDatesPage"))

    "Identifier for financial year page page" in
      assert(FinancialYearsPageId.toString.equals("financialYearsPage"))

    "Identifier for turnover page" in
      assert(TurnoverPageId.toString.equals("turnoverPage"))

    "Identifier for cost of sales page" in
      assert(CostOfSalesId.toString.equals("costOfSales"))

    "Identifier for total payroll page" in
      assert(TotalPayrollCostId.toString.equals("totalPayrollCost"))

    "Identifier for about variable operating expenses page" in
      assert(VariableOperatingExpensesId.toString.equals("variableOperatingExpenses"))

    "Identifier for fixed operating expenses page" in
      assert(FixedOperatingExpensesId.toString.equals("fixedOperatingExpenses"))

    "Identifier for other costs page" in
      assert(OtherCostsId.toString.equals("otherCosts"))

    "Identifier for income expenditure summary page" in
      assert(IncomeExpenditureSummaryId.toString.equals("incomeExpenditureSummary"))

    "Identifier for income expenditure summary page fot 6076" in
      assert(IncomeExpenditureSummary6076Id.toString.equals("incomeExpenditureSummary6076"))

    "Identifier for unusual circumstances page" in
      assert(UnusualCircumstancesId.toString.equals("unusualCircumstances"))

    "Identifier for electric Vehicle Charging Points page" in
      assert(ElectricVehicleChargingPointsId.toString.equals("electricVehicleChargingPoints"))
    "Identifier for Electricity Generated  page" in
      assert(ElectricityGeneratedId.toString.equals("electricityGeneratedPage"))

    "Identifier for CostOfSales6076 page" in
      assert(CostOfSales6076Id.toString.equals("costOfSales6076Page"))

    "Identifier for CostOfSalesIntermittent6076 page" in
      assert(CostOfSales6076IntermittentId.toString.equals("costOfSales6076IntermittentPage"))

    "Identifier for StaffCosts page" in
      assert(StaffCostsId.toString.equals("staffCostsPage"))

    "Identifier for GrossReceiptsForBaseLoad page" in
      assert(GrossReceiptsForBaseLoadId.toString.equals("grossReceiptsForBaseLoadPage"))

    "Identifier for PremisesCosts page" in
      assert(PremisesCostsId.toString.equals("premisesCostsPage"))
    "Identifier for tenting Pitches On Site page" in
      assert(TentingPitchesOnSiteId.toString.equals("tentingPitchesOnSitePage"))
    "Identifier for tenting Pitches All Year page" in
      assert(TentingPitchesAllYearId.toString.equals("tentingPitchesAllYearPage"))
    "Identifier for pitches for caravans page" in
      assert(PitchesForCaravansId.toString.equals("pitchesForCaravansPage"))
    "Identifier for pitches for glamping  page" in
      assert(PitchesForGlampingId.toString.equals("pitchesForGlampingPage"))
    "Identifier for rally areas  page" in
      assert(RallyAreasId.toString.equals("rallyAreasPage"))
    "Identifier for tenting Pitches  total" in
      assert(TentingPitchesTotalId.toString.equals("tentingPitchesTotalPage"))
    "Identifier for tenting Pitches  certificated" in
      assert(TentingPitchesCertificatedId.toString.equals("tentingPitchesCertificatedPage"))

    "Identifier for additional activities on site" in
      assert(AdditionalActivitiesOnSiteId.toString.equals("additionalActivitiesOnSitePage"))

    "Identifier for additional activities all year" in
      assert(AdditionalActivitiesAllYearId.toString.equals("additionalActivitiesAllYearPage"))

    "Identifier for additional activities shops" in
      assert(AdditionalShopsId.toString.equals("additionalShopsPage"))

    "Identifier for additional activities catering" in
      assert(AdditionalCateringId.toString.equals("additionalCateringPage"))
    "Identifier for additional activities bars" in
      assert(AdditionalBarsClubsId.toString.equals("additionalBarsClubsPage"))

    "Identifier for additional amusements" in
      assert(AdditionalAmusementsId.toString.equals("additionalAmusementsPage"))

    "Identifier for additional misc" in
      assert(AdditionalMiscId.toString.equals("additionalMiscPage"))

    "Identifier for caravans open all year" in
      assert(CaravansOpenAllYearId.toString.equals("caravansOpenAllYearPage"))

    "Identifier for gross receipts holiday unit page" in
      assert(GrossReceiptsHolidayUnitsId.toString.equals("grossReceiptsHolidayUnitsPage"))

    "Identifier for gross receipts sub let units page " in
      assert(GrossReceiptsSubLetUnitsId.toString.equals("grossReceiptsSubLetUnitsPage"))

    "Identifier for single caravan age categories page  " in
      assert(SingleCaravansAgeCategoriesId.toString.equals("singleCaravansAgeCategoriesPage"))

    "Identifier for single caravan owned by operator page  " in
      assert(SingleCaravansOwnedByOperatorId.toString.equals("singleCaravansOwnedByOperatorPage"))

    "Identifier for single caravan sublet page  " in
      assert(SingleCaravansSubletId.toString.equals("singleCaravansSubletPage"))
    "Identifier for total site capacity page  " in
      assert(TotalSiteCapacityId.toString.equals("TotalSiteCapacityPage"))
    "Identifier for twin caravan owned by operator page  " in
      assert(TwinCaravansOwnedByOperatorId.toString.equals("twinCaravansOwnedByOperatorPage"))
    "Identifier for twin caravan sublet page  " in
      assert(TwinCaravansSubletId.toString.equals("twinCaravansSubletPage"))

    "Identifier for Check Your Answers Tenting Pitches page" in
      assert(CheckYourAnswersTentingPitchesId.toString.equals("checkYourAnswersTentingPitchesPage"))

    "Identifier for Check Your Answers Additional Activities page" in
      assert(CheckYourAnswersAdditionalActivitiesId.toString.equals("checkYourAnswersAdditionalActivitiesPage"))

    "Identifier for check your answers about the trading history page" in
      assert(CheckYourAnswersAboutTheTradingHistoryId.toString.equals("checkYourAnswersAboutTheTradingHistory"))

    "Identifier for change occupation and accounting page" in
      assert(ChangeOccupationAndAccountingId.toString.equals("changeOccupationAndAccountingPage"))

    "Identifier for vat registered page" in
      assert(AreYouVATRegisteredId.toString.equals("areYouVATRegisteredPage"))

    "Identifier for total fuel sold page" in
      assert(TotalFuelSoldId.toString.equals("totalFuelSold"))
    "Identifier for bunkered fuel question" in
      assert(BunkeredFuelQuestionId.toString.equals("bunkeredFuelQuestionPage"))
    "Identifier for bunkered fuel sold page" in
      assert(BunkeredFuelSoldId.toString.equals("bunkeredFuelSoldPage"))
    "Identifier for customer credit accounts page" in
      assert(CustomerCreditAccountsId.toString.equals("customerCreditAccountsPage"))
    "Identifier for percentage from fuel cards page" in
      assert(PercentageFromFuelCardsId.toString.equals("percentageFromFuelCardsPage"))
    "Identifier for add bunker fuel cards page" in
      assert(AddAnotherBunkerFuelCardsDetailsId.toString.equals("addAnotherBunkerFuelCardsDetailsPage"))
    "Identifier for accept low margin fuel cards page" in
      assert(AcceptLowMarginFuelCardsId.toString.equals("acceptLowMarginFuelCardsPage"))
    "Identifier for add lm fuel cards page" in
      assert(AddAnotherLowMarginFuelCardsDetailsId.toString.equals("addAnotherLowMarginFuelCardsDetailsPage"))
    "Identifier for lm fuel cards page" in
      assert(LowMarginFuelCardsDetailsId.toString.equals("lowMarginFuelCardsDetailsPage"))
    "Identifier for bunker fuel card details page" in
      assert(BunkerFuelCardsDetailsId.toString.equals("bunkerFuelCardsDetailsPage"))
    "Identifier for other holiday accommodation details page" in
      assert(OtherHolidayAccommodationDetailsId.toString.equals("otherHolidayAccommodationDetailsPage"))
    "Identifier for other CYA holiday accommodation details page" in
      assert(CheckYourAnswersOtherHolidayAccommodationId.toString.equals("checkYourAnswersOtherHolidayAccommodation"))

    "Identifier for Gross Receipts Caravan Fleet Hire page" in
      assert(GrossReceiptsCaravanFleetHireId.toString.equals("grossReceiptsCaravanFleetHirePage"))

    "Identifier for Gross Receipts Excluding Vat  page" in
      assert(GrossReceiptsExcludingVatId.toString.equals("grossReceiptsExcludingVatPage"))

    "Identifier for Head Office Expenses  page" in
      assert(HeadOfficeExpensesId.toString.equals("headOfficeExpensesPage"))

    "Identifier for   Operational Expenses  page" in
      assert(OperationalExpensesId.toString.equals("operationalExpensesPage"))

    "Identifier for Other Holiday Accommodation  page" in
      assert(OtherHolidayAccommodationId.toString.equals("otherHolidayAccommodationPage"))

    "Identifier for  Other Income  page" in
      assert(OtherIncomeId.toString.equals("otherIncomePage"))

    "Identifier for  Static Caravans  page" in
      assert(StaticCaravansId.toString.equals("staticCaravansPage"))

    "Identifier for Twin Caravans  page" in
      assert(TwinCaravansAgeCategoriesId.toString.equals("twinCaravansAgeCategoriesPage"))

    "Identifier for caravansTotalSiteCapacityPage" in
      assert(CaravansTotalSiteCapacityId.toString.equals("caravansTotalSiteCapacityPage"))

    "Identifier for caravansPerServicePage" in
      assert(CaravansPerServiceId.toString.equals("caravansPerServicePage"))

    "Identifier for caravansAnnualPitchFeePage" in
      assert(CaravansAnnualPitchFeeId.toString.equals("caravansAnnualPitchFeePage"))

    "Identifier for income6048Page" in
      assert(Income6048Id.toString.equals("income6048Page"))

    "Identifier for fixedCostsPage" in
      assert(FixedCosts6048Id.toString.equals("fixedCostsPage"))

    "Identifier for accountingCostsPage" in
      assert(AccountingCosts6048Id.toString.equals("accountingCostsPage"))

    "Identifier for administrativeCostsPage" in
      assert(AdministrativeCosts6048Id.toString.equals("administrativeCostsPage"))

    "Identifier for operationalCostsPage" in
      assert(OperationalCosts6048Id.toString.equals("operationalCostsPage"))

  }
}
