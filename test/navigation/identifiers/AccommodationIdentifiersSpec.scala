/*
 * Copyright 2026 HM Revenue & Customs
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

import uk.gov.hmrc.vo.unit.test.BaseSpec

class AccommodationIdentifiersSpec extends BaseSpec:

  "Accommodation unit page identifiers" should {
    "be correct for the accommodation unit page" in {
      AccommodationUnitPageId.toString shouldBe "accommodationUnitPage"
    }

    "be correct for the available rooms page" in {
      AvailableRoomsPageId.toString shouldBe "availableRoomsPage"
    }

    "be correct for the accommodation letting history page" in {
      AccommodationLettingHistoryPageId.toString shouldBe "accommodationLettingHistoryPage"
    }

    "be correct for the high season tariff page" in {
      HighSeasonTariffPageId.toString shouldBe "highSeasonTariffPage"
    }

    "be correct for the included tariff items page" in {
      IncludedTariffItemsPageId.toString shouldBe "includedTariffItemsPage"
    }

    "be correct for the accommodation unit list page" in {
      AccommodationUnitListPageId.toString shouldBe "accommodationUnitListPage"
    }

    "be correct for the added maximum accommodation units page" in {
      AddedMaximumAccommodationUnitsPageId.toString shouldBe "addedMaximumAccommodationUnitsPage"
    }

    "be correct for the accommodation details CYA page" in {
      AccommodationDetailsCYAPageId.toString shouldBe "accommodationDetailsCYAPage"
    }
  }
