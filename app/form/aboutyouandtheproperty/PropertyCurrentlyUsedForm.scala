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

package form.aboutyouandtheproperty

import form.MappingSupport.nonEmptyList
import models.submissions.aboutyouandtheproperty.PropertyCurrentlyUsed
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object PropertyCurrentlyUsedForm {

  private val anotherUseDetailsRequired: Constraint[PropertyCurrentlyUsed] =
    Constraint("constraint.anotherUseDetailsRequired") { pcu =>
      if (pcu.propertyCurrentlyUsed.contains("other") && pcu.anotherUseDetails.forall(_.trim.isEmpty)) {
        Invalid(Seq(ValidationError("error.anotherUseDetails.required")))
      } else {
        Valid
      }
    }

  private val propertyCurrentlyUsedMapping: Mapping[PropertyCurrentlyUsed] = mapping(
    "propertyCurrentlyUsed" -> list(text).verifying(
      nonEmptyList("error.propertyCurrentlyUsed.required")
    ),
    "anotherUseDetails"     -> optional(text)
      .verifying("error.anotherUseDetails.maxLength", it => it.forall(_.length <= 200))
  )(PropertyCurrentlyUsed.apply)(o => Some(Tuple.fromProductTyped(o)))

  val propertyCurrentlyUsedForm: Form[PropertyCurrentlyUsed] = Form(
    propertyCurrentlyUsedMapping.verifying(anotherUseDetailsRequired)
  )
}
