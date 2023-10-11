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

package form.aboutthetradinghistory

import form.DateMappings.dateTooEarlyConstraint
import models.submissions.aboutthetradinghistory.TurnoverSection
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{bigDecimal, localDate, mapping, number, optional}
import play.api.data.validation.{Constraint, Invalid, Valid}

object TurnoverForm {
  def turnoverForm(expectedNumberOfFinancialYears: Int): Form[Seq[TurnoverSection]] = {
    val ukDateMappings                                     = localDate("dd/MM/yyyy")
    val averageOccupancyConstraint: Constraint[BigDecimal] = Constraint[BigDecimal]("averageOccupancyConstraint") {
      averageOccupancy =>
        if (averageOccupancy >= 0 && averageOccupancy <= 100) Valid
        else Invalid("Average occupancy rate must be between 0 and 100")
    }
    val columnMapping: Mapping[TurnoverSection]            = mapping(
      "financial-year-end"     -> ukDateMappings.verifying(dateTooEarlyConstraint),
      "weeks"                  -> number(min = 0, max = 52),
      "alcoholic-drinks"       -> optional(bigDecimal),
      "food"                   -> optional(bigDecimal),
      "other-receipts"         -> optional(bigDecimal),
      "accommodation"          -> optional(bigDecimal),
      "average-occupancy-rate" -> optional(bigDecimal.verifying(averageOccupancyConstraint))
    )(TurnoverSection.apply)(TurnoverSection.unapply)

    Form {
      expectedNumberOfFinancialYears match {
        case 1                               =>
          mapping("0" -> columnMapping)(section => Seq(section)) {
            case Seq(section) => Some(section)
            case _            => None
          }
        case 2                               =>
          mapping(
            "0" -> columnMapping,
            "1" -> columnMapping
          ) { case (first, second) => Seq(first, second) } {
            case Seq(first, second) => Some((first, second))
            case _                  => None
          }
        case 3                               =>
          mapping(
            "0" -> columnMapping,
            "1" -> columnMapping,
            "2" -> columnMapping
          ) { case (first, second, third) => Seq(first, second, third) } {
            case Seq(first, second, third) => Some((first, second, third))
            case _                         => None
          }
        case incorrectNumberOfFinancialYears =>
          throw new IllegalArgumentException(
            s"$expectedNumberOfFinancialYears must be between 1 and 3, was: $incorrectNumberOfFinancialYears"
          )
      }
    }
  }
}
