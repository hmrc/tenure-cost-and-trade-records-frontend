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
import form.MappingSupport.{costOfSalesMapping, mappingPerYear}
import models.submissions.aboutthetradinghistory.StaffCosts
import play.api.data.Forms.{mapping, single}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object StaffCostsForm {

  private def sumMapping(year: String)(implicit messages: Messages): Mapping[StaffCosts] =
    mapping(
      "wages"         -> costOfSalesMapping("staffCosts.wages", year),
      "NI"            -> costOfSalesMapping("staffCosts.NI", year),
      "pension"       -> costOfSalesMapping("staffCosts.pension", year),
      "remunerations" -> costOfSalesMapping("staffCosts.remunerations", year)
    )(StaffCosts.apply)(StaffCosts.unapply)

  def staffCostsForm(years: Seq[String])(implicit messages: Messages): Form[Seq[StaffCosts]] =
    Form {
      single(
        "staffCosts" -> mappingPerYear(years, (year, idx) => s"[$idx]" -> sumMapping(year))
      )
    }
}
