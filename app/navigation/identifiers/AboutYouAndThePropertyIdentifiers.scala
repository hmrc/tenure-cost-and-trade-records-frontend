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

case object AboutYouPageId extends Identifier { override def toString: String = "aboutYouPage" }

case object ContactDetailsQuestionId extends Identifier {
  override def toString: String = "contactDetailsQuestionPage"
}

case object CharityQuestionPageId extends Identifier {
  override def toString: String = "charityQuestionPage"
}

case object TradingActivityPageId extends Identifier {
  override def toString: String = "tradingActivityPage"
}

case object AboutThePropertyPageId extends Identifier { override def toString: String = "aboutThePropertyPage" }

//6045/46 page only
case object PropertyCurrentlyUsedPageId extends Identifier {
  override def toString: String = "propertyCurrentlyUsedPage"
}

// 6030 page only
case object AboutThePropertyStringPageId extends Identifier {
  override def toString: String = "aboutThePropertyStringPage"
}

case object WebsiteForPropertyPageId extends Identifier { override def toString: String = "websiteForPropertyPage" }

// 6015 page only
case object PremisesLicenseGrantedId extends Identifier { override def toString: String = "premisesLicenseGrantedPage" }

// 6015 page only
case object PremisesLicenseGrantedDetailsId extends Identifier {
  override def toString: String = "premisesLicenseGrantedDetailsPage"
}

case object LicensableActivityPageId extends Identifier { override def toString: String = "licensableActivityPage" }

case object LicensableActivityDetailsPageId extends Identifier {
  override def toString: String = "licensableActivityDetailsPage"
}

case object PremisesLicenceConditionsPageId extends Identifier {
  override def toString: String = "premisesLicenceConditionsPage"
}

case object PremisesLicenceConditionsDetailsPageId extends Identifier {
  override def toString: String = "premisesLicenceConditionsDetailsPage"
}

case object EnforcementActionBeenTakenPageId extends Identifier {
  override def toString: String = "enforcementActionBeenTakenPage"
}

case object EnforcementActionBeenTakenDetailsPageId extends Identifier {
  override def toString: String = "enforcementActionBeenTakenDetailsPage"
}

case object TiedForGoodsPageId extends Identifier { override def toString: String = "tiedForGoodsPage" }

case object TiedForGoodsDetailsPageId extends Identifier { override def toString: String = "tiedForGoodsDetailsPage" }

case object RenewablesPlantPageId extends Identifier {
  override def toString: String = "renewablesPlantPage"
}

case object ThreeYearsConstructedPageId extends Identifier {
  override def toString: String = "threeYearsConstructedPage"
}

case object BatteriesCapacityId extends Identifier {
  override def toString: String = "batteriesCapacityPage"
}

case object CommercialLettingQuestionId extends Identifier {
  override def toString: String = "commercialLettingQuestionPage"
}

case object CommercialLettingAvailabilityId extends Identifier {
  override def toString: String = "commercialLettingAvailabilityPage"
}

case object CommercialLettingAvailabilityWelshId extends Identifier {
  override def toString: String = "commercialLettingAvailabilityWelshPage"
}

case object OccupiersDetailsId extends Identifier {
  override def toString: String = "occupiersDetailsPage"
}

case object OccupiersDetailsListId extends Identifier {
  override def toString: String = "occupiersDetailsListPage"
}

case object GeneratorCapacityId extends Identifier {
  override def toString: String = "generatorCapacityPage"
}

case object CompletedCommercialLettingsId extends Identifier {
  override def toString: String = "completedCommercialLettingsPage"
}

case object CompletedCommercialLettingsWelshId extends Identifier {
  override def toString: String = "completedCommercialLettingsWelshPage"
}

case object PartsUnavailableId extends Identifier {
  override def toString: String = "partsUnavailablePage"
}

case object PlantAndTechnologyId extends Identifier {
  override def toString: String = "plantAndTechnologyPage"
}

case object CostsBreakdownId extends Identifier {
  override def toString: String = "costsBreakdownPage"
}

case object CheckYourAnswersAboutThePropertyPageId extends Identifier {
  override def toString: String = "checkYourAnswersAboutThePropertyPage"
}
