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

package models.submissions.additionalinformation

import actions.SessionRequest
import models.Session
import play.api.libs.json.{Json, OFormat}

case class AdditionalInformation(
  furtherInformationOrRemarksDetails: Option[FurtherInformationOrRemarksDetails] = None,
  checkYourAnswersAdditionalInformation: Option[CheckYourAnswersAdditionalInformation] = None
)

object AdditionalInformation {
  implicit val format: OFormat[AdditionalInformation] = Json.format[AdditionalInformation]

  def updateAdditionalInformation(
    copy: AdditionalInformation => AdditionalInformation
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAdditionalInformation = sessionRequest.sessionData.additionalInformation

    val updateAdditionalInformation = currentAdditionalInformation match {
      case Some(_) => sessionRequest.sessionData.additionalInformation.map(copy)
      case _       => Some(copy(AdditionalInformation()))
    }

    sessionRequest.sessionData.copy(additionalInformation = updateAdditionalInformation)

  }
}
