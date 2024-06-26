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
import models.submissions.aboutthetradinghistory.GrossReceiptsExcludingVAT
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object GrossReceiptsExcludingVATForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[GrossReceiptsExcludingVAT] = mapping(
    "electricitySales"      -> turnoverSalesMappingWithYear("grossReceipts.electricitySales.sales", year),
    "feedInTariff"          -> turnoverSalesMappingWithYear("grossReceipts.feedInTariff.sales", year),
    "rocBuyout"             -> turnoverSalesMappingWithYear("grossReceipts.rocBuyout.sales", year),
    "rocRecycle"            -> turnoverSalesMappingWithYear("grossReceipts.rocRecycle.sales", year),
    "contractForDifference" -> turnoverSalesMappingWithYear("grossReceipts.contractForDifference.sales", year),
    "capacityMarket"        -> turnoverSalesMappingWithYear("grossReceipts.capacityMarket.sales", year),
    "balancingServices"     -> turnoverSalesMappingWithYear("grossReceipts.balancingServices.sales", year),
    "embeddedBenefits"      -> turnoverSalesMappingWithYear("grossReceipts.embeddedBenefits.sales", year)
  )(GrossReceiptsExcludingVAT.apply)(GrossReceiptsExcludingVAT.unapply)

  def grossReceiptsExcludingVATForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[GrossReceiptsExcludingVAT]] =
    Form {
      mappingPerYear(years, (year, idx) => s"grossReceiptsExcludingVAT[$idx]" -> columnMapping(year))
    }

}
