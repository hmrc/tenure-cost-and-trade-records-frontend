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

class LettingHistoryIdentifiersSpec extends TestBaseSpec {

  "Letting history identifiers" when {

    "Identifier for permanent residents page" in
      assert(HasPermanentResidentsPageId.toString.equals("hasPermanentResidentsPage"))

    "Identifier for resident details page" in
      assert(ResidentDetailPageId.toString.equals("residentDetailPage"))

    "Identifier for resident remove page" in
      assert(ResidentRemovePageId.toString.equals("residentRemovePage"))

    "Identifier for resident list page" in
      assert(ResidentListPageId.toString.equals("residentListPage"))

    "Identifier for max number reached page" in
      assert(MaxNumberReachedPageId.toString.equals("maxNumberReachedPage"))

    "Identifier for has completed lettings page" in
      assert(HasCompletedLettingsPageId.toString.equals("hasCompletedLettings"))

    "Identifier for occupier detail page" in
      assert(OccupierDetailPageId.toString.equals("occupierDetailPage"))

    "Identifier for rental period page" in
      assert(RentalPeriodPageId.toString.equals("rentalPeriodPage"))

    "Identifier for occupier remove page" in
      assert(OccupierRemovePageId.toString.equals("occupierRemovePage"))

    "Identifier for occupier list page" in
      assert(OccupierListPageId.toString.equals("occupierListPage"))

    "Identifier for how many nights page" in
      assert(HowManyNightsPageId.toString.equals("howManyNightsPage"))

    "Identifier for has stopped letting page" in
      assert(HasStoppedLettingPageId.toString.equals("hasStoppedLettingPage"))

    "Identifier for when was last let page" in
      assert(WhenWasLastLetPageId.toString.equals("whenWasLastLetPage"))

    "Identifier for is yearly available page" in
      assert(IsYearlyAvailablePageId.toString.equals("isYearlyAvailablePage"))

    "Identifier for trading season length page" in
      assert(TradingSeasonLengthPageId.toString.equals("tradingSeasonLengthPage"))

    "Identifier for has online advertising page" in
      assert(HasOnlineAdvertisingPageId.toString.equals("hasOnlineAdvertisingPage"))

    "Identifier for advertising detail page" in
      assert(AdvertisingDetailPageId.toString.equals("onlineAdvertisingDetailPage"))

    "Identifier for advertising list page" in
      assert(AdvertisingListPageId.toString.equals("advertisingListPage"))

    "Identifier for advertising remove page" in
      assert(AdvertisingRemovePageId.toString.equals("advertisingRemovePage"))

    "Identifier for check your answers page" in
      assert(CheckYourAnswersPageId.toString.equals("checkYourAnswers"))

  }
}
