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
  * 6045/6046 Trading history.
  *
  * @author Yuriy Tumakha
  */
case class TurnoverSection6045(
  financialYearEnd: LocalDate,
  // 3. Caravans
  grossReceiptsCaravanFleetHire: Option[GrossReceiptsCaravanFleetHire] = None,
  singleCaravansOwnedByOperator: Option[CaravansTrading6045] = None,
  singleCaravansSublet: Option[CaravansTrading6045] = None, // sub-let by operator on behalf of private owners
  twinUnitCaravansOwnedByOperator: Option[CaravansTrading6045] = None,
  twinUnitCaravansSublet: Option[CaravansTrading6045] = None, // sub-let by operator on behalf of private owners
  // 3.1.0 Other holiday accommodation
  grossReceiptsLettingUnits: Option[GrossReceiptsLettingUnits] = None,
  grossReceiptsSubLetUnits: Option[GrossReceiptsSubLetUnits] = None,
  // 3.2.0 Touring and tenting pitches
  pitchesForCaravans: Option[TentingPitchesTradingData] = None,
  pitchesForGlamping: Option[TentingPitchesTradingData] = None,
  rallyAreas: Option[RallyAreasTradingData] = None,
  // 3.3.0 Additional activities
  additionalShops: Option[AdditionalShops] = None,
  additionalCatering: Option[AdditionalCatering] = None,
  additionalBarsClubs: Option[AdditionalBarsClubs] = None,
  additionalAmusements: Option[AdditionalAmusements] = None
)

object TurnoverSection6045 {
  implicit val format: OFormat[TurnoverSection6045] = Json.format
}
