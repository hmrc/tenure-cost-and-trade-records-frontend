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

    "Identifier for about your trading history page" in {
      assert(AboutYourTradingHistoryPageId.toString.equals("aboutYourTradingHistoryPage"))
    }

    "Identifier for about your trading history page" in {
      assert(WhatYouWillNeedPageId.toString.equals("whatYouWillNeedPage"))
    }

    "Identifier for financial year end page" in {
      assert(FinancialYearEndPageId.toString.equals("financialYearEndPage"))
    }

    "Identifier for financial year end dates page" in {
      assert(FinancialYearEndDatesPageId.toString.equals("financialYearEndDatesPage"))
    }

    "Identifier for turnover page" in {
      assert(TurnoverPageId.toString.equals("turnoverPage"))
    }

    "Identifier for cost of sales page" in {
      assert(CostOfSalesId.toString.equals("costOfSales"))
    }

    "Identifier for total payroll page" in {
      assert(TotalPayrollCostId.toString.equals("totalPayrollCost"))
    }

    "Identifier for about variable operating expenses page" in {
      assert(VariableOperatingExpensesId.toString.equals("variableOperatingExpenses"))
    }

    "Identifier for fixed operating expenses page" in {
      assert(FixedOperatingExpensesId.toString.equals("fixedOperatingExpenses"))
    }

    "Identifier for other costs page" in {
      assert(OtherCostsId.toString.equals("otherCosts"))
    }

    "Identifier for income expenditure summary page" in {
      assert(IncomeExpenditureSummaryId.toString.equals("incomeExpenditureSummary"))
    }

    "Identifier for income expenditure summary page fot 6076" in {
      assert(IncomeExpenditureSummary6076Id.toString.equals("incomeExpenditureSummary6076"))
    }

    "Identifier for unusual circumstances page" in {
      assert(UnusualCircumstancesId.toString.equals("unusualCircumstances"))
    }

    "Identifier for electric Vehicle Charging Points page" in {
      assert(ElectricVehicleChargingPointsId.toString.equals("electricVehicleChargingPoints"))
    }
    "Identifier for Electricity Generated  page" in {
      assert(ElectricityGeneratedId.toString.equals("electricityGeneratedPage"))
    }
    "Identifier for CostOfSales6076 page" in {
      assert(CostOfSales6076Id.toString.equals("costOfSales6076Page"))
    }

    "Identifier for StaffCosts page" in {
      assert(StaffCostsId.toString.equals("staffCostsPage"))
    }

    "Identifier for GrossReceiptsForBaseLoad page" in {
      assert(GrossReceiptsForBaseLoadId.toString.equals("grossReceiptsForBaseLoadPage"))
    }

    "Identifier for PremisesCosts page" in {
      assert(PremisesCostsId.toString.equals("premisesCostsPage"))
    }
    "Identifier for tentingPitchesOnSiteId page" in {
      assert(TentingPitchesOnSiteId.toString.equals("tentingPitchesOnSitePage"))
    }
    "Identifier for tentingPitchesAllYearId page" in {
      assert(TentingPitchesAllYearId.toString.equals("tentingPitchesAllYearPage"))
    }
    "Identifier for pitches for caravansId page" in {
      assert(PitchesForCaravansId.toString.equals("pitchesForCaravansPage"))
    }
    "Identifier for CheckYourAnswersTentingPitchesId page" in {
      assert(CheckYourAnswersTentingPitchesId.toString.equals("checkYourAnswersTentingPitchesPage"))
    }
    "Identifier for check your answers about the trading history page" in {
      assert(CheckYourAnswersAboutTheTradingHistoryId.toString.equals("checkYourAnswersAboutTheTradingHistory"))
    }

    "Identifier for total fuel sold page" in {
      assert(TotalFuelSoldId.toString.equals("totalFuelSold"))
    }
    "Identifier for bunkered fuel question" in {
      assert(BunkeredFuelQuestionId.toString.equals("bunkeredFuelQuestionPage"))
    }
    "Identifier for bunkered fuel sold page" in {
      assert(BunkeredFuelSoldId.toString.equals("bunkeredFuelSoldPage"))
    }
    "Identifier for customer credit accounts page" in {
      assert(CustomerCreditAccountsId.toString.equals("customerCreditAccountsPage"))
    }
    "Identifier for percentage from fuel cards page" in {
      assert(PercentageFromFuelCardsId.toString.equals("percentageFromFuelCardsPage"))
    }
    "Identifier for add bunker fuel cards page" in {
      assert(AddAnotherBunkerFuelCardsDetailsId.toString.equals("addAnotherBunkerFuelCardsDetailsPage"))
    }
    "Identifier for accept low margin fuel cards page" in {
      assert(AcceptLowMarginFuelCardsId.toString.equals("acceptLowMarginFuelCardsPage"))
    }
    "Identifier for add lm fuel cards page" in {
      assert(AddAnotherLowMarginFuelCardsDetailsId.toString.equals("addAnotherLowMarginFuelCardsDetailsPage"))
    }
    "Identifier for lm fuel cards page" in {
      assert(LowMarginFuelCardsDetailsId.toString.equals("lowMarginFuelCardsDetailsPage"))
    }
    "Identifier for bunker fuel card details page" in {
      assert(BunkerFuelCardsDetailsId.toString.equals("bunkerFuelCardsDetailsPage"))
    }
    "Identifier for other holiday accommodation details page" in {
      assert(OtherHolidayAccommodationDetailsId.toString.equals("otherHolidayAccommodationDetailsPage"))
    }
    "Identifier for other CYA holiday accommodation details page" in {
      assert(CheckYourAnswersOtherHolidayAccommodationId.toString.equals("checkYourAnswersOtherHolidayAccommodation"))
    }

    "Identifier for Gross Receipts Caravan Fleet Hire page" in {
      assert(GrossReceiptsCaravanFleetHireId.toString.equals("grossReceiptsCaravanFleetHirePage"))
    }

    "Identifier for Gross Receipts Excluding Vat  page" in {
      assert(GrossReceiptsExcludingVatId.toString.equals("grossReceiptsExcludingVatPage"))
    }

    "Identifier for Head Office Expenses  page" in {
      assert(HeadOfficeExpensesId.toString.equals("headOfficeExpensesPage"))
    }

    "Identifier for   Operational Expenses  page" in {
      assert(OperationalExpensesId.toString.equals("operationalExpensesPage"))
    }

    "Identifier for Other Holiday Accommodation  page" in {
      assert(OtherHolidayAccommodationId.toString.equals("otherHolidayAccommodationPage"))
    }

    "Identifier for  Other Income  page" in {
      assert(OtherIncomeId.toString.equals("otherIncomePage"))
    }

    "Identifier for  Static Caravans  page" in {
      assert(StaticCaravansId.toString.equals("staticCaravansPage"))
    }
  }
}
