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

import form.MappingSupport.{mappingPerYear, nonNegativeNumberWithYear, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.TentingPitchesData
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object TentingPitchesDataForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[TentingPitchesData] =
    mapping(
      "grossReceipts"   -> turnoverSalesMappingWithYear("tentingPitches.grossReceipts", year),
      "numberOfPitches" -> nonNegativeNumberWithYear("tentingPitches.numberOfPitches", year)
    )(TentingPitchesData.apply)(o => Some(Tuple.fromProductTyped(o)))

  def tentingPitchesDataForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[TentingPitchesData]] =
    Form {
      mappingPerYear(years, (year, idx) => s"tentingPitches[$idx]" -> columnMapping(year))
    }

}
