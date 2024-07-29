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

package form.aboutthetradinghistory

import form.MappingSupport.{mappingPerYear, rallyAreasMapping, tradingPeriodWeeks, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.RallyAreasTradingData
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object RallyAreasTradingDataForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[RallyAreasTradingData] =
    mapping(
      "weeks"           -> tradingPeriodWeeks(year),
      "grossReceipts"   -> turnoverSalesMappingWithYear("tentingPitches.grossReceipts", year),
      "numberOfPitches" -> rallyAreasMapping(year)
    )(RallyAreasTradingData.apply)(o => Some(Tuple.fromProductTyped(o)))

  def rallyAreasTradingDataForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[RallyAreasTradingData]] =
    Form {
      mappingPerYear(years, (year, idx) => s"rallyAreas[$idx]" -> columnMapping(year))
    }

}
