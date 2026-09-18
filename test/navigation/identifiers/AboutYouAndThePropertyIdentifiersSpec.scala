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

class AboutYouAndThePropertyIdentifiersSpec extends BaseSpec:

  "About you and the property page identifiers" should {
    "be correct for the about you page" in {
      AboutYouPageId.toString shouldBe "aboutYouPage"
    }

    "be correct for the charity question page" in {
      CharityQuestionPageId.toString shouldBe "charityQuestionPage"
    }

    "be correct for the contact details question page" in {
      ContactDetailsQuestionId.toString shouldBe "contactDetailsQuestionPage"
    }

    "be correct for the trading activity page" in {
      TradingActivityPageId.toString shouldBe "tradingActivityPage"
    }

    "be correct for the about the property page" in {
      AboutThePropertyPageId.toString shouldBe "aboutThePropertyPage"
    }

    // 6030 page only
    "be correct for the about the property 6030 page" in {
      AboutThePropertyStringPageId.toString shouldBe "aboutThePropertyStringPage"
    }

    "be correct for the website for the property page" in {
      WebsiteForPropertyPageId.toString shouldBe "websiteForPropertyPage"
    }

    // 6015 page only
    "be correct for the premises licence granted page" in {
      PremisesLicenseGrantedId.toString shouldBe "premisesLicenseGrantedPage"
    }

    // 6015 page only
    "be correct for the premises licence granted details page" in {
      PremisesLicenseGrantedDetailsId.toString shouldBe "premisesLicenseGrantedDetailsPage"
    }

    "be correct for the licensable activity page" in {
      LicensableActivityPageId.toString shouldBe "licensableActivityPage"
    }

    "be correct for the licensable activity details page" in {
      LicensableActivityDetailsPageId.toString shouldBe "licensableActivityDetailsPage"
    }

    "be correct for the premises licence conditions page" in {
      PremisesLicenceConditionsPageId.toString shouldBe "premisesLicenceConditionsPage"
    }

    "be correct for the premises licence conditions details page" in {
      PremisesLicenceConditionsDetailsPageId.toString shouldBe "premisesLicenceConditionsDetailsPage"
    }

    "be correct for the enforcement action taken page" in {
      EnforcementActionBeenTakenPageId.toString shouldBe "enforcementActionBeenTakenPage"
    }

    "be correct for the enforcement action taken details page" in {
      EnforcementActionBeenTakenDetailsPageId.toString shouldBe "enforcementActionBeenTakenDetailsPage"
    }

    "be correct for the tied for goods page" in {
      TiedForGoodsPageId.toString shouldBe "tiedForGoodsPage"
    }

    "be correct for the tied for goods details page" in {
      TiedForGoodsDetailsPageId.toString shouldBe "tiedForGoodsDetailsPage"
    }

    "be correct for the renewables plant page" in {
      RenewablesPlantPageId.toString shouldBe "renewablesPlantPage"
    }

    "be correct for the three years constructed page" in {
      ThreeYearsConstructedPageId.toString shouldBe "threeYearsConstructedPage"
    }

    "be correct for the batteries capacity page" in {
      BatteriesCapacityId.toString shouldBe "batteriesCapacityPage"
    }

    "be correct for the commercial letting question page" in {
      CommercialLettingQuestionId.toString shouldBe "commercialLettingQuestionPage"
    }

    "be correct for the commercial letting availability page" in {
      CommercialLettingAvailabilityId.toString shouldBe "commercialLettingAvailabilityPage"
    }

    "be correct for the commercial letting availability Welsh page" in {
      CommercialLettingAvailabilityWelshId.toString shouldBe "commercialLettingAvailabilityWelshPage"
    }

    "be correct for the Occupiers Details page" in {
      OccupiersDetailsId.toString shouldBe "occupiersDetailsPage"
    }

    "be correct for the Occupiers Details List page" in {
      OccupiersDetailsListId.toString shouldBe "occupiersDetailsListPage"
    }

    "be correct for the completed lettings page" in {
      CompletedCommercialLettingsId.toString shouldBe "completedCommercialLettingsPage"
    }

    "be correct for the completed lettings Welsh page" in {
      CompletedCommercialLettingsWelshId.toString shouldBe "completedCommercialLettingsWelshPage"
    }

    "be correct for the parts unavailable page" in {
      PartsUnavailableId.toString shouldBe "partsUnavailablePage"
    }

    "be correct for the generator capacity page" in {
      GeneratorCapacityId.toString shouldBe "generatorCapacityPage"
    }

    "be correct for the plant and technology page" in {
      PlantAndTechnologyId.toString shouldBe "plantAndTechnologyPage"
    }

    "be correct for the costs breakdown page" in {
      CostsBreakdownId.toString shouldBe "costsBreakdownPage"
    }

    "be correct for the property currently used page" in {
      PropertyCurrentlyUsedPageId.toString shouldBe "propertyCurrentlyUsedPage"
    }

    "be correct for the about you and the property CYA page" in {
      CheckYourAnswersAboutThePropertyPageId.toString shouldBe "checkYourAnswersAboutThePropertyPage"
    }
  }
