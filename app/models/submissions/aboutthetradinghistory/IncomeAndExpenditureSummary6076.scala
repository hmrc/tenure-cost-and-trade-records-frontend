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

import play.api.libs.json.{Json, OFormat}

case class IncomeAndExpenditureSummary6076(
  totalGrossReceipts: BigDecimal,
  totalBaseLoadReceipts: BigDecimal,
  totalOtherIncome: BigDecimal,
  totalCostOfSales: BigDecimal,
  totalCostOfSalesIntermittent: BigDecimal,
  totalStaffCosts: BigDecimal,
  totalPremisesCosts: BigDecimal,
  totalOperationalExpenses: BigDecimal,
  headOfficeExpenses: BigDecimal,
  netProfitOrLoss: BigDecimal
)

object IncomeAndExpenditureSummary6076 {
  implicit val format: OFormat[IncomeAndExpenditureSummary6076] = Json.format
}
