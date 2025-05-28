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

package form.aboutthetradinghistory

import form.MappingSupport.{tradingPeriodWeeks, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.TurnoverSection6030
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

object TurnoverForm6030 {

  def turnoverForm6030(expectedNumberOfFinancialYears: Int, financialYearEndDates: Seq[LocalDate])(using
    messages: Messages
  ): Form[Seq[TurnoverSection6030]] = {

    def columnMapping(year: String)(using messages: Messages): Mapping[TurnoverSection6030] = mapping(
      "financial-year-end" -> ignored(LocalDate.EPOCH),
      "weeks"              -> tradingPeriodWeeks(year),
      "grossIncome"        -> turnoverSalesMappingWithYear("turnover.grossIncome", year),
      "totalVisitorNumber" -> optional(text)
        .verifying(nonNegativeNumberConstraint(year))
        .transform[Option[Int]](
          _.flatMap(str => Try(str.toInt).toOption),
          _.map(_.toString)
        )
    )(TurnoverSection6030.apply)(o => Some(Tuple.fromProductTyped(o)))

    val yearMappings = financialYearEndDates.map(date => columnMapping(date.getYear.toString))

    Form {
      expectedNumberOfFinancialYears match {
        case 1 =>
          mapping(
            "0" -> yearMappings.head
          )(Seq(_))(_.headOption)

        case 2 =>
          mapping(
            "0" -> yearMappings.head,
            "1" -> yearMappings(1)
          )(Seq(_, _)) {
            case Seq(first, second) => Some((first, second))
            case _                  => None
          }

        case 3 =>
          mapping(
            "0" -> yearMappings.head,
            "1" -> yearMappings(1),
            "2" -> yearMappings(2)
          )(Seq(_, _, _)) {
            case Seq(first, second, third) => Some((first, second, third))
            case _                         => None
          }

        case _ =>
          throw new IllegalArgumentException(
            s"Unexpected number of financial years: $expectedNumberOfFinancialYears"
          )
      }
    }
  }

  private def nonNegativeNumberConstraint(year: String): Constraint[Option[String]] =
    Constraint("constraints.nonNegative") {
      case Some(text) =>
        Try(text.toDouble).toOption match {
          case Some(num) if num >= 0 => Valid
          case Some(_)               => Invalid(ValidationError("error.totalVisitorNumber.negative", year))
          case None                  => Invalid(ValidationError("error.totalVisitorNumber.nonNumeric", year))
        }
      case None       => Invalid(ValidationError("error.totalVisitorNumber.required", year))
    }
}
