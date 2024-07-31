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

import java.time.LocalDate

/**
  * 6076 Trading history.
  *
  * @author Yuriy Tumakha
  */
case class TurnoverSection6076(
  financialYearEnd: LocalDate,
  tradingPeriod: Int,
  electricityGenerated: Option[String] = None,
  otherIncome: Option[BigDecimal] = None,
  costOfSales6076Sum: Option[CostOfSales6076Sum] = None,
  costOfSales6076IntermittentSum: Option[CostOfSales6076IntermittentSum] = None,
  operationalExpenses: Option[OperationalExpenses] = None,
  headOfficeExpenses: Option[BigDecimal] = None,
  staffCosts: Option[StaffCosts] = None,
  grossReceiptsExcludingVAT: Option[GrossReceiptsExcludingVAT] = None,
  grossReceiptsForBaseLoad: Option[GrossReceiptsForBaseLoad] = None,
  premisesCosts: Option[PremisesCosts] = None,
  incomeAndExpenditureSummary: Option[IncomeAndExpenditureSummary6076] = None
)

object TurnoverSection6076 {
  implicit val format: OFormat[TurnoverSection6076] = Json.format
}
