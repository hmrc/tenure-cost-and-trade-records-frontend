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

case object GrossProfitsId extends Identifier {
  override def toString: String = "grossProfit"
}

case object TotalPayrollCostId extends Identifier {
  override def toString: String = "totalPayrollCost"
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

case object NetProfitId extends Identifier {
  override def toString: String = "netProfit"
}

case object CheckYourAnswersAboutTheTradingHistoryId extends Identifier {
  override def toString: String = "checkYourAnswersAboutTheTradingHistory"
}
