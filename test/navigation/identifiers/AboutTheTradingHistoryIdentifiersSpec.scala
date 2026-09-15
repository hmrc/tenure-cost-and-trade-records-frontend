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

package navigation.identifiers

import uk.gov.hmrc.vo.unit.test.BaseSpec

class AboutTheTradingHistoryIdentifiersSpec extends BaseSpec:

  "Trading history page identifiers" should {
    "be correct for the about your trading history page" in {
      AboutYourTradingHistoryPageId.toString shouldBe "aboutYourTradingHistoryPage"
    }

    "be correct for the what will you need page" in {
      WhatYouWillNeedPageId.toString shouldBe "whatYouWillNeedPage"
    }

    "be correct for the financial year end page" in {
      FinancialYearEndPageId.toString shouldBe "financialYearEndPage"
    }

    "be correct for the financial year end dates page" in {
      FinancialYearEndDatesPageId.toString shouldBe "financialYearEndDatesPage"
    }

    "be correct for the accounting info CYA page" in {
      CheckYourAnswersAccountingInfoPageId.toString shouldBe "checkYourAnswersAccountingInfoPage"
    }

    "be correct for the turnover page" in {
      TurnoverPageId.toString shouldBe "turnoverPage"
    }

    "be correct for the cost of sales page" in {
      CostOfSalesId.toString shouldBe "costOfSales"
    }

    "be correct for the total payroll cost page" in {
      TotalPayrollCostId.toString shouldBe "totalPayrollCost"
    }

    "be correct for the variable operating expenses page" in {
      VariableOperatingExpensesId.toString shouldBe "variableOperatingExpenses"
    }

    "be correct for the fixed operating expenses page" in {
      FixedOperatingExpensesId.toString shouldBe "fixedOperatingExpenses"
    }

    "be correct for the other costs page" in {
      OtherCostsId.toString shouldBe "otherCosts"
    }

    "be correct for the income expenditure summary page" in {
      IncomeExpenditureSummaryId.toString shouldBe "incomeExpenditureSummary"
    }

    "be correct for the income expenditure summary page for 6076" in {
      IncomeExpenditureSummary6076Id.toString shouldBe "incomeExpenditureSummary6076"
    }

    "be correct for the unusual circumstances page" in {
      UnusualCircumstancesId.toString shouldBe "unusualCircumstances"
    }

    "be correct for the Electric Vehicle Charging Points page" in {
      ElectricVehicleChargingPointsId.toString shouldBe "electricVehicleChargingPoints"
    }

    "be correct for the Electricity Generated page" in {
      ElectricityGeneratedId.toString shouldBe "electricityGeneratedPage"
    }

    "be correct for the CostOfSales6076 page" in {
      CostOfSales6076Id.toString shouldBe "costOfSales6076Page"
    }

    "be correct for the CostOfSalesIntermittent6076 page" in {
      CostOfSales6076IntermittentId.toString shouldBe "costOfSales6076IntermittentPage"
    }

    "be correct for the StaffCosts page" in {
      StaffCostsId.toString shouldBe "staffCostsPage"
    }

    "be correct for the GrossReceiptsForBaseLoad page" in {
      GrossReceiptsForBaseLoadId.toString shouldBe "grossReceiptsForBaseLoadPage"
    }

    "be correct for the PremisesCosts page" in {
      PremisesCostsId.toString shouldBe "premisesCostsPage"
    }

    "be correct for the Tenting Pitches On Site page" in {
      TentingPitchesOnSiteId.toString shouldBe "tentingPitchesOnSitePage"
    }

    "be correct for the pitches for caravans page" in {
      PitchesForCaravansId.toString shouldBe "pitchesForCaravansPage"
    }

    "be correct for the pitches for glamping page" in {
      PitchesForGlampingId.toString shouldBe "pitchesForGlampingPage"
    }

    "be correct for the rally areas page" in {
      RallyAreasId.toString shouldBe "rallyAreasPage"
    }

    "be correct for the tenting pitches total page" in {
      TentingPitchesTotalId.toString shouldBe "tentingPitchesTotalPage"
    }

    "be correct for the tenting pitches certificated page" in {
      TentingPitchesCertificatedId.toString shouldBe "tentingPitchesCertificatedPage"
    }

    "be correct for the additional activities on site page" in {
      AdditionalActivitiesOnSiteId.toString shouldBe "additionalActivitiesOnSitePage"
    }

    "be correct for the additional activities shops page" in {
      AdditionalShopsId.toString shouldBe "additionalShopsPage"
    }

    "be correct for the additional activities catering page" in {
      AdditionalCateringId.toString shouldBe "additionalCateringPage"
    }

    "be correct for the additional activities bars page" in {
      AdditionalBarsClubsId.toString shouldBe "additionalBarsClubsPage"
    }

    "be correct for the additional amusements page" in {
      AdditionalAmusementsId.toString shouldBe "additionalAmusementsPage"
    }

    "be correct for the additional misc page" in {
      AdditionalMiscId.toString shouldBe "additionalMiscPage"
    }

    "be correct for the gross receipts holiday units page" in {
      GrossReceiptsHolidayUnitsId.toString shouldBe "grossReceiptsHolidayUnitsPage"
    }

    "be correct for the gross receipts sub let units page" in {
      GrossReceiptsSubLetUnitsId.toString shouldBe "grossReceiptsSubLetUnitsPage"
    }

    "be correct for the single caravans age categories page" in {
      SingleCaravansAgeCategoriesId.toString shouldBe "singleCaravansAgeCategoriesPage"
    }

    "be correct for the single caravans owned by operator page" in {
      SingleCaravansOwnedByOperatorId.toString shouldBe "singleCaravansOwnedByOperatorPage"
    }

    "be correct for the single caravans sublet page" in {
      SingleCaravansSubletId.toString shouldBe "singleCaravansSubletPage"
    }

    "be correct for the total site capacity page" in {
      TotalSiteCapacityId.toString shouldBe "TotalSiteCapacityPage"
    }

    "be correct for the twin caravans owned by operator page" in {
      TwinCaravansOwnedByOperatorId.toString shouldBe "twinCaravansOwnedByOperatorPage"
    }

    "be correct for the twin caravans sublet page" in {
      TwinCaravansSubletId.toString shouldBe "twinCaravansSubletPage"
    }

    "be correct for the tenting pitches CYA page" in {
      CheckYourAnswersTentingPitchesId.toString shouldBe "checkYourAnswersTentingPitchesPage"
    }

    "be correct for the additional activities CYA page" in {
      CheckYourAnswersAdditionalActivitiesId.toString shouldBe "checkYourAnswersAdditionalActivitiesPage"
    }

    "be correct for the trading history CYA page" in {
      CheckYourAnswersAboutTheTradingHistoryId.toString shouldBe "checkYourAnswersAboutTheTradingHistory"
    }

    "be correct for the change occupation and accounting page" in {
      ChangeOccupationAndAccountingId.toString shouldBe "changeOccupationAndAccountingPage"
    }

    "be correct for the VAT registered page" in {
      AreYouVATRegisteredId.toString shouldBe "areYouVATRegisteredPage"
    }

    "be correct for the total fuel sold page" in {
      TotalFuelSoldId.toString shouldBe "totalFuelSold"
    }

    "be correct for the bunkered fuel question page" in {
      BunkeredFuelQuestionId.toString shouldBe "bunkeredFuelQuestionPage"
    }

    "be correct for the bunkered fuel sold page" in {
      BunkeredFuelSoldId.toString shouldBe "bunkeredFuelSoldPage"
    }

    "be correct for the customer credit accounts page" in {
      CustomerCreditAccountsId.toString shouldBe "customerCreditAccountsPage"
    }

    "be correct for the percentage from fuel cards page" in {
      PercentageFromFuelCardsId.toString shouldBe "percentageFromFuelCardsPage"
    }

    "be correct for the add bunker fuel card page" in {
      AddAnotherBunkerFuelCardsDetailsId.toString shouldBe "addAnotherBunkerFuelCardsDetailsPage"
    }

    "be correct for the accept low margin fuel card page" in {
      AcceptLowMarginFuelCardsId.toString shouldBe "acceptLowMarginFuelCardsPage"
    }

    "be correct for the AccommodationUnit page" in {
      AccommodationUnitPageId.toString shouldBe "accommodationUnitPage"
    }

    "be correct for the AvailableRooms page" in {
      AvailableRoomsPageId.toString shouldBe "availableRoomsPage"
    }

    "be correct for the add low margin fuel card page" in {
      AddAnotherLowMarginFuelCardsDetailsId.toString shouldBe "addAnotherLowMarginFuelCardsDetailsPage"
    }

    "be correct for the low margin fuel cards page" in {
      LowMarginFuelCardsDetailsId.toString shouldBe "lowMarginFuelCardsDetailsPage"
    }

    "be correct for the bunker fuel card details page" in {
      BunkerFuelCardsDetailsId.toString shouldBe "bunkerFuelCardsDetailsPage"
    }

    "be correct for the other holiday accommodation CYA page" in {
      CheckYourAnswersOtherHolidayAccommodationId.toString shouldBe "checkYourAnswersOtherHolidayAccommodation"
    }

    "be correct for the Gross Receipts Caravan Fleet Hire page" in {
      GrossReceiptsCaravanFleetHireId.toString shouldBe "grossReceiptsCaravanFleetHirePage"
    }

    "be correct for the Gross Receipts Excluding VAT page" in {
      GrossReceiptsExcludingVatId.toString shouldBe "grossReceiptsExcludingVatPage"
    }

    "be correct for the Head Office Expenses page" in {
      HeadOfficeExpensesId.toString shouldBe "headOfficeExpensesPage"
    }

    "be correct for the Operational Expenses page" in {
      OperationalExpensesId.toString shouldBe "operationalExpensesPage"
    }

    "be correct for the Other Holiday Accommodation page" in {
      OtherHolidayAccommodationId.toString shouldBe "otherHolidayAccommodationPage"
    }

    "be correct for the Other Income page" in {
      OtherIncomeId.toString shouldBe "otherIncomePage"
    }

    "be correct for the Static Caravans page" in {
      StaticCaravansId.toString shouldBe "staticCaravansPage"
    }

    "be correct for the Twin Caravans Age Categories page" in {
      TwinCaravansAgeCategoriesId.toString shouldBe "twinCaravansAgeCategoriesPage"
    }

    "be correct for the Caravans Total Site Capacity page" in {
      CaravansTotalSiteCapacityId.toString shouldBe "caravansTotalSiteCapacityPage"
    }

    "be correct for the Caravans Per Service page" in {
      CaravansPerServiceId.toString shouldBe "caravansPerServicePage"
    }

    "be correct for the Caravans Annual Pitch Fee page" in {
      CaravansAnnualPitchFeeId.toString shouldBe "caravansAnnualPitchFeePage"
    }

    "be correct for the Income6048 page" in {
      Income6048Id.toString shouldBe "income6048Page"
    }

    "be correct for the fixed costs page" in {
      FixedCosts6048Id.toString shouldBe "fixedCostsPage"
    }

    "be correct for the accounting costs page" in {
      AccountingCosts6048Id.toString shouldBe "accountingCostsPage"
    }

    "be correct for the administrative costs page" in {
      AdministrativeCosts6048Id.toString shouldBe "administrativeCostsPage"
    }

    "be correct for the operational costs page" in {
      OperationalCosts6048Id.toString shouldBe "operationalCostsPage"
    }
  }
