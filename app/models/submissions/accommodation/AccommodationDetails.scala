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

package models.submissions.accommodation

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class AccommodationDetails(
  accommodationUnits: List[AccommodationUnit],
  sectionCompleted: Option[AnswersYesNo] = None
)

object AccommodationDetails:
  implicit val format: OFormat[AccommodationDetails] = Json.format

  private val initialAccommodationDetails = AccommodationDetails(List(AccommodationUnit("", "")))

  def updateAccommodationDetails(
    copy: AccommodationDetails => AccommodationDetails
  )(implicit sessionRequest: SessionRequest[?]): Session =
    val accommodationDetails = copy(
      sessionRequest.sessionData.accommodationDetails.getOrElse(initialAccommodationDetails)
    )
    sessionRequest.sessionData.copy(accommodationDetails = Some(accommodationDetails))

  def updateAccommodationUnit(
    idx: Int,
    update: AccommodationUnit => AccommodationUnit
  )(implicit sessionRequest: SessionRequest[?]): Session =
    updateAccommodationDetails { accommodationDetails =>
      val accommodationUnit = update(accommodationDetails.accommodationUnits(idx))

      accommodationDetails.copy(accommodationUnits =
        accommodationDetails.accommodationUnits.updated(idx, accommodationUnit)
      )
    }
