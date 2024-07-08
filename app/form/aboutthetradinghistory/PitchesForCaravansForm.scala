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

import controllers.toOpt
import form.MappingSupport.{mappingPerYear, numberOfPitches, tradingPeriodWeeks, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.PitchesForCaravans
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object PitchesForCaravansForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[PitchesForCaravans] =
    mapping(
      "weeks"           -> tradingPeriodWeeks(year),
      "grossReceipts"   -> turnoverSalesMappingWithYear("tentingPitches.grossReceipts", year),
      "numberOfPitches" -> numberOfPitches(year)
    )(PitchesForCaravans.apply)(PitchesForCaravans.unapply)

  def pitchesForCaravansForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[PitchesForCaravans]] =
    Form {
      mappingPerYear(years, (year, idx) => s"tentingPitches[$idx]" -> columnMapping(year))
    }

}
