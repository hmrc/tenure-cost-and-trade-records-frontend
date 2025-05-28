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
import models.submissions.aboutthetradinghistory.PremisesCosts
import play.api.data.Forms.{mapping, single}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object PremisesCostsForm {

  def premisesCostsForm(
    years: Seq[String]
  )(using messages: Messages): Form[Seq[PremisesCosts]] =
    Form {
      single(
        "premisesCosts" -> mappingPerYear(years, (year, idx) => s"[$idx]" -> sumMapping(year))
      )
    }

  private def sumMapping(year: String)(using messages: Messages): Mapping[PremisesCosts] =
    mapping(
      "energyAndUtilities"           -> turnoverSalesMappingWithYear(
        "premisesCosts.energyAndUtilities",
        year
      ),
      "buildingRepairAndMaintenance" -> turnoverSalesMappingWithYear(
        "premisesCosts.buildingRepairAndMaintenance",
        year
      ),
      "repairsAndRenewalsOfFixtures" -> turnoverSalesMappingWithYear(
        "premisesCosts.repairsAndRenewalsOfFixtures",
        year
      ),
      "rent"                         -> turnoverSalesMappingWithYear("premisesCosts.rent", year),
      "businessRates"                -> turnoverSalesMappingWithYear("premisesCosts.businessRates", year),
      "buildingInsurance"            -> turnoverSalesMappingWithYear("premisesCosts.buildingInsurance", year)
    )(PremisesCosts.apply)(o => Some(Tuple.fromProductTyped(o)))
}
