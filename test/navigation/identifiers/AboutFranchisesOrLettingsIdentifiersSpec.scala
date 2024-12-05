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

class AboutFranchisesOrLettingsIdentifiersSpec extends TestBaseSpec {

  "About franchise or lettings identifiers" when {

    "Identifier for franchise or lettings page" in {
      assert(FranchiseOrLettingsTiedToPropertyId.toString.equals("franchiseOrLettingsTiedToPropertyPage"))
    }

    "Identifier for rent from concession page" in {
      assert(RentFromConcessionId.toString.equals("rentFromConcession"))
    }

    "Identifier for rent received from page" in {
      assert(RentReceivedFromPageId.toString.equals("rentReceivedFromPage"))
    }

    "Identifier for catering operations page" in {
      assert(CateringOperationPageId.toString.equals("cateringOperationPage"))
    }

    "Identifier for catering operations business page" in {
      assert(CateringOperationBusinessPageId.toString.equals("cateringOperationBusinessPage"))
    }

    "Identifier for fee received page" in {
      assert(FeeReceivedPageId.toString.equals("feeReceivedPage"))
    }

    "Identifier for concession type  details received page" in {
      assert(ConcessionTypeDetailsId.toString.equals("concessionTypeDetailsPage"))
    }
    "Identifier for concession type  fee received page" in {
      assert(ConcessionTypeFeesId.toString.equals("concessionTypeFeesPage"))
    }

    "Identifier for letting type  details page" in {
      assert(LettingTypeDetailsId.toString.equals("lettingTypeDetailsPage"))
    }

    "Identifier for letting type  rent page" in {
      assert(LettingTypeRentId.toString.equals("lettingTypeRentPage"))
    }

    "Identifier for letting type included page" in {
      assert(LettingTypeIncludedId.toString.equals("lettingTypeIncludedPage"))
    }

    "Identifier for catering operations details page" in {
      assert(CateringOperationDetailsPageId.toString.equals("cateringOperationDetailsPage"))
    }

    "Identifier for catering operations rent details page" in {
      assert(CateringOperationRentDetailsPageId.toString.equals("cateringOperationRentDetailsPage"))
    }

    "Identifier for calculating the rent for included page" in {
      assert(CalculatingTheRentForPageId.toString.equals("calculatingTheRentForPage"))
    }

    "Identifier for catering operations rent included page" in {
      assert(CateringOperationRentIncludesPageId.toString.equals("cateringOperationRentIncludesPage"))
    }

    "Identifier for add another catering operations page" in {
      assert(AddAnotherCateringOperationPageId.toString.equals("addAnotherCateringOperationPage"))
    }

    "Identifier for add another concession operation page" in {
      assert(AddAnotherCateringOperationPageId.toString.equals("addAnotherCateringOperationPage"))
    }
    "Identifier for add another concession page" in {
      assert(AddAnotherConcessionPageId.toString.equals("addAnotherConcessionRoutingPage"))
    }

    "Identifier for max number of lettings reached page" in {
      assert(MaxOfLettingsReachedCateringId.toString.equals("MaxOfLettingsReachedCateringPage"))
    }

    "Identifier for max number of lettings page" in {
      assert(MaxOfLettingsReachedCurrentId.toString.equals("MaxOfLettingsReachedCurrentPage"))
    }

    "Identifier for lettings page" in {
      assert(LettingAccommodationPageId.toString.equals("lettingAccommodationPage"))
    }

    "Identifier for lettings details page" in {
      assert(LettingAccommodationDetailsPageId.toString.equals("lettingAccommodationDetailsPage"))
    }

    "Identifier for lettings rent details page" in {
      assert(LettingAccommodationRentDetailsPageId.toString.equals("lettingAccommodationRentDetailsPage"))
    }

    "Identifier for lettings rent included page" in {
      assert(LettingAccommodationRentIncludesPageId.toString.equals("lettingAccommodationRentIncludesPage"))
    }

    "Identifier for add another lettings operations page" in {
      assert(AddAnotherLettingAccommodationPageId.toString.equals("addAnotherLettingAccommodationPage"))
    }

    "Identifier for concession or franchise fee page" in {
      assert(ConcessionOrFranchiseFeePageId.toString.equals("concessionOrFranchiseFeePage"))
    }

    "Identifier for cya franchise or lettings page" in {
      assert(CheckYourAnswersAboutFranchiseOrLettingsId.toString.equals("checkYourAnswersAboutFranchiseOrLettingsPage"))
    }

  }
}
