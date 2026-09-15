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

class AboutFranchisesOrLettingsIdentifiersSpec extends BaseSpec:

  "Franchise or lettings page identifiers" should {
    "be correct for the franchise or lettings page" in {
      FranchiseOrLettingsTiedToPropertyId.toString shouldBe "franchiseOrLettingsTiedToPropertyPage"
    }

    "be correct for the rent received from page" in {
      RentReceivedFromPageId.toString shouldBe "rentReceivedFromPage"
    }

    "be correct for the catering operations business page" in {
      CateringOperationBusinessPageId.toString shouldBe "cateringOperationBusinessPage"
    }

    "be correct for the fee received page" in {
      FeeReceivedPageId.toString shouldBe "feeReceivedPage"
    }

    "be correct for the concession type details page" in {
      ConcessionTypeDetailsId.toString shouldBe "concessionTypeDetailsPage"
    }

    "be correct for the concession type fees page" in {
      ConcessionTypeFeesId.toString shouldBe "concessionTypeFeesPage"
    }

    "be correct for the franchise type details page" in {
      FranchiseTypeDetailsId.toString shouldBe "franchiseTypeDetailsPage"
    }

    "be correct for the letting type details page" in {
      LettingTypeDetailsId.toString shouldBe "lettingTypeDetailsPage"
    }

    "be correct for the rental income page" in {
      RentalIncomeRentId.toString shouldBe "rentalIncomePage"
    }

    "be correct for the rental income included page" in {
      RentalIncomeIncludedId.toString shouldBe "rentalIncomeIncludedPage"
    }

    "be correct for the calculating the rent for page" in {
      CalculatingTheRentForPageId.toString shouldBe "calculatingTheRentForPage"
    }

    "be correct for the add another concession page" in {
      AddAnotherConcessionPageId.toString shouldBe "addAnotherConcessionRoutingPage"
    }

    "be correct for the max number of lettings reached catering page" in {
      MaxOfLettingsReachedCateringId.toString shouldBe "MaxOfLettingsReachedCateringPage"
    }

    "be correct for the max number of lettings reached current page" in {
      MaxOfLettingsReachedCurrentId.toString shouldBe "MaxOfLettingsReachedCurrentPage"
    }

    "be correct for the franchise or lettings CYA page" in {
      CheckYourAnswersAboutFranchiseOrLettingsId.toString shouldBe "checkYourAnswersAboutFranchiseOrLettingsPage"
    }
  }
