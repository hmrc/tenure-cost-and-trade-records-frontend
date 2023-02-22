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

    "Identifier for catering operations page" in {
      assert(CateringOperationPageId.toString.equals("cateringOperationPage"))
    }

    "Identifier for catering operations details page" in {
      assert(CateringOperationDetailsPageId.toString.equals("cateringOperationDetailsPage"))
    }

    "Identifier for catering operations rent details page" in {
      assert(CateringOperationRentDetailsPageId.toString.equals("cateringOperationRentDetailsPage"))
    }

    "Identifier for catering operations rent included page" in {
      assert(CateringOperationRentIncludesPageId.toString.equals("cateringOperationRentIncludesPage"))
    }

    "Identifier for add another catering operations page" in {
      assert(AddAnotherCateringOperationPageId.toString.equals("addAnotherCateringOperationPage"))
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

    "Identifier for concession or franchise page" in {
      assert(ConcessionOrFranchiseId.toString.equals("concessionOrFranchisePage"))
    }
  }
}
