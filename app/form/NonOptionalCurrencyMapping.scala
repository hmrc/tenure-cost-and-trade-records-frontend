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

package form

import play.api.data.Forms.{default, optional, text}
import play.api.data.Mapping
import play.api.data.validation._
import util.NumberUtil._

import scala.util.{Failure, Success, Try}

object NonOptionalCurrencyMapping {

  def partOfAnnualCharge(
                          errorTitle: String,
                          annualCharge: Option[BigDecimal],
                          otherIncludedPartsSum: BigDecimal
                        ): Mapping[BigDecimal] =
    text
      .verifying("error.annualCharge.invalid", str => Try(BigDecimal(str)).isSuccess) // reject non numerics
      .transform[BigDecimal]( // transform to BigDecimal
        str => Try(BigDecimal(str)).getOrElse(BigDecimal(0)), // getOrElse can be removed as we know it is numeric from above. I'll leave that in for now.
        bd => bd.toString // For display
      )
      .verifying(
        Constraint[BigDecimal]("constraint.check") { bd =>
          validate(bd, otherIncludedPartsSum, annualCharge, errorTitle) // validate as normal. No changes to validate
        }
      )

  private def validate(
    partOfRent: BigDecimal,
    otherIncludedPartsSum: BigDecimal,
    annualRent: Option[BigDecimal],
    errorTitle: String
  ): ValidationResult = {
    val includedPartsSum = partOfRent + otherIncludedPartsSum
    val rent             = annualRent.getOrElse(BigDecimal(Long.MaxValue))

    if (partOfRent < 0) {
      Invalid(ValidationError("error.optCurrency.negative", errorTitle))
    } else if (partOfRent > rent) {
      Invalid(ValidationError("error.optCurrency.graterThanAnnualRent", errorTitle, rent.asMoney))
    } else if (includedPartsSum > rent) {
      Invalid(
        ValidationError("error.includedPartsSum.graterThanAnnualRent", includedPartsSum.asMoney, rent.asMoney)
      )
    } else {
      Valid
    }
  }

}
