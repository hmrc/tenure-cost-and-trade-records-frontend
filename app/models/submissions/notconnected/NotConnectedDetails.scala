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

package models.submissions.notconnected

import actions.SessionRequest
import models.Session
import models.submissions.common.{Address, ContactDetails}
import models.submissions.PastConnectionType
import play.api.libs.json.Json

case class NotConnectedDetails(
                                  notConnectedContact: Option[ContactDetails]
                                )
object NotConnectedDetails {
  implicit val format = Json.format[NotConnectedDetails]

  def updateNotConnectedDetails(
                                   copy: NotConnectedDetails => NotConnectedDetails
                                 )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentNotConnectedDetails = sessionRequest.sessionData.notConnectedDetails

    val updatedNotConnectedDetails = currentNotConnectedDetails match {
      case Some(_) => sessionRequest.sessionData.stillConnectedDetails.map(copy)
      case _       => Some(copy(NotConnectedDetails()))
    }

    sessionRequest.sessionData.copy(notConnectedDetails = updatedNotConnectedDetails)

  }
}
