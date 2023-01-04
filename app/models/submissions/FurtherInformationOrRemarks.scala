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

package models.submissions

import actions.SessionRequest
import models.Session
import models.submissions.Form6010.{AlternativeContactDetails, FurtherInformationOrRemarksDetails}
import play.api.libs.json.Json

case class FurtherInformationOrRemarks(
                                        furtherInfo: Option[FurtherInformationOrRemarksDetails] = None,
                                        alternativeContactDetails: Option[AlternativeContactDetails] = None
                     )

object FurtherInformationOrRemarks {
  implicit val format = Json.format[FurtherInformationOrRemarks]

  def updateFurtherInformationOrRemarks(copy: FurtherInformationOrRemarks => FurtherInformationOrRemarks)(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentFurtherInformationOrRemarks = sessionRequest.sessionData.furtherInformationOrRemarks

    val updatedFurtherInformationOrRemarks = currentFurtherInformationOrRemarks match {
      case Some(_) => sessionRequest.sessionData.furtherInformationOrRemarks.map(copy)
      case _       => Some(copy(FurtherInformationOrRemarks()))
    }

    sessionRequest.sessionData.copy(furtherInformationOrRemarks = updatedFurtherInformationOrRemarks)

  }
}
