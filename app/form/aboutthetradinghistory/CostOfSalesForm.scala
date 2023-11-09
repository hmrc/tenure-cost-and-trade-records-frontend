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

import form.MappingSupport.turnoverSalesMapping
import models.submissions.aboutthetradinghistory.CostOfSales
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

import java.time.LocalDate

object CostOfSalesForm {

  val columnMapping: Mapping[CostOfSales] = mapping(
    "financial-year-end" -> ignored(LocalDate.EPOCH),
    "accommodation"      -> turnoverSalesMapping("turnover.accommodation.sales"),
    "food"               -> turnoverSalesMapping("turnover.food.sales"),
    "drinks"             -> turnoverSalesMapping("turnover.alcohol.sales"),
    "other"              -> turnoverSalesMapping("turnover.other.sales")
  )(CostOfSales.apply)(CostOfSales.unapply)

  val costOfSalesForm: Form[Seq[CostOfSales]] = Form(
    single(
      "costOfSales" -> seq(columnMapping).verifying("error.costOfSales.maxColumns", _.length <= 3)
    )
  )

}
