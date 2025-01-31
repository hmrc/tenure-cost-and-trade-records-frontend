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

import utils.TestBaseSpec

class AccommodationIdentifiersSpec extends TestBaseSpec {

  "Letting history identifiers" when {

    "Identifier for accommodation unit page" in
      assert(AccommodationUnitPageId.toString.equals("accommodationUnitPage"))

    "Identifier for available rooms page" in
      assert(AvailableRoomsPageId.toString.equals("availableRoomsPage"))

    "Identifier for accommodation letting history page" in
      assert(AccommodationLettingHistoryPageId.toString.equals("accommodationLettingHistoryPage"))

    "Identifier for high season tariff page" in
      assert(HighSeasonTariffPageId.toString.equals("highSeasonTariffPage"))

    "Identifier for included tariff items page" in
      assert(IncludedTariffItemsPageId.toString.equals("includedTariffItemsPage"))
  }
}
