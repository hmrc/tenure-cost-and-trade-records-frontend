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

import form.MappingSupport.{mappingPerYear, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.AdditionalBarsClubs
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object AdditionalBarsClubsForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[AdditionalBarsClubs] =
    mapping(
      "grossBar"            -> turnoverSalesMappingWithYear("additionalBarsClubs.grossBar", year),
      "costBar"             -> turnoverSalesMappingWithYear("additionalBarsClubs.costBar", year),
      "grossClubMembership" -> turnoverSalesMappingWithYear("additionalBarsClubs.grossClubMembership", year),
      "grossFromSeparate"   -> turnoverSalesMappingWithYear("additionalBarsClubs.grossFromSeparate", year),
      "costOfEntertainment" -> turnoverSalesMappingWithYear("additionalBarsClubs.costOfEntertainment", year)
    )(AdditionalBarsClubs.apply)(o => Some(Tuple.fromProductTyped(o)))

  def additionalBarsClubsForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[AdditionalBarsClubs]] =
    Form {
      mappingPerYear(years, (year, idx) => s"additionalBarsClubs[$idx]" -> columnMapping(year))
    }

}
