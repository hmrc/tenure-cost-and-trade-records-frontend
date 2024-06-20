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

package models.submissions.aboutthetradinghistory

case class IncomeExpenditure6076Entry(
  financialYearEnd: String,
  totalGrossReceipts: BigDecimal,
  grossReceiptsUrl: String,
  totalBaseLoadReceipts: BigDecimal,
  baseLoadReceiptsUrl: String,
  totalOtherIncome: BigDecimal,
  otherIncomeUrl: String,
  totalCostOfSales: BigDecimal,
  costOfSalesUrl: String,
  totalStaffCosts: BigDecimal,
  staffCostsUrl: String,
  totalPremisesCosts: BigDecimal,
  premisesCostsUrl: String,
  totalOperationalExpenses: BigDecimal,
  operationalExpensesUrl: String,
  headOfficeExpenses: BigDecimal,
  headOfficeExpensesUrl: String,
  netProfitOrLoss: BigDecimal
)
