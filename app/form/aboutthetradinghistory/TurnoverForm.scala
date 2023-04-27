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

import models.submissions.aboutthetradinghistory.TurnoverSection
import play.api.data.Form
import play.api.data.Forms.{bigDecimal, localDate, mapping, number}
import play.api.data.validation.{Constraint, Invalid, Valid}

import java.time.LocalDate

object TurnoverForm {
  def turnoverForm(expectedNumberOfFinancialYears: Int): Form[Seq[TurnoverSection]] = {
    val ukDateMappings                                     = localDate("dd/MM/yyyy")
    val averageOccupancyConstraint: Constraint[BigDecimal] = Constraint[BigDecimal]("averageOccupancyConstraint") {
      averageOccupancy =>
        if (averageOccupancy >= 0 && averageOccupancy <= 100) Valid
        else Invalid("Average occupancy rate must be between 0 and 100")
    }
    val dateTooEarlyConstraint: Constraint[LocalDate]      = Constraint[LocalDate]("dateTooEarlyConstraint") { date =>
      if (date.isAfter(LocalDate.of(1900, 1, 1))) Valid else Invalid("errorName")
    }

    Form(
      expectedNumberOfFinancialYears match {
        case 1                               =>
          mapping(
            "0-financial-year-end"     -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
            "0-weeks"                  -> number(min = 0, max = 52),
            "0-alcoholic-drinks"       -> bigDecimal,
            "0-food"                   -> bigDecimal,
            "0-other-receipts"         -> bigDecimal,
            "0-accommodation"          -> bigDecimal,
            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint)
          ) { case (yearEnd, weeks, alcohol, food, otherReceipts, accommodation, occupancy) =>
            Seq(
              TurnoverSection(
                yearEnd,
                weeks,
                alcohol,
                food,
                otherReceipts,
                accommodation,
                occupancy
              )
            )
          } {
            case Seq(TurnoverSection(yearEnd, weeks, alcohol, food, otherReceipts, accommodation, occupancy)) =>
              Some((yearEnd, weeks, alcohol, food, otherReceipts, accommodation, occupancy))
            case _                                                                                            => None
          }
        case 2                               =>
          mapping(
            "0-financial-year-end"     -> ukDateMappings,
            "0-weeks"                  -> number(min = 0, max = 52),
            "0-alcoholic-drinks"       -> bigDecimal,
            "0-food"                   -> bigDecimal,
            "0-other-receipts"         -> bigDecimal,
            "0-accommodation"          -> bigDecimal,
            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
            "1-financial-year-end"     -> ukDateMappings,
            "1-weeks"                  -> number(min = 0, max = 52),
            "1-alcoholic-drinks"       -> bigDecimal,
            "1-food"                   -> bigDecimal,
            "1-other-receipts"         -> bigDecimal,
            "1-accommodation"          -> bigDecimal,
            "1-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint)
          ) {
            case (
                  yearEnd1,
                  weeks1,
                  alcohol1,
                  food1,
                  otherReceipts1,
                  accommodation1,
                  occupancy1,
                  yearEnd2,
                  weeks2,
                  alcohol2,
                  food2,
                  otherReceipts2,
                  accommodation2,
                  occupancy2
                ) =>
              Seq(
                TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
                TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2)
              )
          } {
            case Seq(
                  TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
                  TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2)
                ) =>
              Some(
                (
                  yearEnd1,
                  weeks1,
                  alcohol1,
                  food1,
                  otherReceipts1,
                  accommodation1,
                  occupancy1,
                  yearEnd2,
                  weeks2,
                  alcohol2,
                  food2,
                  otherReceipts2,
                  accommodation2,
                  occupancy2
                )
              )
            case _ => None
          }
        case 3                               =>
          mapping(
            "0-financial-year-end"     -> ukDateMappings,
            "0-weeks"                  -> number(min = 0, max = 52),
            "0-alcoholic-drinks"       -> bigDecimal,
            "0-food"                   -> bigDecimal,
            "0-other-receipts"         -> bigDecimal,
            "0-accommodation"          -> bigDecimal,
            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
            "1-financial-year-end"     -> ukDateMappings,
            "1-weeks"                  -> number(min = 0, max = 52),
            "1-alcoholic-drinks"       -> bigDecimal,
            "1-food"                   -> bigDecimal,
            "1-other-receipts"         -> bigDecimal,
            "1-accommodation"          -> bigDecimal,
            "1-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
            "2-financial-year-end"     -> ukDateMappings,
            "2-weeks"                  -> number(min = 0, max = 52),
            "2-alcoholic-drinks"       -> bigDecimal,
            "2-food"                   -> bigDecimal,
            "2-other-receipts"         -> bigDecimal,
            "2-accommodation"          -> bigDecimal,
            "2-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint)
          ) {
            case (
                  yearEnd1,
                  weeks1,
                  alcohol1,
                  food1,
                  otherReceipts1,
                  accommodation1,
                  occupancy1,
                  yearEnd2,
                  weeks2,
                  alcohol2,
                  food2,
                  otherReceipts2,
                  accommodation2,
                  occupancy2,
                  yearEnd3,
                  weeks3,
                  alcohol3,
                  food3,
                  otherReceipts3,
                  accommodation3,
                  occupancy3
                ) =>
              Seq(
                TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
                TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2),
                TurnoverSection(yearEnd3, weeks3, alcohol3, food3, otherReceipts3, accommodation3, occupancy3)
              )
          } {
            case Seq(
                  TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
                  TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2),
                  TurnoverSection(yearEnd3, weeks3, alcohol3, food3, otherReceipts3, accommodation3, occupancy3)
                ) =>
              Some(
                (
                  yearEnd1,
                  weeks1,
                  alcohol1,
                  food1,
                  otherReceipts1,
                  accommodation1,
                  occupancy1,
                  yearEnd2,
                  weeks2,
                  alcohol2,
                  food2,
                  otherReceipts2,
                  accommodation2,
                  occupancy2,
                  yearEnd3,
                  weeks3,
                  alcohol3,
                  food3,
                  otherReceipts3,
                  accommodation3,
                  occupancy3
                )
              )

            case _ =>
              None
          }
        case incorrectNumberOfFinancialYears =>
          throw new IllegalArgumentException(
            s"expected $expectedNumberOfFinancialYears financial years submissions, got $incorrectNumberOfFinancialYears"
          )
      }
    )
  }
}
