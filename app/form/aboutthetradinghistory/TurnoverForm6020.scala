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

import form.MappingSupport._
import models.submissions.aboutthetradinghistory.TurnoverSection6020
import play.api.data.Forms.{ignored, mapping}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object TurnoverForm6020 {

  private def columnMapping(year: String)(using messages: Messages): Mapping[TurnoverSection6020] = mapping(
    "financial-year-end" -> ignored(LocalDate.EPOCH),
    "shop"               -> turnoverSalesMappingWithYear("turnover.6020.shop", year),
    "carWash"            -> turnoverSalesMappingWithYear("turnover.6020.carWash", year),
    "jetWash"            -> turnoverSalesMappingWithYear("turnover.6020.jetWash", year),
    "lottery"            -> turnoverSalesMappingWithYear("turnover.6020.lottery", year),
    "payPointOrZone"     -> turnoverSalesMappingWithYear("turnover.6020.payPointOrZone", year),
    "otherIncome"        -> turnoverSalesMappingWithYear("turnover.6020.otherIncome", year)
  )(TurnoverSection6020.apply)(o => Some(Tuple.fromProductTyped(o)))

  def turnoverForm6020(years: Seq[String])(using messages: Messages): Form[Seq[TurnoverSection6020]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))
    }

}
