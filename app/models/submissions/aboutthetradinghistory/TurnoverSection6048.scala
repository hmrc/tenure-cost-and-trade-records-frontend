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
  * 6048 Trading history.
  *
  * @author Yuriy Tumakha
  */
case class TurnoverSection6048(
  financialYearEnd: LocalDate,
  tradingPeriod: Int = 52,
  income: Option[Income6048] = None,
  fixedCosts: Option[FixedCosts6048] = None,
  accountingCosts: Option[AccountingCosts6048] = None,
  administrativeCosts: Option[AdministrativeCosts6048] = None
)

object TurnoverSection6048 {
  implicit val format: OFormat[TurnoverSection6048] = Json.format
}
