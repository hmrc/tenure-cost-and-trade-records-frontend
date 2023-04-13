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

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.Json

case class AboutYouAndTheProperty(
  customerDetails: Option[CustomerDetails] = None,
  propertyDetails: Option[PropertyDetails] = None,
  websiteForPropertyDetails: Option[WebsiteForPropertyDetails] = None,
  premisesLicenseGrantedDetail: Option[AnswersYesNo] = None,
  premisesLicenseGrantedInformationDetails: Option[PremisesLicenseGrantedInformationDetails] = None,
  licensableActivities: Option[LicensableActivities] = None,
  licensableActivitiesInformationDetails: Option[LicensableActivitiesInformationDetails] = None,
  premisesLicenseConditions: Option[PremisesLicenseConditions] = None,
  premisesLicenseConditionsDetails: Option[PremisesLicenseConditionsDetails] = None,
  enforcementAction: Option[EnforcementAction] = None,
  enforcementActionHasBeenTakenInformationDetails: Option[EnforcementActionHasBeenTakenInformationDetails] = None,
  tiedForGoods: Option[TiedForGoods] = None,
  tiedForGoodsDetails: Option[TiedForGoodsInformationDetails] = None,
  checkYourAnswersAboutTheProperty: Option[CheckYourAnswersAboutYourProperty] = None
)

object AboutYouAndTheProperty {
  implicit val format = Json.format[AboutYouAndTheProperty]

  def updateAboutYouAndTheProperty(
    copy: AboutYouAndTheProperty => AboutYouAndTheProperty
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAboutTheProperty = sessionRequest.sessionData.aboutYouAndTheProperty

    val updateAboutTheProperty = currentAboutTheProperty match {
      case Some(_) => sessionRequest.sessionData.aboutYouAndTheProperty.map(copy)
      case _       => Some(copy(AboutYouAndTheProperty()))
    }

    sessionRequest.sessionData.copy(aboutYouAndTheProperty = updateAboutTheProperty)

  }
}
