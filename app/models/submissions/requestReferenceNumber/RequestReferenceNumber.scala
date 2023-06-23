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

package models.submissions.requestReferenceNumber

import actions.SessionRequest
import models.Session
import play.api.libs.json.Json

case class RequestReferenceNumber(
  noReferenceNumberAddress: Option[NoReferenceNumber] = None,
  noReferenceContactDetails: Option[NoReferenceNumberContactDetails] = None
)

object RequestReferenceNumber {
  implicit val format = Json.format[RequestReferenceNumber]

  def updateRequestReferenceNumber(
    copy: RequestReferenceNumber => RequestReferenceNumber
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentRequestReferenceNumber = sessionRequest.sessionData.requestReferenceNumber

    val updatedRequestReferenceNumber = currentRequestReferenceNumber match {
      case Some(_) => sessionRequest.sessionData.requestReferenceNumber.map(copy)
      case _       => Some(copy(RequestReferenceNumber()))
    }

    sessionRequest.sessionData.copy(requestReferenceNumber = updatedRequestReferenceNumber)

  }
}
