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

package form.aboutYourLeaseOrTenure

import form.MappingSupport.multipleDoesTheRentPayableMapping
import models.submissions.aboutYourLeaseOrTenure._
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfAnyEqual

object DoesTheRentPayableForm {

  val proprietor = DoesTheRentPayableListProprietor.name
  val otherProperty = DoesTheRentPayableListOtherProperty.name
  val onlyPart = DoesTheRentPayableListOnlyPart.name
  val onlyLand = DoesTheRentPayableListOnlyLand.name
  val shellUnit = DoesTheRentPayableListShellUnit.name
  val none = DoesTheRentPayableListNone.name

  val doesTheRentPayableForm: Form[DoesTheRentPayable] = Form(
    mapping(
      "rentPayable"        -> multipleDoesTheRentPayableMapping,
      "detailsToQuestions" ->
        mandatoryIfAnyEqual(
          Seq(("rentPayable[]", "proprietor"),
            ("rentPayable[]", "otherProperty"),
            ("rentPayable[]", "onlyPart"),
            ("rentPayable[]", "onlyLand"),
            ("rentPayable[]", "shellUnit")),
          default(text, "").verifying(
            nonEmpty(errorMessage = "error.detailsToQuestions.required"),
            maxLength(1000, "error.detailsToQuestions.maxLength")
          )
        )
    )(DoesTheRentPayable.apply)(DoesTheRentPayable.unapply)
  )
}
