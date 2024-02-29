/*
 * Copyright 2024 HM Revenue & Customs
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

package form.aboutfranchisesorlettings

import form.MappingSupport._
import models.submissions.aboutfranchisesorlettings.{FeeReceived, FeeReceivedPerYear}
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
object FeeReceivedForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[FeeReceivedPerYear] = mapping(
    "financialYearEnd"         -> ignored(LocalDate.EPOCH),
    "tradingPeriod"            -> text
      .verifying(messages("error.weeksMapping.blank", year), _.nonEmpty)
      .transform[Int](str => Try(str.toInt).getOrElse(-1), _.toString)
      .verifying(messages("error.weeksMapping.invalid", year), (0 to 52) contains _),
    "concessionOrFranchiseFee" -> turnoverSalesMappingWithYear("feeReceived.concessionOrFranchiseFee", year)
  )(FeeReceivedPerYear.apply)(FeeReceivedPerYear.unapply)

  private def feeReceivedPerYearSeq(
    years: Seq[String]
  )(implicit messages: Messages): Mapping[Seq[FeeReceivedPerYear]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"year[$idx]" -> columnMapping(year)
    }

    mappingPerYear match {
      case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
      case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
      case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
    }
  }

  def feeReceivedForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[FeeReceived] =
    Form {
      mapping(
        "feeReceivedPerYear"    -> feeReceivedPerYearSeq(years),
        "feeCalculationDetails" -> optional(text(maxLength = 2000))
          .verifying(messages("error.feeReceived.feeCalculationDetails.required"), _.nonEmpty)
      )(FeeReceived.apply)(FeeReceived.unapply)
    }

}
