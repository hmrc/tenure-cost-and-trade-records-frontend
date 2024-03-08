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
import play.api.data.validation.Constraints.maxLength
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.Try

object ElectricVehicleChargingPointsMapping {

  def validateSpacesOrBays =
    default(text, "")
      .verifying(nonNegativeNumberConstraint())
      .transform[Int](
        str => Try(str.toInt).getOrElse(-1),
        int => int.toString
      )

  private def nonNegativeNumberConstraint(): Constraint[String] =
    Constraint("constraints.nonNegative")({
      case text =>
        Try(text.toDouble).toOption match {
          case Some(num) if num >= 0 && num <= 999 => Valid
          case Some(num) if num > 999              => Invalid(ValidationError("error.spacesOrBaysNumber.required"))
          case Some(_)                             => Invalid(ValidationError("error.spacesOrBaysNumber.negative"))
          case None                                => Invalid(ValidationError("error.spacesOrBaysNumber.nonNumeric"))
        }
      case _    => Invalid(ValidationError("error.spacesOrBaysNumber.required"))
    })
}
