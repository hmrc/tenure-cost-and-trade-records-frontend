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

case class CostOfSales6076Sum(
  fuelOrFeedstock: Option[BigDecimal],
  importedPower: Option[BigDecimal],
  TNuoS: Option[BigDecimal],
  BSuoS: Option[BigDecimal],
  other: Option[BigDecimal]
) {

  def total: BigDecimal = Seq(fuelOrFeedstock, importedPower, TNuoS, BSuoS, other).flatten.sum
}

object CostOfSales6076Sum {

  implicit val format: OFormat[CostOfSales6076Sum] = Json.format[CostOfSales6076Sum]
}
