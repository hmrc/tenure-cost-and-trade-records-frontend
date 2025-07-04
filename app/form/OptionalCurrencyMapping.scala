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

package form

import play.api.data.Forms.{optional, text}
import play.api.data.Mapping
import play.api.data.validation._
import util.NumberUtil.*

import scala.util.{Failure, Success, Try}

object OptionalCurrencyMapping {

  def partOfAnnualRent(
    errorTitle: String,
    annualRent: Option[BigDecimal],
    otherIncludedPartsSum: BigDecimal
  ): Mapping[Option[BigDecimal]] =
    optional(
      text
    ).verifying(
      Constraint[Option[String]]("partOfAnnualRent") {
        _.fold[ValidationResult](Valid)(value =>
          Try(BigDecimal(value)) match {
            case Success(amount) => validate(amount, otherIncludedPartsSum, annualRent, errorTitle)
            case Failure(_)      => Invalid(ValidationError("error.optCurrency.invalid", errorTitle))
          }
        )
      }
    ).transform[Option[BigDecimal]](
      _.flatMap(value => Try(BigDecimal(value)).toOption),
      _.map(_.toString)
    )

  private def validate(
    partOfRent: BigDecimal,
    otherIncludedPartsSum: BigDecimal,
    annualRent: Option[BigDecimal],
    errorTitle: String
  ): ValidationResult = {
    val includedPartsSum = partOfRent + otherIncludedPartsSum
    val rent             = annualRent.getOrElse(BigDecimal(Long.MaxValue))

    if partOfRent < 0 then Invalid(ValidationError("error.optCurrency.negative", errorTitle))
    else if partOfRent > rent then
      Invalid(ValidationError("error.optCurrency.graterThanAnnualRent", errorTitle, rent.asMoney))
    else if includedPartsSum > rent then
      Invalid(ValidationError("error.includedPartsSum.graterThanAnnualRent", includedPartsSum.asMoney, rent.asMoney))
    else Valid
  }

}
