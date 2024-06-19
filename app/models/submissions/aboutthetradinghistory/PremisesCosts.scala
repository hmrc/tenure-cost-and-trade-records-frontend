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

case class PremisesCosts(
  energyAndUtilities: Option[BigDecimal],
  buildingRepairAndMaintenance: Option[BigDecimal],
  repairsAndRenewalsOfFixtures: Option[BigDecimal],
  rent: Option[BigDecimal],
  businessRates: Option[BigDecimal],
  buildingInsurance: Option[BigDecimal]
) {
  def total = Seq(
    energyAndUtilities,
    buildingRepairAndMaintenance,
    rent,
    repairsAndRenewalsOfFixtures,
    businessRates,
    buildingInsurance
  ).flatten.sum
}

object PremisesCosts {
  implicit val format: OFormat[PremisesCosts] = Json.format
}
