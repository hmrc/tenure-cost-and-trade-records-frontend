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

package models.submissions.notconnected

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

case class RemoveConnectionDetails(
  removeConnectionDetails: Option[RemoveConnectionsDetails] = None,
  pastConnectionType: Option[AnswersYesNo] = None
)

object RemoveConnectionDetails:
  implicit val format: OFormat[RemoveConnectionDetails] = Json.format

  def updateRemoveConnectionDetails(
    copy: RemoveConnectionDetails => RemoveConnectionDetails
  )(implicit sessionRequest: SessionRequest[?]): Session =
    val currentRemoveConnectionDetails = sessionRequest.sessionData.removeConnectionDetails

    val updatedRemoveConnectionDetails = currentRemoveConnectionDetails match {
      case Some(_) => sessionRequest.sessionData.removeConnectionDetails.map(copy)
      case _       => Some(copy(RemoveConnectionDetails()))
    }

    sessionRequest.sessionData.copy(removeConnectionDetails = updatedRemoveConnectionDetails)
