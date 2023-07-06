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

class ConnectedToPropertyIdentifiersSpec extends TestBaseSpec {

  "Connection to property identifiers" when {

    "Identifier for sign in page" in {
      assert(SignInPageId.toString.equals("signInPage"))
    }

    "Identifier for are you still connected page" in {
      assert(AreYouStillConnectedPageId.toString.equals("areYouStillConnectedPage"))
    }

    "Identifier for edit address page" in {
      assert(EditAddressPageId.toString.equals("editAddressPage"))
    }

    "Identifier for connection to the property page" in {
      assert(ConnectionToPropertyPageId.toString.equals("ConnectionToPropertyPage"))
    }

    "Identifier for no reference number page" in {
      assert(NoReferenceNumberPageId.toString.equals("NoReferenceNumberPage"))
    }

    "Identifier for no reference number contact details page" in {
      assert(NoReferenceNumberContactDetailsPageId.toString.equals("NoReferenceNumberContactDetailsPage"))
    }

    "Identifier for check your answers request reference number page" in {
      assert(CheckYourAnswersRequestReferenceNumberPageId.toString.equals("CheckYourAnswersRequestReferenceNumberPage"))
    }

    "Identifier for connection to property page" in {
      assert(ConnectionToPropertyPageId.toString.equals("ConnectionToPropertyPage"))
    }

    "Identifier for vacant properties page" in {
      assert(VacantPropertiesPageId.toString.equals("VacantPropertiesPage"))
    }

    "Identifier for cya request ref number page" in {
      assert(CheckYourAnswersRequestReferenceNumberPageId.toString.equals("CheckYourAnswersRequestReferenceNumberPage"))
    }

    "Identifier for download pdf reference page" in {
      assert(DownloadPDFReferenceNumberPageId.toString.equals("DownloadPDFReferenceNumberPage"))
    }

    "Identifier for property become vacant page" in {
      assert(PropertyBecomeVacantPageId.toString.equals("PropertyBecomeVacant"))
    }

    "Identifier for letting income page" in {
      assert(LettingIncomePageId.toString.equals("LettingIncome"))
    }

    "Identifier for trading name operating from property page" in {
      assert(TradingNameOperatingFromPropertyPageId.toString.equals("TradingNameOperatingFromProperty"))
    }

    "Identifier for trading name own the property page" in {
      assert(TradingNameOwnThePropertyPageId.toString.equals("TradingNameOwnTheProperty"))
    }

    "Identifier for are you third party page" in {
      assert(AreYouThirdPartyPageId.toString.equals("AreYouThirdParty"))
    }

    "Identifier for cya reference number page" in {
      assert(CheckYourAnswersRequestReferenceNumberPageId.toString.equals("CheckYourAnswersRequestReferenceNumberPage"))
    }

    "Identifier for cya about the property page" in {
      assert(CheckYourAnswersAboutThePropertyId.toString.equals("CheckYourAnswersAboutThePropertyPage"))
    }
  }
}
