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

import models.submissions.aboutthetradinghistory.TotalPayrollCost
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{bigDecimal, default, localDate, mapping, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid}

import java.time.LocalDate

object TotalPayrollCostForm {

  def totalPayrollCostForm(expectedNumberOfFinancialYears: Int): Form[Seq[TotalPayrollCost]] = {
    val ukDateMappings                                = localDate("dd/MM/yyyy")
    val dateTooEarlyConstraint: Constraint[LocalDate] = Constraint[LocalDate]("dateTooEarlyConstraint") { date =>
      if (date.isAfter(LocalDate.of(1900, 1, 1))) Valid else Invalid("errorName")
    }
    val columnMapping: Mapping[TotalPayrollCost]      = mapping(
      "financial-year-end"     -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
      "managers-and-staff"     -> bigDecimal,
      "directors-remuneration" -> bigDecimal
    )(TotalPayrollCost.apply)(TotalPayrollCost.unapply)

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
