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

import form.ConditionalConstraintMappings.mandatoryStringIfNonZeroSum
import form.MappingSupport.{mappingPerYear, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.CostOfSales6076Sum
import play.api.data.Forms.{mapping, tuple}
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object CostOfSales6076Form {

  private def sumMapping(year: String)(using messages: Messages): Mapping[CostOfSales6076Sum] =
    mapping(
      "fuelOrFeedstock" -> turnoverSalesMappingWithYear("costOfSales6076.fuelOrFeedstock", year),
      "importedPower"   -> turnoverSalesMappingWithYear("costOfSales6076.importedPower", year),
      "TNuoS"           -> turnoverSalesMappingWithYear("costOfSales6076.TNuoS", year),
      "BSuoS"           -> turnoverSalesMappingWithYear("costOfSales6076.BSuoS", year),
      "otherSales"      -> turnoverSalesMappingWithYear("costOfSales6076.otherSales", year)
    )(CostOfSales6076Sum.apply)(o => Some(Tuple.fromProductTyped(o)))

  def costOfSales6076Mapping(years: Seq[String])(using messages: Messages): Mapping[Seq[CostOfSales6076Sum]] =
    mappingPerYear(years, (year, idx) => s"[$idx]" -> sumMapping(year))

  def costOfSales6076Form(years: Seq[String])(using messages: Messages): Form[(Seq[CostOfSales6076Sum], String)] =
    Form {
      tuple(
        "costOfSales6076"   -> costOfSales6076Mapping(years),
        "otherSalesDetails" -> mandatoryStringIfNonZeroSum(
          ".otherSales",
          "error.costOfSales6076.details.required"
        ).verifying(
          maxLength(2000, "error.costOfSales6076.otherSalesDetails.maxLength")
        )
      )
    }

}
