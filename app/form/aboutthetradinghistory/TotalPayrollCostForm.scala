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

package form.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.TotalPayrollCost
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{ignored, mapping}
import form.MappingSupport.{EnrichedSeq, turnoverSalesMappingWithYear}
import play.api.i18n.Messages

import java.time.LocalDate

object TotalPayrollCostForm {
  private def totalPayrollCostMapping(year: String)(implicit messages: Messages): Mapping[TotalPayrollCost] = mapping(
    "financial-year-end"     -> ignored(LocalDate.EPOCH),
    "managers-and-staff"     -> turnoverSalesMappingWithYear("managers-and-staff", year),
    "directors-remuneration" -> turnoverSalesMappingWithYear("directors-remuneration", year)
  )(TotalPayrollCost.apply)(TotalPayrollCost.unapply)

  def totalPayrollCostForm(years: Seq[String])(implicit messages: Messages): Form[Seq[TotalPayrollCost]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"totalPayrollCosts[$idx]" -> totalPayrollCostMapping(year)
    }
    Form(
      mappingPerYear match {
        case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
        case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
        case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
      }
    )
  }
}
