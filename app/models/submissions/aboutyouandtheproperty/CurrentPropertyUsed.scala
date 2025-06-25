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

package models.submissions.aboutyouandtheproperty

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum CurrentPropertyUsed(propertyCurrentlyUsed: String):
  override def toString: String = propertyCurrentlyUsed

  case CurrentPropertyPublicHouse extends CurrentPropertyUsed("publicHouse")
  case CurrentPropertyWineBarOrCafe extends CurrentPropertyUsed("wineCafeBar")
  case CurrentPropertyOtherBar extends CurrentPropertyUsed("otherBar")
  case CurrentPropertyPubAndRestaurant extends CurrentPropertyUsed("pubRestaurant")
  case CurrentPropertyLicencedRestaurant extends CurrentPropertyUsed("licencedRestaurant")
  case CurrentPropertyHotel extends CurrentPropertyUsed("hotel")
  case CurrentPropertyDiscoOrNightclub extends CurrentPropertyUsed("discoNightclub")
  case CurrentPropertyOther extends CurrentPropertyUsed("other")
  case CurrentPropertyHealthSpa extends CurrentPropertyUsed("healthSpa")
  case CurrentPropertyLodgeAndRestaurant extends CurrentPropertyUsed("lodgeAndRestaurant")
  case CurrentPropertyConferenceCentre extends CurrentPropertyUsed("conferenceCentre")
end CurrentPropertyUsed

object CurrentPropertyUsed:
  implicit val format: Format[CurrentPropertyUsed] = Scala3EnumJsonFormat.format
