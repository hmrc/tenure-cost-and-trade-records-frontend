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

import form.MappingSupport.turnoverSalesMappingWithYear
import models.submissions.aboutthetradinghistory.TurnoverSection
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{ignored, mapping, optional}
import play.api.i18n.Messages
import play.api.data.Forms._

import scala.util.Try
import java.time.LocalDate

object TurnoverForm {

  def turnoverForm(expectedNumberOfFinancialYears: Int, financialYearEndDates: Seq[LocalDate])(implicit
    messages: Messages
  ): Form[Seq[TurnoverSection]] = {

    val salesMax = BigDecimal(1000000000000L)

    def averageOccupancyRateMapping(year: String)(implicit messages: Messages): Mapping[Option[BigDecimal]] = optional(
      text
        .transform[BigDecimal](
          s => Try(BigDecimal(s)).getOrElse(BigDecimal(-1)),
          _.toString
        )
        .verifying(
          messages("error.turnover.averageOccupancyRate.invalid", year),
          amount => amount >= 0 && amount <= 100
        )
    ).verifying(messages("error.turnover.averageOccupancyRate.required", year), _.isDefined)

    def columnMapping(year: String)(implicit messages: Messages): Mapping[TurnoverSection] = mapping(
      "financial-year-end"     -> ignored(LocalDate.EPOCH),
      "weeks"                  -> text
        .verifying(messages("error.weeksMapping.blank", year), _.nonEmpty)
        .transform[Int](
          str => Try(str.toInt).getOrElse(-1),
          int => int.toString
        )
        .verifying(messages("error.weeksMapping.invalid", year), weeks => weeks == 0 || (weeks >= 1 && weeks <= 52)),
      "alcoholic-drinks"       -> turnoverSalesMappingWithYear("turnover.alcohol.sales", year),
      "food"                   -> turnoverSalesMappingWithYear("turnover.food.sales", year),
      "other-receipts"         -> turnoverSalesMappingWithYear("turnover.other.sales", year),
      "accommodation"          -> turnoverSalesMappingWithYear("turnover.accommodation.sales", year),
      "average-occupancy-rate" -> averageOccupancyRateMapping(year)
    )(TurnoverSection.apply)(TurnoverSection.unapply _)

    val yearMappings = financialYearEndDates.map(date => columnMapping(date.getYear.toString))

    Form {
      expectedNumberOfFinancialYears match {
        case 1 =>
          mapping(
            "0" -> yearMappings(0)
          )(Seq(_))(_.headOption)

        case 2 =>
          mapping(
            "0" -> yearMappings(0),
            "1" -> yearMappings(1)
          )(Seq(_, _)) {
            case Seq(first, second) => Some(first, second)
            case _                  => None
          }

        case 3 =>
          mapping(
            "0" -> yearMappings(0),
            "1" -> yearMappings(1),
            "2" -> yearMappings(2)
          )(Seq(_, _, _)) {
            case Seq(first, second, third) => Some(first, second, third)
            case _                         => None
          }

        case _ =>
          throw new IllegalArgumentException(
            s"Unexpected number of financial years: $expectedNumberOfFinancialYears"
          )
      }
    }
  }

}
