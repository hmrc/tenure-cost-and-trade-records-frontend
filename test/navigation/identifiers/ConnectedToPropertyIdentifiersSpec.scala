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

class ConnectedToPropertyIdentifiersSpec extends BaseSpec:

  "Connection to the property page identifiers" should {
    "be correct for the sign in page" in {
      SignInPageId.toString shouldBe "signInPage"
    }

    "be correct for the are you still connected page" in {
      AreYouStillConnectedPageId.toString shouldBe "areYouStillConnectedPage"
    }

    "be correct for the edit address page" in {
      EditAddressPageId.toString shouldBe "editAddressPage"
    }

    "be correct for the connection to the property page" in {
      ConnectionToPropertyPageId.toString shouldBe "ConnectionToPropertyPage"
    }

    "be correct for the vacant properties page" in {
      VacantPropertiesPageId.toString shouldBe "VacantPropertiesPage"
    }

    "be correct for the become vacant page" in {
      PropertyBecomeVacantPageId.toString shouldBe "PropertyBecomeVacantPage"
    }

    "be correct for the letting income page" in {
      LettingIncomePageId.toString shouldBe "LettingIncomePage"
    }

    "be correct for the no reference number page" in {
      NoReferenceNumberPageId.toString shouldBe "NoReferenceNumberPage"
    }

    "be correct for the no reference number contact details page" in {
      NoReferenceNumberContactDetailsPageId.toString shouldBe "NoReferenceNumberContactDetailsPage"
    }

    "be correct for the request reference number CYA page" in {
      CheckYourAnswersRequestReferenceNumberPageId.toString shouldBe "CheckYourAnswersRequestReferenceNumberPage"
    }

    "be correct for the connection to property page" in {
      ConnectionToPropertyPageId.toString shouldBe "ConnectionToPropertyPage"
    }

    "be correct for the trading name operating from property page" in {
      TradingNameOperatingFromPropertyPageId.toString shouldBe "TradingNameOperatingFromProperty"
    }

    "be correct for the trading name own the property page" in {
      TradingNameOwnThePropertyPageId.toString shouldBe "TradingNameOwnTheProperty"
    }

    "be correct for the trading name paying rent page" in {
      TradingNamePayingRentPageId.toString shouldBe "TradingNamePayingRentPage"
    }

    "be correct for the provide your contact details page" in {
      ProvideYourContactDetailsPageId.toString shouldBe "ProvideYourContactDetailsPage"
    }

    "be correct for the are you third party page" in {
      AreYouThirdPartyPageId.toString shouldBe "AreYouThirdParty"
    }

    "be correct for the letting part of the property details page" in {
      LettingPartOfPropertyDetailsPageId.toString shouldBe "LettingPartOfPropertyDetailsPage"
    }

    "be correct for the letting part of the property rent details page" in {
      LettingPartOfPropertyRentDetailsPageId.toString shouldBe "LettingPartOfPropertyRentDetailsPage"
    }

    "be correct for the letting part of the property items included in rent page" in {
      LettingPartOfPropertyItemsIncludedInRentPageId.toString shouldBe "LettingPartOfPropertyItemsIncludedInRentPage"
    }

    "be correct for the add another letting part of the property page" in {
      AddAnotherLettingPartOfPropertyPageId.toString shouldBe "AddAnotherLettingPartOfPropertyPage"
    }

    "be correct for the connection to the property CYA page" in {
      CheckYourAnswersConnectionToPropertyId.toString shouldBe "CheckYourAnswersConnectionToPropertyPage"
    }

    "be correct for the max lettings reached page" in {
      MaxOfLettingsReachedId.toString shouldBe "MaxOfLettingsReachedPage"
    }
  }
