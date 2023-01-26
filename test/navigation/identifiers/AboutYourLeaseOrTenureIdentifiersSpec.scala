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

class AboutYourLeaseOrTenureIdentifiersSpec extends TestBaseSpec {

  "About your lease or tenure identifiers" when {

    "Identifier for about your landlord page" in {
      assert(AboutTheLandlordPageId.toString.equals("aboutTheLandlordPage"))
    }

    "Identifier for current annual rent page" in {
      assert(CurrentAnnualRentPageId.toString.equals("currentAnnualRentPage"))
    }

    "Identifier for lease agreement details page" in {
      assert(LeaseOrAgreementDetailsPageId.toString.equals("leaseOrAgreementDetailsPage"))
    }

    "Identifier for current rent payable with 12 months page" in {
      assert(CurrentRentPayableWithin12monthsPageId.toString.equals("currentRentPayableWithin12monthsPage"))
    }

    "Identifier for current rent first paid page" in {
      assert(CurrentRentFirstPaidPageId.toString.equals("currentRentFirstPaidPage"))
    }

    "Identifier for tenancy lease agreement expire page (6011 only)" in {
      assert(TenancyLeaseAgreementExpirePageId.toString.equals("tenancyLeaseAgreementExpirePage"))
    }
  }
}
