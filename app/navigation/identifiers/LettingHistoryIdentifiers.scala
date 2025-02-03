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

case object HasPermanentResidentsPageId extends Identifier:
  override def toString: String = "hasPermanentResidentsPage"

case object ResidentDetailPageId extends Identifier:
  override def toString: String = "residentDetailPage"

case object ResidentRemovePageId extends Identifier:
  override def toString: String = "residentRemovePage"

case object ResidentListPageId extends Identifier:
  override def toString: String = "residentListPage"

case object MaxNumberReachedPageId extends Identifier:
  override def toString: String = "maxNumberReachedPage"

case object HasCompletedLettingsPageId extends Identifier:
  override def toString: String = "hasCompletedLettings"

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

case object IsYearlyAvailablePageId extends Identifier:
  override def toString: String = "isYearlyAvailablePage"

case object TradingSeasonLengthPageId extends Identifier:
  override def toString: String = "tradingSeasonLengthPage"

case object HasOnlineAdvertisingPageId extends Identifier:
  override def toString: String = "hasOnlineAdvertisingPage"

case object AdvertisingDetailPageId extends Identifier:
  override def toString: String = "onlineAdvertisingDetailPage"

case object AdvertisingListPageId extends Identifier:
  override def toString: String = "advertisingListPage"

case object AdvertisingRemovePageId extends Identifier:
  override def toString: String = "AdvertisingRemovePageId"

case object CheckYourAnswersPageId extends Identifier:
  override def toString: String = "checkYourAnswers"

extension (string: String)
  def asPageIdentifier: Option[Identifier] = string match
    case "hasPermanentResidentsPage" => Some(HasPermanentResidentsPageId)
    case "residentDetailPage"        => Some(ResidentDetailPageId)
    case "residentRemovePage"        => Some(ResidentRemovePageId)
    case "residentListPage"          => Some(ResidentListPageId)
    case "maxNumberReachedPage"      => Some(MaxNumberReachedPageId)
    case "hasCompletedLettingsPage"  => Some(HasCompletedLettingsPageId)
    case "occupierDetailPage"        => Some(OccupierDetailPageId)
    case "rentalPeriodPage"          => Some(RentalPeriodPageId)
    case "occupierRemovePage"        => Some(OccupierRemovePageId)
    case "occupierListPage"          => Some(OccupierListPageId)
    case "howManyNightsPage"         => Some(HowManyNightsPageId)
    case "hasStoppedLettingPage"     => Some(HasStoppedLettingPageId)
    case "whenWasLastLetPage"        => Some(WhenWasLastLetPageId)
    case "isYearlyAvailablePage"     => Some(IsYearlyAvailablePageId)
    case "tradingSeasonLengthPage"   => Some(TradingSeasonLengthPageId)
    case "hasOnlineAdvertisingPage"  => Some(HasOnlineAdvertisingPageId)
    case "AdvertisingDetailPage"     => Some(AdvertisingDetailPageId)
    case "advertisingListPage"       => Some(AdvertisingListPageId)
    case "advertisingRemovePage"     => Some(AdvertisingRemovePageId)
    case "checkYourAnswers"          => Some(CheckYourAnswersPageId)
    case _                           => None
