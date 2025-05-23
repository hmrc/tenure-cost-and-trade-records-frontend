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

package form.accommodation

import form.MappingSupport.nonNegativeNumber
import models.submissions.accommodation.AvailableRooms
import play.api.data.Form
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints.maxLength
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

/**
  * @author Yuriy Tumakha
  */
object AvailableRooms6048Form:

  private val singleDoubleZero: Constraint[AvailableRooms] =
    Constraint("constraint.anotherUseDetailsRequired") { x =>
      if (x.singleBedrooms == 0 && x.doubleBedrooms == 0) {
        Invalid(Seq(ValidationError("error.accommodation.singleBedrooms.zero")))
      } else {
        Valid
      }
    }

  private val availableRooms6048Mapping: Mapping[AvailableRooms] =
    mapping(
      "singleBedrooms"                -> nonNegativeNumber("accommodation.singleBedrooms"),
      "doubleBedrooms"                -> nonNegativeNumber("accommodation.doubleBedrooms"),
      "bathrooms"                     -> nonNegativeNumber("accommodation.bathrooms"),
      "otherAccommodationDescription" -> optional(
        text.verifying(maxLength(200, "error.accommodation.otherAccommodationDescription.maxLength"))
      ),
      "maxGuestsNumber"               -> nonNegativeNumber("accommodation.maxGuestsNumber")
    )(AvailableRooms.apply)(o => Some(Tuple.fromProductTyped(o)))

  val availableRooms6048Form: Form[AvailableRooms] = Form(
    availableRooms6048Mapping.verifying(singleDoubleZero)
  )
