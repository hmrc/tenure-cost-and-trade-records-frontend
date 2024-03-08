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

import play.api.data.Forms.{optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.Try

object ElectricVehicleChargingPointsMapping {

  val spacesOrBaysRegex = """^([0-9]\d{0,2})$"""

  def validateSpacesOrBays =
    optional(text)
      .verifying(nonNegativeNumberConstraint())
      .transform[Option[Int]](
        _.flatMap(str => Try(str.toInt).toOption),
        _.map(_.toString)
      )
      .verifying(Errors.spacesOrBays, sB => sB.nonEmpty)
      .verifying(
        Errors.spacesOrBaysNumber, sB => sB.min == 0 && sB.max == 999
      )

  private def nonNegativeNumberConstraint(): Constraint[Option[String]] =
    Constraint("constraints.nonNegative")({
      case Some(text) =>
        Try(text.toDouble).toOption match {
          case Some(num) if num >= 0 => Valid
          case Some(_) => Invalid(ValidationError("error.totalVisitorNumber.negative"))
          case None => Invalid(ValidationError("error.totalVisitorNumber.nonNumeric"))
        }
      case None => Invalid(ValidationError("error.totalVisitorNumber.required"))
    })
}
