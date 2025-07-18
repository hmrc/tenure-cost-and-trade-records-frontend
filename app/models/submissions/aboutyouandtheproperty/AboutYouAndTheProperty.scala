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

import actions.SessionRequest
import models.Session
import models.submissions.common.{Address, AnswersYesNo, CheckYourAnswersAndConfirm}
import play.api.libs.json.{Json, OFormat}

//There are now 22 attributes in this case class, so no more can be added

case class AboutYouAndTheProperty(
  customerDetails: Option[CustomerDetails] = None,
  altDetailsQuestion: Option[AnswersYesNo] = None,
  alternativeContactAddress: Option[Address] = None,
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
  checkYourAnswersAboutTheProperty: Option[CheckYourAnswersAndConfirm] = None,
  propertyDetailsString: Option[String] = None, // added for 6030 - February 2024
  charityQuestion: Option[AnswersYesNo] = None, // 6030
  tradingActivity: Option[TradingActivity] = None, // 6030
  renewablesPlant: Option[RenewablesPlantType] = None, // 6076
  threeYearsConstructed: Option[AnswersYesNo] = None, // 6076
  costsBreakdown: Option[String] = None // 6076
)

object AboutYouAndTheProperty:
  implicit val format: OFormat[AboutYouAndTheProperty] = Json.format

  def updateAboutYouAndTheProperty(
    copy: AboutYouAndTheProperty => AboutYouAndTheProperty
  )(implicit sessionRequest: SessionRequest[?]): Session =

    val currentAboutTheProperty = sessionRequest.sessionData.aboutYouAndTheProperty

    val updateAboutTheProperty = currentAboutTheProperty match {
      case Some(_) => sessionRequest.sessionData.aboutYouAndTheProperty.map(copy)
      case _       => Some(copy(AboutYouAndTheProperty()))
    }

    sessionRequest.sessionData.copy(aboutYouAndTheProperty = updateAboutTheProperty)
