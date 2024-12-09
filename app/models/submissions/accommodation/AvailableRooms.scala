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

import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class AvailableRooms(
  singleBedrooms: Int = 0,
  doubleBedrooms: Int = 0,
  bathrooms: Int = 0,
  otherAccommodationDescription: Option[String] = None,
  maxGuestsNumber: Int = 0
)

object AvailableRooms:
  implicit val format: OFormat[AvailableRooms] = Json.format