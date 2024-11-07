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
case class FixedCosts6048(
  insurance: Option[BigDecimal] = None,
  businessRatesOrCouncilTax: Option[BigDecimal] = None,
  rent: Option[BigDecimal] = None
) {
  def total: BigDecimal = Seq(insurance, businessRatesOrCouncilTax, rent).flatten.sum
}

object FixedCosts6048 {
  implicit val format: OFormat[FixedCosts6048] = Json.format
}
