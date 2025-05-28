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

import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.Income6048
import play.api.data.Forms.{mapping, tuple}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object Income6048Form {

  private def columnMapping(year: String)(using messages: Messages): Mapping[(Int, Income6048)] =
    tuple(
      "weeks"  -> tradingPeriodWeeks(year),
      "income" -> mapping(
        "letting"          -> turnoverSalesMappingWithYear("turnover.6048.income.letting", year),
        "serviceProvision" -> turnoverSalesMappingWithYear("turnover.6048.income.serviceProvision", year),
        "other"            -> turnoverSalesMappingWithYear("turnover.6048.income.other", year)
      )(Income6048.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def income6048Form(
    years: Seq[String]
  )(using messages: Messages): Form[Seq[(Int, Income6048)]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))
    }

}
