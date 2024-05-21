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

class AboutYouAndThePropertyIdentifiersSpec extends TestBaseSpec {

  "About the property identifiers" when {

    "Identifier for about you page" in {
      assert(AboutYouPageId.toString.equals("aboutYouPage"))
    }

    "Identifier for charity question page" in {
      assert(CharityQuestionPageId.toString.equals("charityQuestionPage"))
    }

    "Identifier for contact details question page" in {
      assert(ContactDetailsQuestionId.toString.equals("contactDetailsQuestionPage"))
    }

    "Identifier for trading activity page" in {
      assert(TradingActivityPageId.toString.equals("tradingActivityPage"))
    }

    "Identifier for alt contact page" in {
      assert(AlternativeContactDetailsId.toString.equals("alternativeContactDetailsPage"))
    }

    "Identifier for about the property page" in {
      assert(AboutThePropertyPageId.toString.equals("aboutThePropertyPage"))
    }

    // 6030 page only
    "Identifier for about the property string page" in {
      assert(AboutThePropertyStringPageId.toString.equals("aboutThePropertyStringPage"))
    }

    "Identifier for website for property page" in {
      assert(WebsiteForPropertyPageId.toString.equals("websiteForPropertyPage"))
    }

    // 6015 page only
    "Identifier for premises licence granted page" in {
      assert(PremisesLicenseGrantedId.toString.equals("premisesLicenseGrantedPage"))
    }

    // 6015 page only
    "Identifier for premises licence granted details page" in {
      assert(PremisesLicenseGrantedDetailsId.toString.equals("premisesLicenseGrantedDetailsPage"))
    }

    "Identifier for licence activity page" in {
      assert(LicensableActivityPageId.toString.equals("licensableActivityPage"))
    }

    "Identifier for licence activity details page" in {
      assert(LicensableActivityDetailsPageId.toString.equals("licensableActivityDetailsPage"))
    }

    "Identifier for premise licence conditions page" in {
      assert(PremisesLicenceConditionsPageId.toString.equals("premisesLicenceConditionsPage"))
    }

    "Identifier for premise licence conditions details page" in {
      assert(PremisesLicenceConditionsDetailsPageId.toString.equals("premisesLicenceConditionsDetailsPage"))
    }

    "Identifier for enforcement action taken page" in {
      assert(EnforcementActionBeenTakenPageId.toString.equals("enforcementActionBeenTakenPage"))
    }

    "Identifier for enforcement action taken details page" in {
      assert(EnforcementActionBeenTakenDetailsPageId.toString.equals("enforcementActionBeenTakenDetailsPage"))
    }

    "Identifier for tied for goods page" in {
      assert(TiedForGoodsPageId.toString.equals("tiedForGoodsPage"))
    }

    "Identifier for tied for goods details page" in {
      assert(TiedForGoodsDetailsPageId.toString.equals("tiedForGoodsDetailsPage"))
    }

    "Identifier for renewables plant page" in {
      assert(RenewablesPlantPageId.toString.equals("renewablesPlantPage"))
    }

    "Identifier for three years constructed page" in {
      assert(ThreeYearsConstructedPageId.toString.equals("threeYearsConstructedPage"))
    }

    "Identifier for plants and technologies page" in {
      assert(BatteriesCapacityId.toString.equals("batteriesCapacityPage"))
    }

    "Identifier generator capacity page" in {
      assert(GeneratorCapacityId.toString.equals("generatorCapacityPage"))
    }

    "Identifier for batteries capacity page" in {
      assert(PlantAndTechnologyId.toString.equals("plantAndTechnologyPage"))
    }

    "Identifier for costs breakdown page" in {
      assert(CostsBreakdownId.toString.equals("costsBreakdownPage"))
    }

    "Identifier for check your answers page" in {
      assert(CheckYourAnswersAboutThePropertyPageId.toString.equals("checkYourAnswersAboutThePropertyPage"))
    }
  }
}
