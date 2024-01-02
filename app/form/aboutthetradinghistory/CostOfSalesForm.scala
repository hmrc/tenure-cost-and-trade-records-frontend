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

  def costOfSalesForm(years: Seq[String])(implicit messages: Messages): Form[Seq[CostOfSales]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"costOfSales[$idx]" -> columnMapping(year)
    }

    Form(
      years.length match {
        case 1 =>
          mapping(
            mappingPerYear.head
          )(Seq(_))(_.headOption)
        case 2 =>
          mapping(
            mappingPerYear.head,
            mappingPerYear(1)
          )(Seq(_, _))(_.toTuple2)
        case 3 =>
          mapping(
            mappingPerYear.head,
            mappingPerYear(1),
            mappingPerYear(2)
          )(Seq(_, _, _))(_.toTuple3)
      }
    )
  }

}
