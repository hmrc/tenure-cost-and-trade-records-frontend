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

import form.MappingSupport._
import models.submissions.aboutthetradinghistory.CostOfSales
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

object CostOfSalesForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[CostOfSales] = mapping(
    "financial-year-end" -> ignored(LocalDate.EPOCH),
    "accommodation"      -> turnoverSalesMappingWithYear("turnover.accommodation.sales", year),
    "food"               -> turnoverSalesMappingWithYear("turnover.food.sales", year),
    "drinks"             -> turnoverSalesMappingWithYear("turnover.alcohol.sales", year),
    "other"              -> turnoverSalesMappingWithYear("turnover.other.sales", year)
  )(CostOfSales.apply)(CostOfSales.unapply)

  def costOfSalesForm(years: Seq[String])(implicit messages: Messages): Form[Seq[CostOfSales]] =
    Form {
      mappingPerYear(years, (year, idx) => s"costOfSales[$idx]" -> columnMapping(year))
    }

}
