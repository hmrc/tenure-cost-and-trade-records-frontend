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

package models.submissions.requestReferenceNumber

import actions.SessionRequest
import models.Session
import play.api.libs.json.{Json, OFormat}

case class RequestReferenceNumberDetails(
  propertyDetails: Option[RequestReferenceNumberPropertyDetails] = None,
  contactDetails: Option[RequestReferenceNumberContactDetails] = None
)

object RequestReferenceNumberDetails:

  implicit val format: OFormat[RequestReferenceNumberDetails] = Json.format

  def updateRequestReferenceNumber(
    copy: RequestReferenceNumberDetails => RequestReferenceNumberDetails
  )(implicit sessionRequest: SessionRequest[?]): Session =

    val currentRequestReferenceNumber = sessionRequest.sessionData.requestReferenceNumberDetails

    val updatedRequestReferenceNumber = currentRequestReferenceNumber match {
      case Some(_) => sessionRequest.sessionData.requestReferenceNumberDetails.map(copy)
      case _       => Some(copy(RequestReferenceNumberDetails()))
    }

    sessionRequest.sessionData.copy(requestReferenceNumberDetails = updatedRequestReferenceNumber)
