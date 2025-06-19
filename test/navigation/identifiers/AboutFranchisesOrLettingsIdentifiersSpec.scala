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

import utils.TestBaseSpec

class AboutFranchisesOrLettingsIdentifiersSpec extends TestBaseSpec {

  "About franchise or lettings identifiers" when {

    "Identifier for franchise or lettings page" in
      assert(FranchiseOrLettingsTiedToPropertyId.toString.equals("franchiseOrLettingsTiedToPropertyPage"))

    "Identifier for rent received from page" in
      assert(RentReceivedFromPageId.toString.equals("rentReceivedFromPage"))

    "Identifier for catering operations business page" in
      assert(CateringOperationBusinessPageId.toString.equals("cateringOperationBusinessPage"))

    "Identifier for fee received page" in
      assert(FeeReceivedPageId.toString.equals("feeReceivedPage"))

    "Identifier for concession type  details received page" in
      assert(ConcessionTypeDetailsId.toString.equals("concessionTypeDetailsPage"))
    "Identifier for concession type  fee received page" in
      assert(ConcessionTypeFeesId.toString.equals("concessionTypeFeesPage"))

    "Identifier for franchise type  details page" in
      assert(FranchiseTypeDetailsId.toString.equals("franchiseTypeDetailsPage"))

    "Identifier for letting type  details page" in
      assert(LettingTypeDetailsId.toString.equals("lettingTypeDetailsPage"))

    "Identifier for letting type  rent page" in
      assert(RentalIncomeRentId.toString.equals("rentalIncomePage"))

    "Identifier for letting type included page" in
      assert(RentalIncomeIncludedId.toString.equals("rentalIncomeIncludedPage"))

    "Identifier for calculating the rent for included page" in
      assert(CalculatingTheRentForPageId.toString.equals("calculatingTheRentForPage"))

    "Identifier for add another concession page" in
      assert(AddAnotherConcessionPageId.toString.equals("addAnotherConcessionRoutingPage"))

    "Identifier for max number of lettings reached page" in
      assert(MaxOfLettingsReachedCateringId.toString.equals("MaxOfLettingsReachedCateringPage"))

    "Identifier for max number of lettings page" in
      assert(MaxOfLettingsReachedCurrentId.toString.equals("MaxOfLettingsReachedCurrentPage"))

    "Identifier for franchise type details page" in
      assert(FranchiseTypeDetailsId.toString.equals("franchiseTypeDetailsPage"))

    "Identifier for cya franchise or lettings page" in
      assert(CheckYourAnswersAboutFranchiseOrLettingsId.toString.equals("checkYourAnswersAboutFranchiseOrLettingsPage"))

  }
}
