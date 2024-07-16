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

package form.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.BunkerFuelCardDetails
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.util.Try
object BunkerFuelCardDetailsForm {

  private val positiveBigDecimal: Constraint[BigDecimal] = Constraint("constraint.positive") { value =>
    if (value >= 0) Valid else Invalid("error.handlingFee.mustBeNonNegative")
  }

  private def bigDecimalWithCustomError(errorMessage: String): Mapping[BigDecimal] =
    text
      .verifying(errorMessage, str => Try(BigDecimal(str)).isSuccess)
      .transform[BigDecimal](str => BigDecimal(str), _.toString)

  val bunkerFuelCardDetailsForm: Form[BunkerFuelCardDetails] = Form(
    mapping(
      "name"        -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.bunkerFuelCard.name.required"),
        maxLength(100, "error.bunkerFuelCardName.maxLength")
      ),
      "handlingFee" -> bigDecimalWithCustomError("error.bunkerFuelCard.handlingFee.invalidFormat")
        .verifying(positiveBigDecimal)
    )(BunkerFuelCardDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
}
