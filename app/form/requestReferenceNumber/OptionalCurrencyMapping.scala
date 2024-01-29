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

package form.requestReferenceNumber

import play.api.data.Forms.{optional, text}
import play.api.i18n.Messages
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.Mapping
import util.NumberUtil._

import scala.util.{Failure, Success, Try}

object OptionalCurrencyMapping {

  def partOfAnnualRent(errorTitle: String, annualRent: Option[BigDecimal], otherIncludedPartsSum: BigDecimal)(implicit
    messages: Messages
  ): Mapping[Option[BigDecimal]] =
    optional(
      text
    ).verifying(
      Constraint[Option[String]]("") {
        case Some(value) =>
          Try(BigDecimal(value)) match {
            case Success(amount) =>
              annualRent match {
                case Some(rent) =>
                  val includedPartsSum = amount + otherIncludedPartsSum

                  if (amount < 0) Invalid(ValidationError(messages("error.optCurrency.negative", errorTitle)))
                  else if (amount > rent)
                    Invalid(ValidationError(messages("error.optCurrency.graterThanAnnualRent", errorTitle, rent.asMoney)))
                  else if (includedPartsSum > rent)
                    Invalid(
                      ValidationError(
                        messages("error.includedPartsSum.graterThanAnnualRent", includedPartsSum.asMoney, rent.asMoney)
                      )
                    )
                  else Valid
                case None       => Valid
              }
            case Failure(_)      => Invalid(ValidationError(messages("error.optCurrency.invalid", errorTitle)))
          }
        case None        => Valid
      }
    ).transform[Option[BigDecimal]](
      _.flatMap(value => Try(BigDecimal(value)).toOption),
      _.map(_.toString)
    )

}
