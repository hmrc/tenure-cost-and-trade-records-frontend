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

package form.aboutYourLeaseOrTenure

import form.MappingSupport.{nonEmptyList, noneCantBeSelectedWithOther}
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.IncludedInYourRentDetails
import play.api.data.Form
import play.api.data.Forms.{list, mapping, optional, text}
import play.api.data.validation._

object IncludedInYourRentForm {

  private val regexForVatValue = """^\d+(\.\d+)?$"""

  val vatValueNumberConstraint: Constraint[Option[String]] = Constraint("constraints.vatValueNumber") {
    case Some(value) if value.matches(regexForVatValue) => Valid
    case Some(_)                                        => Invalid(Seq(ValidationError("error.includedInYourRent.vatValue.range")))
    case None                                           => Valid
  }

  def includedInYourRentForm(forTypes: String): Form[IncludedInYourRentDetails] = Form(
    mapping(
      "includedInYourRent" -> list(text).verifying(
        nonEmptyList("error.includedInYourRent.required"),
        noneCantBeSelectedWithOther(
          "noneOfThese",
          "error.includedInYourRent.noneSelectedWithOther"
        )
      ),
      "vatValue"           -> optional(text)
        .verifying(vatValueNumberConstraint)
        .transform[Option[BigDecimal]](
          {
            case Some(value) if value.matches(regexForVatValue) => Some(BigDecimal(value))
            case _                                              => None
          },
          _.map(_.toString)
        )
    )(IncludedInYourRentDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
      .verifying(
        "error.includedInYourRent.vatValue.required",
        fields =>
          fields match {
            case IncludedInYourRentDetails(includedInYourRent, vatValue) =>
              !(forTypes == ForTypes.for6045 && includedInYourRent.contains("vat") && vatValue.isEmpty)
          }
      )
  )
}
