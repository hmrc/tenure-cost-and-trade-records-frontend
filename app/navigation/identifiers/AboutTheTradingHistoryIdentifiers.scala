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

case object AboutYourTradingHistoryPageId extends Identifier {
  override def toString: String = "aboutYourTradingHistoryPage"
}

case object FinancialYearEndPageId extends Identifier {
  override def toString: String = "financialYearEndPage"
}

case object FinancialYearEndDatesPageId extends Identifier {
  override def toString: String = "financialYearEndDatesPage"
}

case object TurnoverPageId extends Identifier {
  override def toString: String = "turnoverPage"
}

case object CostOfSalesId extends Identifier {
  override def toString: String = "costOfSales"
}

case object TotalPayrollCostId extends Identifier {
  override def toString: String = "totalPayrollCost"
}

case object TotalFuelSoldId extends Identifier {
  override def toString: String = "totalFuelSold"
}

case object BunkeredFuelQuestionId extends Identifier {
  override def toString: String = "bunkeredFuelQuestionPage"
}

case object BunkeredFuelSoldId extends Identifier {
  override def toString: String = "bunkeredFuelSoldPage"
}

case object BunkerFuelCardsDetailsId extends Identifier {
  override def toString: String = "bunkerFuelCardsDetailsPage"
}

case object LowMarginFuelCardsDetailsId extends Identifier {
  override def toString: String = "lowMarginFuelCardsDetailsPage"
}

case object CustomerCreditAccountsId extends Identifier {
  override def toString: String = "customerCreditAccountsPage"
}

case object PercentageFromFuelCardsId extends Identifier {
  override def toString: String = "percentageFromFuelCardsPage"
}

case object VariableOperatingExpensesId extends Identifier {
  override def toString: String = "variableOperatingExpenses"
}

case object FixedOperatingExpensesId extends Identifier {
  override def toString: String = "fixedOperatingExpenses"
}

case object OtherCostsId extends Identifier {
  override def toString: String = "otherCosts"
}

case object IncomeExpenditureSummaryId extends Identifier {
  override def toString: String = "incomeExpenditureSummary"
}

case object UnusualCircumstancesId extends Identifier {
  override def toString: String = "unusualCircumstances"
}

case object ElectricVehicleChargingPointsId extends Identifier {
  override def toString: String = "electricVehicleChargingPoints"
}

case object CheckYourAnswersAboutTheTradingHistoryId extends Identifier {
  override def toString: String = "checkYourAnswersAboutTheTradingHistory"
}
