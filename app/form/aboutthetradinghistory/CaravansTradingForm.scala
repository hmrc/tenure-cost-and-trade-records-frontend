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

import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.Caravans.{CaravanLettingType, CaravanUnitType}
import models.submissions.aboutthetradinghistory.CaravansTrading6045
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * 6045/6046 Trading history form - for 4 pages - single/twinUnit caravans OwnedByOperator/SubletByOperator.
  *
  * @author Yuriy Tumakha
  */
object CaravansTradingForm {

  def caravansTradingForm(
    years: Seq[String],
    unitType: CaravanUnitType,
    lettingType: CaravanLettingType
  )(implicit messages: Messages): Form[Seq[CaravansTrading6045]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year, s"caravans.$unitType.$lettingType"))
    }

  private def columnMapping(year: String, combinedKey: String)(implicit
    messages: Messages
  ): Mapping[CaravansTrading6045] =
    mapping(
      "weeks"         -> tradingPeriodWeeks(year),
      "grossReceipts" -> turnoverSalesMappingWithYear(s"turnover.6045.$combinedKey.grossReceipts", year),
      "vans"          -> nonNegativeNumberWithYear(s"$combinedKey.vans", year)
    )(CaravansTrading6045.apply)(o => Some(Tuple.fromProductTyped(o)))

}
