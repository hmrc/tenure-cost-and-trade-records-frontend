/*
 * Copyright 2024 HM Revenue & Customs
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

package models.submissions.aboutYourLeaseOrTenure

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

case class AboutLeaseOrAgreementPartFour(
  rentIncludeStructuresBuildings: Option[AnswersYesNo] = None, // Added Aug 2024 for 6045/46
  rentIncludeStructuresBuildingsDetails: Option[String] = None, // Added Aug 2024 for 6045/46
  surrenderedLeaseAgreementDetails: Option[SurrenderedLeaseAgreementDetails] = None // Added Aug 2024 for 6045/46
)

object AboutLeaseOrAgreementPartFour {
  implicit val format: OFormat[AboutLeaseOrAgreementPartFour] = Json.format

  def updateAboutLeaseOrAgreementPartFour(
    copy: AboutLeaseOrAgreementPartFour => AboutLeaseOrAgreementPartFour
  )(implicit sessionRequest: SessionRequest[?]): Session = {
    val currentAboutLeaseOrAgreementPartFour = sessionRequest.sessionData.aboutLeaseOrAgreementPartFour

    val updatedAboutLeaseOrAgreementPartFour = currentAboutLeaseOrAgreementPartFour match {
      case Some(_) => sessionRequest.sessionData.aboutLeaseOrAgreementPartFour.map(copy)
      case _       => Some(copy(AboutLeaseOrAgreementPartFour()))
    }

    sessionRequest.sessionData.copy(aboutLeaseOrAgreementPartFour = updatedAboutLeaseOrAgreementPartFour)
  }
}
