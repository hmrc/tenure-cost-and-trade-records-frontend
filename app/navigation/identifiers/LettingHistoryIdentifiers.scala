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

package navigation.identifiers

case object PermanentResidentsPageId extends Identifier:
  override def toString: String = "permanentResidentsPage"

case object ResidentDetailPageId extends Identifier:
  override def toString: String = "residentDetailPage"

case object ResidentRemovePageId extends Identifier:
  override def toString: String = "residentRemovePage"

case object ResidentListPageId extends Identifier:
  override def toString: String = "residentListPage"

case object MaxNumberReachedPageId extends Identifier:
  override def toString: String = "maxNumberReachedPage"

case object CompletedLettingsPageId extends Identifier:
  override def toString: String = "commercialLettingsPage"

case object OccupierDetailPageId extends Identifier:
  override def toString: String = "occupierDetailPage"

case object RentalPeriodPageId extends Identifier:
  override def toString: String = "rentalPeriodPage"

case object OccupierRemovePageId extends Identifier:
  override def toString: String = "occupierRemovePage"

case object OccupierListPageId extends Identifier:
  override def toString: String = "occupierListPage"

case object HowManyNightsPageId extends Identifier:
  override def toString: String = "howManyNightsPage"

case object HasStoppedLettingPageId extends Identifier:
  override def toString: String = "hasStoppedLettingPage"

case object WhenWasLastLetPageId extends Identifier:
  override def toString: String = "whenWasLastLetPage"

case object AdvertisingOnlinePageId extends Identifier:
  override def toString: String = "advertisingOnlinePage"

case object AdvertisingOnlineDetailsPageId extends Identifier:
  override def toString: String = "advertisingOnlineDetailsPage"

case object AdvertisingListPageId extends Identifier:
  override def toString: String = "advertisingListPage"

case object AdvertisingRemovePageId extends Identifier:
  override def toString: String = "advertisingRemovePage"

extension (string: String)
  def asPageIdentifier: Option[Identifier] = string match
    case "permanentResidentsPage"       => Some(PermanentResidentsPageId)
    case "residentDetailPage"           => Some(ResidentDetailPageId)
    case "residentRemovePage"           => Some(ResidentRemovePageId)
    case "residentListPage"             => Some(ResidentListPageId)
    case "maxNumberReachedPage"         => Some(MaxNumberReachedPageId)
    case "completedLettingsPage"        => Some(CompletedLettingsPageId)
    case "occupierDetailPage"           => Some(OccupierDetailPageId)
    case "rentalPeriodPage"             => Some(RentalPeriodPageId)
    case "occupierRemovePage"           => Some(OccupierRemovePageId)
    case "occupierListPage"             => Some(OccupierListPageId)
    case "howManyNightsPage"            => Some(HowManyNightsPageId)
    case "hasStoppedLettingPage"        => Some(HasStoppedLettingPageId)
    case "whenWasLastLetPage"           => Some(WhenWasLastLetPageId)
    case "advertisingOnlinePage"        => Some(AdvertisingOnlinePageId)
    case "advertisingOnlineDetailsPage" => Some(AdvertisingOnlineDetailsPageId)
    case "advertisingListPage"          => Some(AdvertisingListPageId)
    case "advertisingRemovePage"        => Some(AdvertisingRemovePageId)
    case _                              => None
