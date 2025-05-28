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

import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.AdministrativeCosts6048
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object AdministrativeCosts6048Form {

  private def columnMapping(year: String)(using messages: Messages): Mapping[AdministrativeCosts6048] =
    mapping(
      "subscriptionsAndFees"                -> turnoverSalesMappingWithYear(
        "turnover.6048.administrativeCosts.subscriptionsAndFees",
        year
      ),
      "telephoneBroadbandStationeryPostage" -> turnoverSalesMappingWithYear(
        "turnover.6048.administrativeCosts.telephoneBroadbandStationeryPostage",
        year
      ),
      "bookingAgency"                       -> turnoverSalesMappingWithYear("turnover.6048.administrativeCosts.bookingAgency", year),
      "advertising"                         -> turnoverSalesMappingWithYear("turnover.6048.administrativeCosts.advertising", year),
      "printing"                            -> turnoverSalesMappingWithYear("turnover.6048.administrativeCosts.printing", year)
    )(AdministrativeCosts6048.apply)(o => Some(Tuple.fromProductTyped(o)))

  def administrativeCosts6048Form(
    years: Seq[String]
  )(using messages: Messages): Form[Seq[AdministrativeCosts6048]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))
    }

}
