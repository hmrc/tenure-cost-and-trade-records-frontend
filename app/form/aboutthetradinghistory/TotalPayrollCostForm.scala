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
import models.submissions.aboutthetradinghistory.TotalPayrollCost
import play.api.data.Forms.{ignored, mapping}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

object TotalPayrollCostForm {
  private def totalPayrollCostMapping(year: String)(using messages: Messages): Mapping[TotalPayrollCost] = mapping(
    "financial-year-end"     -> ignored(LocalDate.EPOCH),
    "managers-and-staff"     -> turnoverSalesMappingWithYear("managers-and-staff", year),
    "directors-remuneration" -> turnoverSalesMappingWithYear("directors-remuneration", year)
  )(TotalPayrollCost.apply)(o => Some(Tuple.fromProductTyped(o)))

  def totalPayrollCostForm(years: Seq[String])(using messages: Messages): Form[Seq[TotalPayrollCost]] =
    Form {
      mappingPerYear(years, (year, idx) => s"totalPayrollCosts[$idx]" -> totalPayrollCostMapping(year))
    }

}
