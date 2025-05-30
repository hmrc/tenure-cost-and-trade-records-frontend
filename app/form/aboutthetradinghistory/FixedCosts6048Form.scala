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
import models.submissions.aboutthetradinghistory.FixedCosts6048
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object FixedCosts6048Form {

  private def columnMapping(year: String)(using messages: Messages): Mapping[FixedCosts6048] =
    mapping(
      "insurance"                 -> turnoverSalesMappingWithYear("turnover.6048.fixedCosts.insurance", year),
      "businessRatesOrCouncilTax" -> turnoverSalesMappingWithYear(
        "turnover.6048.fixedCosts.businessRatesOrCouncilTax",
        year
      ),
      "rent"                      -> turnoverSalesMappingWithYear("turnover.6048.fixedCosts.rent", year)
    )(FixedCosts6048.apply)(o => Some(Tuple.fromProductTyped(o)))

  def fixedCosts6048Form(
    years: Seq[String]
  )(using messages: Messages): Form[Seq[FixedCosts6048]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))
    }

}
