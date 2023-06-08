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

package form.aboutyouandtheproperty

import form.MappingSupport.{aboutYourPropertyType, multipleCurrentPropertyUsedMapping}
import models.submissions.aboutyouandtheproperty._
import play.api.data.Form
import play.api.data.Forms.{default, mapping, nonEmptyText, optional, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object AboutThePropertyForm {

  val publicHouse        = CurrentPropertyPublicHouse.name
  val wineBarOrCafe      = CurrentPropertyWineBarOrCafe.name
  val otherBar           = CurrentPropertyOtherBar.name
  val pubAndRestaurant   = CurrentPropertyPubAndRestaurant.name
  val licencedRestaurant = CurrentPropertyLicencedRestaurant.name
  val hotel              = CurrentPropertyHotel.name
  val discoOrNightclub   = CurrentPropertyDiscoOrNightclub.name
  val other              = CurrentPropertyOther.name
  val healthSpa          = CurrentPropertyHealthSpa.name
  val lodgeAndRestaurant = CurrentPropertyLodgeAndRestaurant.name
  val conferenceCentre   = CurrentPropertyConferenceCentre.name

  val aboutThePropertyForm: Form[PropertyDetails] = Form(
    mapping(
      "currentOccupierName"        -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.currentOccupierName.required"),
        maxLength(50, "error.currentOccupierName.maxLength")
      ),
      "propertyCurrentlyUsed"      -> multipleCurrentPropertyUsedMapping,
      "propertyCurrentlyUsedOther" -> optional(text)
    )(PropertyDetails.apply)(PropertyDetails.unapply)
  )

}
