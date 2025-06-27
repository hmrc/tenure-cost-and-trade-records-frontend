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

package models.submissions.aboutyouandtheproperty

import crypto.MongoCrypto
import models.submissions.common.{AnswersYesNo, SensitiveAddress}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveAboutYouAndTheProperty(
  customerDetails: Option[SensitiveCustomerDetails] = None,
  altDetailsQuestion: Option[AnswersYesNo] = None,
  alternativeContactAddress: Option[SensitiveAddress] = None,
  propertyDetails: Option[PropertyDetails] = None,
  websiteForPropertyDetails: Option[WebsiteForPropertyDetails] = None,
  premisesLicenseGrantedDetail: Option[AnswersYesNo] = None,
  premisesLicenseGrantedInformationDetails: Option[String] = None,
  licensableActivities: Option[AnswersYesNo] = None,
  licensableActivitiesInformationDetails: Option[String] = None,
  premisesLicenseConditions: Option[AnswersYesNo] = None,
  premisesLicenseConditionsDetails: Option[String] = None,
  enforcementAction: Option[AnswersYesNo] = None,
  enforcementActionHasBeenTakenInformationDetails: Option[String] = None,
  tiedForGoods: Option[AnswersYesNo] = None,
  tiedForGoodsDetails: Option[TiedForGoodsInformationDetails] = None,
  checkYourAnswersAboutTheProperty: Option[AnswersYesNo] = None,
  propertyDetailsString: Option[String] = None,
  charityQuestion: Option[AnswersYesNo] = None,
  tradingActivity: Option[TradingActivity] = None,
  renewablesPlant: Option[RenewablesPlantType] = None,
  threeYearsConstructed: Option[AnswersYesNo] = None,
  costsBreakDown: Option[String] = None
) extends Sensitive[AboutYouAndTheProperty] {

  override def decryptedValue: AboutYouAndTheProperty = AboutYouAndTheProperty(
    customerDetails.map(_.decryptedValue),
    altDetailsQuestion,
    alternativeContactAddress.map(_.decryptedValue),
    propertyDetails,
    websiteForPropertyDetails,
    premisesLicenseGrantedDetail,
    premisesLicenseGrantedInformationDetails,
    licensableActivities,
    licensableActivitiesInformationDetails,
    premisesLicenseConditions,
    premisesLicenseConditionsDetails,
    enforcementAction,
    enforcementActionHasBeenTakenInformationDetails,
    tiedForGoods,
    tiedForGoodsDetails,
    checkYourAnswersAboutTheProperty,
    propertyDetailsString,
    charityQuestion,
    tradingActivity,
    renewablesPlant,
    threeYearsConstructed,
    costsBreakDown
  )
}

object SensitiveAboutYouAndTheProperty {

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAboutYouAndTheProperty] = Json.format

  def apply(aboutYouAndTheProperty: AboutYouAndTheProperty): SensitiveAboutYouAndTheProperty =
    SensitiveAboutYouAndTheProperty(
      aboutYouAndTheProperty.customerDetails.map(SensitiveCustomerDetails(_)),
      aboutYouAndTheProperty.altDetailsQuestion,
      aboutYouAndTheProperty.alternativeContactAddress.map(SensitiveAddress(_)),
      aboutYouAndTheProperty.propertyDetails,
      aboutYouAndTheProperty.websiteForPropertyDetails,
      aboutYouAndTheProperty.premisesLicenseGrantedDetail,
      aboutYouAndTheProperty.premisesLicenseGrantedInformationDetails,
      aboutYouAndTheProperty.licensableActivities,
      aboutYouAndTheProperty.licensableActivitiesInformationDetails,
      aboutYouAndTheProperty.premisesLicenseConditions,
      aboutYouAndTheProperty.premisesLicenseConditionsDetails,
      aboutYouAndTheProperty.enforcementAction,
      aboutYouAndTheProperty.enforcementActionHasBeenTakenInformationDetails,
      aboutYouAndTheProperty.tiedForGoods,
      aboutYouAndTheProperty.tiedForGoodsDetails,
      aboutYouAndTheProperty.checkYourAnswersAboutTheProperty,
      aboutYouAndTheProperty.propertyDetailsString,
      aboutYouAndTheProperty.charityQuestion,
      aboutYouAndTheProperty.tradingActivity,
      aboutYouAndTheProperty.renewablesPlant,
      aboutYouAndTheProperty.threeYearsConstructed,
      aboutYouAndTheProperty.costsBreakdown
    )
}
