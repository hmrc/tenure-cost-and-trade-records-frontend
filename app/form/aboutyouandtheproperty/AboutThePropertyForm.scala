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

package form.aboutyouandtheproperty

import form.MappingSupport.currentPropertyUsedMapping
import models.submissions.aboutyouandtheproperty._
import models.submissions.aboutyouandtheproperty.CurrentPropertyUsed.*
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.validation.Constraints.maxLength

object AboutThePropertyForm:

  val publicHouse: String        = CurrentPropertyPublicHouse.toString
  val wineBarOrCafe: String      = CurrentPropertyWineBarOrCafe.toString
  val otherBar: String           = CurrentPropertyOtherBar.toString
  val pubAndRestaurant: String   = CurrentPropertyPubAndRestaurant.toString
  val licencedRestaurant: String = CurrentPropertyLicencedRestaurant.toString
  val hotel: String              = CurrentPropertyHotel.toString
  val discoOrNightclub: String   = CurrentPropertyDiscoOrNightclub.toString
  val other: String              = CurrentPropertyOther.toString
  val healthSpa: String          = CurrentPropertyHealthSpa.toString
  val lodgeAndRestaurant: String = CurrentPropertyLodgeAndRestaurant.toString
  val conferenceCentre: String   = CurrentPropertyConferenceCentre.toString

  val aboutThePropertyForm: Form[PropertyDetails] = Form(
    mapping(
      "propertyCurrentlyUsed"      -> currentPropertyUsedMapping,
      "propertyCurrentlyUsedOther" -> optional(
        default(text, "").verifying(
          maxLength(200, "error.propertyCurrentlyUsed.maxLength")
        )
      )
    )(PropertyDetails.apply)(o => Some(Tuple.fromProductTyped(o))).verifying(
      "error.propertyCurrentlyUsed.required",
      pd => !(pd.propertyCurrentlyUsed == CurrentPropertyOther && pd.currentlyUsedOtherField.isEmpty)
    )
  )
