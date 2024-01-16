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

package models.submissions.aboutyouandtheproperty

import crypto.MongoCrypto
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveAboutYouAndTheProperty(
  customerDetails: Option[SensitiveCustomerDetails] = None,
  altDetailsQuestion: Option[ContactDetailsQuestion] = None,
  altContactInformation: Option[SensitiveAlternativeContactDetails] = None,
  propertyDetails: Option[PropertyDetails] = None,
  websiteForPropertyDetails: Option[WebsiteForPropertyDetails] = None,
  premisesLicenseGrantedDetail: Option[AnswersYesNo] = None,
  premisesLicenseGrantedInformationDetails: Option[PremisesLicenseGrantedInformationDetails] = None,
  licensableActivities: Option[AnswersYesNo] = None,
  licensableActivitiesInformationDetails: Option[LicensableActivitiesInformationDetails] = None,
  premisesLicenseConditions: Option[AnswersYesNo] = None,
  premisesLicenseConditionsDetails: Option[PremisesLicenseConditionsDetails] = None,
  enforcementAction: Option[AnswersYesNo] = None,
  enforcementActionHasBeenTakenInformationDetails: Option[EnforcementActionHasBeenTakenInformationDetails] = None,
  tiedForGoods: Option[AnswersYesNo] = None,
  tiedForGoodsDetails: Option[TiedForGoodsInformationDetails] = None,
  checkYourAnswersAboutTheProperty: Option[CheckYourAnswersAboutYourProperty] = None
) extends Sensitive[AboutYouAndTheProperty] {
  override def decryptedValue: AboutYouAndTheProperty = AboutYouAndTheProperty(
    customerDetails.map(_.decryptedValue),
    altDetailsQuestion,
    altContactInformation.map(_.decryptedValue),
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
    checkYourAnswersAboutTheProperty
  )
}

object SensitiveAboutYouAndTheProperty {

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAboutYouAndTheProperty] =
    Json.format[SensitiveAboutYouAndTheProperty]

  def apply(aboutYouAndTheProperty: AboutYouAndTheProperty): SensitiveAboutYouAndTheProperty =
    SensitiveAboutYouAndTheProperty(
      aboutYouAndTheProperty.customerDetails.map(SensitiveCustomerDetails(_)),
      aboutYouAndTheProperty.altDetailsQuestion,
      aboutYouAndTheProperty.altContactInformation.map(SensitiveAlternativeContactDetails(_)),
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
      aboutYouAndTheProperty.checkYourAnswersAboutTheProperty
    )
}
