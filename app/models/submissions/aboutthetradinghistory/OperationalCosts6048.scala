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

/**
  * @author Yuriy Tumakha
  */
case class OperationalCosts6048(
  energyBills: Option[BigDecimal] = None,
  laundryCleaning: Option[BigDecimal] = None,
  repairsRenewalsMaintenance: Option[BigDecimal] = None,
  tvLicences: Option[BigDecimal] = None,
  travellingAndMotorExpenses: Option[BigDecimal] = None,
  other: Option[BigDecimal] = None // other details in AboutTheTradingHistoryPartOne.otherOperationalExpensesDetails
) {
  def total: BigDecimal = Seq(
    energyBills,
    laundryCleaning,
    repairsRenewalsMaintenance,
    tvLicences,
    travellingAndMotorExpenses,
    other
  ).flatten.sum
}

object OperationalCosts6048 {
  implicit val format: OFormat[OperationalCosts6048] = Json.format
}
