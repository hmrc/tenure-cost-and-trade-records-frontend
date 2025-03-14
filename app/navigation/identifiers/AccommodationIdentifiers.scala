/*
 * Copyright 2025 HM Revenue & Customs
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

case object AccommodationUnitPageId extends Identifier:
  override def toString: String = "accommodationUnitPage"

case object AvailableRoomsPageId extends Identifier:
  override def toString: String = "availableRoomsPage"

case object AccommodationLettingHistoryPageId extends Identifier:
  override def toString: String = "accommodationLettingHistoryPage"

case object HighSeasonTariffPageId extends Identifier:
  override def toString: String = "highSeasonTariffPage"

case object IncludedTariffItemsPageId extends Identifier:
  override def toString: String = "includedTariffItemsPage"

case object AccommodationUnitListPageId extends Identifier:
  override def toString: String = "accommodationUnitListPage"

case object AddedMaximumAccommodationUnitsPageId extends Identifier:
  override def toString: String = "addedMaximumAccommodationUnitsPage"

case object AccommodationDetailsCYAPageId extends Identifier:
  override def toString: String = "accommodationDetailsCYAPage"
