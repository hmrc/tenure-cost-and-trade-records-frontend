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

class LettingHistoryIdentifiersSpec extends BaseSpec:

  "Letting history page identifiers" should {
    "be correct for the permanent residents page" in {
      HasPermanentResidentsPageId.toString shouldBe "hasPermanentResidentsPage"
    }

    "be correct for the resident details page" in {
      ResidentDetailPageId.toString shouldBe "residentDetailPage"
    }

    "be correct for the resident remove page" in {
      ResidentRemovePageId.toString shouldBe "residentRemovePage"
    }

    "be correct for the resident list page" in {
      ResidentListPageId.toString shouldBe "residentListPage"
    }

    "be correct for the max number reached page" in {
      MaxNumberReachedPageId.toString shouldBe "maxNumberReachedPage"
    }

    "be correct for the has completed lettings page" in {
      HasCompletedLettingsPageId.toString shouldBe "hasCompletedLettings"
    }

    "be correct for the occupier detail page" in {
      OccupierDetailPageId.toString shouldBe "occupierDetailPage"
    }

    "be correct for the rental period page" in {
      RentalPeriodPageId.toString shouldBe "rentalPeriodPage"
    }

    "be correct for the occupier remove page" in {
      OccupierRemovePageId.toString shouldBe "occupierRemovePage"
    }

    "be correct for the occupier list page" in {
      OccupierListPageId.toString shouldBe "occupierListPage"
    }

    "be correct for the how many nights page" in {
      HowManyNightsPageId.toString shouldBe "howManyNightsPage"
    }

    "be correct for the has stopped letting page" in {
      HasStoppedLettingPageId.toString shouldBe "hasStoppedLettingPage"
    }

    "be correct for the when was last let page" in {
      WhenWasLastLetPageId.toString shouldBe "whenWasLastLetPage"
    }

    "be correct for the is yearly available page" in {
      IsYearlyAvailablePageId.toString shouldBe "isYearlyAvailablePage"
    }

    "be correct for the trading season length page" in {
      TradingSeasonLengthPageId.toString shouldBe "tradingSeasonLengthPage"
    }

    "be correct for the has online advertising page" in {
      HasOnlineAdvertisingPageId.toString shouldBe "hasOnlineAdvertisingPage"
    }

    "be correct for the advertising details page" in {
      AdvertisingDetailPageId.toString shouldBe "onlineAdvertisingDetailPage"
    }

    "be correct for the advertising list page" in {
      AdvertisingListPageId.toString shouldBe "advertisingListPage"
    }

    "be correct for the advertising remove page" in {
      AdvertisingRemovePageId.toString shouldBe "advertisingRemovePage"
    }

    "be correct for the CYA page" in {
      CheckYourAnswersPageId.toString shouldBe "checkYourAnswers"
    }

    "Letting history identifiers correctly convert from strings" in {
      "hasPermanentResidentsPage".asPageIdentifier shouldBe Some(HasPermanentResidentsPageId)
      "residentDetailPage".asPageIdentifier        shouldBe Some(ResidentDetailPageId)
      "residentRemovePage".asPageIdentifier        shouldBe Some(ResidentRemovePageId)
      "residentListPage".asPageIdentifier          shouldBe Some(ResidentListPageId)
      "maxNumberReachedPage".asPageIdentifier      shouldBe Some(MaxNumberReachedPageId)
      "hasCompletedLettingsPage".asPageIdentifier  shouldBe Some(HasCompletedLettingsPageId)
      "occupierDetailPage".asPageIdentifier        shouldBe Some(OccupierDetailPageId)
      "rentalPeriodPage".asPageIdentifier          shouldBe Some(RentalPeriodPageId)
      "occupierRemovePage".asPageIdentifier        shouldBe Some(OccupierRemovePageId)
      "occupierListPage".asPageIdentifier          shouldBe Some(OccupierListPageId)
      "howManyNightsPage".asPageIdentifier         shouldBe Some(HowManyNightsPageId)
      "hasStoppedLettingPage".asPageIdentifier     shouldBe Some(HasStoppedLettingPageId)
      "whenWasLastLetPage".asPageIdentifier        shouldBe Some(WhenWasLastLetPageId)
      "isYearlyAvailablePage".asPageIdentifier     shouldBe Some(IsYearlyAvailablePageId)
      "tradingSeasonLengthPage".asPageIdentifier   shouldBe Some(TradingSeasonLengthPageId)
      "hasOnlineAdvertisingPage".asPageIdentifier  shouldBe Some(HasOnlineAdvertisingPageId)
      "AdvertisingDetailPage".asPageIdentifier     shouldBe Some(AdvertisingDetailPageId)
      "advertisingListPage".asPageIdentifier       shouldBe Some(AdvertisingListPageId)
      "advertisingRemovePage".asPageIdentifier     shouldBe Some(AdvertisingRemovePageId)
      "checkYourAnswers".asPageIdentifier          shouldBe Some(CheckYourAnswersPageId)
    }
  }
