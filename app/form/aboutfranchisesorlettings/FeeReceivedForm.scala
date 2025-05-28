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

package form.aboutfranchisesorlettings

import form.MappingSupport._
import models.submissions.aboutfranchisesorlettings.{FeeReceived, FeeReceivedPerYear}
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object FeeReceivedForm {

  private def columnMapping(year: String)(using messages: Messages): Mapping[FeeReceivedPerYear] = mapping(
    "financialYearEnd"         -> ignored(LocalDate.EPOCH),
    "tradingPeriod"            -> tradingPeriodWeeks(year),
    "concessionOrFranchiseFee" -> turnoverSalesMappingWithYear("feeReceived.concessionOrFranchiseFee", year)
  )(FeeReceivedPerYear.apply)(o => Some(Tuple.fromProductTyped(o)))

  private def feeReceivedPerYearSeq(
    years: Seq[String]
  )(using messages: Messages): Mapping[Seq[FeeReceivedPerYear]] =
    mappingPerYear(years, (year, idx) => s"year[$idx]" -> columnMapping(year))

  def feeReceivedForm(
    years: Seq[String]
  )(using messages: Messages): Form[FeeReceived] =
    Form {
      mapping(
        "feeReceivedPerYear"    -> feeReceivedPerYearSeq(years),
        "feeCalculationDetails" -> optional(text(maxLength = 2000))
        // .verifying(messages("error.feeReceived.feeCalculationDetails.required"), _.nonEmpty)
      )(FeeReceived.apply)(o => Some(Tuple.fromProductTyped(o)))
    }

}
