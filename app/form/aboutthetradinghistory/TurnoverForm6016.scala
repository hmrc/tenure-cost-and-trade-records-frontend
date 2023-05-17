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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package form.aboutthetradinghistory
//
//import models.submissions.aboutthetradinghistory.TurnoverSection1516
//import play.api.data.Form
//import play.api.data.Forms.{bigDecimal, localDate, mapping, number}
//import play.api.data.validation.{Constraint, Invalid, Valid}
//
//import java.time.LocalDate
//
//object TurnoverForm6016 {
//  def turnoverForm6016(expectedNumberOfFinancialYears: Int): Form[Seq[TurnoverSection1516]] = {
//    val ukDateMappings                                     = localDate("dd/MM/yyyy")
//    val averageOccupancyConstraint: Constraint[BigDecimal] = Constraint[BigDecimal]("averageOccupancyConstraint") {
//      averageOccupancy =>
//        if (averageOccupancy >= 0 && averageOccupancy <= 100) Valid
//        else Invalid("Average occupancy rate must be between 0 and 100")
//    }
//    val dateTooEarlyConstraint: Constraint[LocalDate]      = Constraint[LocalDate]("dateTooEarlyConstraint") { date =>
//      if (date.isAfter(LocalDate.of(1900, 1, 1))) Valid else Invalid("errorName")
//    }
//
//    Form(
//      expectedNumberOfFinancialYears match {
//        case 1                               =>
//          mapping(
//            "0-financial-year-end"     -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "0-weeks"                  -> number(min = 0, max = 52),
//            "0-accommodation"          -> bigDecimal,
//            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "0-food"                   -> bigDecimal,
//            "0-drinks"       -> bigDecimal,
//            "0-other"         -> bigDecimal,
//            "0-total-sales-revenue"          -> bigDecimal
//
//          ) { case (yearEnd, weeks, accommodation, occupancy, food, drinks, other, totalSales, ) =>
//            Seq(
//              TurnoverSection1516(
//                yearEnd,
//                weeks,
//                accommodation,
//                occupancy,
//                food,
//                drinks,
//                other,
//                totalSales
//              )
//            )
//          } {
//            case Seq(TurnoverSection1516(yearEnd, weeks, accommodation, occupancy, food, drinks, other, totalSales)) =>
//              Some((yearEnd, weeks, accommodation, occupancy, food, drinks, other, totalSales))
//            case _                                                                                            => None
//          }
//        case 2                               =>
//          mapping(
//            "0-financial-year-end" -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "0-weeks" -> number(min = 0, max = 52),
//            "0-accommodation" -> bigDecimal,
//            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "0-food" -> bigDecimal,
//            "0-drinks" -> bigDecimal,
//            "0-other" -> bigDecimal,
//            "0-total-sales-revenue" -> bigDecimal,
//            "1-financial-year-end" -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "1-weeks" -> number(min = 0, max = 52),
//            "1-accommodation" -> bigDecimal,
//            "1-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "1-food" -> bigDecimal,
//            "1-drinks" -> bigDecimal,
//            "1-other" -> bigDecimal,
//            "1-total-sales-revenue" -> bigDecimal
//          ) {
//            case (
//              yearEnd1,
//              weeks1,
//              accommodation1,
//              occupancy1,
//              food1,
//              drinks1,
//              other1,
//              totalSales1,
//              yearEnd2,
//              weeks2,
//              accommodation2,
//              occupancy2,
//              food2,
//              drinks2,
//              other2,
//              totalSales2
//              ) =>
//              Seq(
//                TurnoverSection1516(yearEnd1, weeks1, accommodation1, occupancy1, food1, drinks1, other1, totalSales1),
//                TurnoverSection1516(yearEnd2, weeks2, accommodation2, occupancy2, food2, drinks2, other2, totalSales2)
//              )
//          } {
//            case Seq(
//            TurnoverSection1516(yearEnd1, weeks1, accommodation1, occupancy1, food1, drinks1, other1, totalSales1),
//            TurnoverSection1516(yearEnd2, weeks2, accommodation2, occupancy2, food2, drinks2, other2, totalSales2)
//            ) =>
//              Some(
//                (
//                  yearEnd1,
//                  weeks1,
//                  accommodation1,
//                  occupancy1,
//                  food1,
//                  drinks1,
//                  other1,
//                  totalSales1,
//                  yearEnd2,
//                  weeks2,
//                  accommodation2,
//                  occupancy2,
//                  food2,
//                  drinks2,
//                  other2,
//                  totalSales2
//                )
//              )
//            case _ => None
//          }
//        case 3                               =>
//          mapping(
//            "0-financial-year-end" -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "0-weeks" -> number(min = 0, max = 52),
//            "0-accommodation" -> bigDecimal,
//            "0-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "0-food" -> bigDecimal,
//            "0-drinks" -> bigDecimal,
//            "0-other" -> bigDecimal,
//            "0-total-sales-revenue" -> bigDecimal,
//            "1-financial-year-end" -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "1-weeks" -> number(min = 0, max = 52),
//            "1-accommodation" -> bigDecimal,
//            "1-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "1-food" -> bigDecimal,
//            "1-drinks" -> bigDecimal,
//            "1-other" -> bigDecimal,
//            "1-total-sales-revenue" -> bigDecimal,
//            "2-financial-year-end" -> ukDateMappings.verifying(dateTooEarlyConstraint, dateTooEarlyConstraint),
//            "2-weeks" -> number(min = 0, max = 52),
//            "2-accommodation" -> bigDecimal,
//            "2-average-occupancy-rate" -> bigDecimal.verifying(averageOccupancyConstraint),
//            "2-food" -> bigDecimal,
//            "2-drinks" -> bigDecimal,
//            "2-other" -> bigDecimal,
//            "2-total-sales-revenue" -> bigDecimal
//          ) {
//            case (
//              yearEnd1,
//              weeks1,
//              alcohol1,
//              food1,
//              otherReceipts1,
//              accommodation1,
//              occupancy1,
//              yearEnd2,
//              weeks2,
//              alcohol2,
//              food2,
//              otherReceipts2,
//              accommodation2,
//              occupancy2,
//              yearEnd3,
//              weeks3,
//              alcohol3,
//              food3,
//              otherReceipts3,
//              accommodation3,
//              occupancy3
//              ) =>
//              Seq(
//                TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
//                TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2),
//                TurnoverSection(yearEnd3, weeks3, alcohol3, food3, otherReceipts3, accommodation3, occupancy3)
//              )
//          } {
//            case Seq(
//            TurnoverSection(yearEnd1, weeks1, alcohol1, food1, otherReceipts1, accommodation1, occupancy1),
//            TurnoverSection(yearEnd2, weeks2, alcohol2, food2, otherReceipts2, accommodation2, occupancy2),
//            TurnoverSection(yearEnd3, weeks3, alcohol3, food3, otherReceipts3, accommodation3, occupancy3)
//            ) =>
//              Some(
//                (
//                  yearEnd1,
//                  weeks1,
//                  alcohol1,
//                  food1,
//                  otherReceipts1,
//                  accommodation1,
//                  occupancy1,
//                  yearEnd2,
//                  weeks2,
//                  alcohol2,
//                  food2,
//                  otherReceipts2,
//                  accommodation2,
//                  occupancy2,
//                  yearEnd3,
//                  weeks3,
//                  alcohol3,
//                  food3,
//                  otherReceipts3,
//                  accommodation3,
//                  occupancy3
//                )
//              )
//
//            case _ =>
//              None
//          }
//        case incorrectNumberOfFinancialYears =>
//          throw new IllegalArgumentException(
//            s"expected $expectedNumberOfFinancialYears financial years submissions, got $incorrectNumberOfFinancialYears"
//          )
//      }
//    )
//  }
//}
