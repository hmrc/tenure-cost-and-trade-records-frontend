/*
 * Copyright 2022 HM Revenue & Customs
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

package models.submissions

import actions.SessionRequest
import models.Session
import models.submissions.Form6010.{PropertyDetails, WebsiteForPropertyDetails}
import play.api.libs.json.Json

case class SectionTwo(
  propertyDetails: Option[PropertyDetails] = None,
  websiteForPropertyDetails: Option[WebsiteForPropertyDetails] = None
)

object SectionTwo {
  implicit val format = Json.format[SectionTwo]

  def updateSectionTwo(copy: SectionTwo => SectionTwo)(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentSectionTwo = sessionRequest.sessionData.sectionTwo

    val updatedSectionTwo = currentSectionTwo match {
      case Some(_) => sessionRequest.sessionData.sectionTwo.map(copy)
      case _       => Some(copy(SectionTwo()))
    }

    sessionRequest.sessionData.copy(sectionTwo = updatedSectionTwo)

  }
}
