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
import play.api.data.Forms._
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object HeadOfficeExpensesForm {

  private def columnMapping(year: String)(using messages: Messages): Mapping[Option[BigDecimal]] = single(
    "headOfficeExpenses" -> turnoverSalesMappingWithYear("turnover.6076.headOfficeExpenses", year)
  )

  private def headOfficeExpensesSeq(
    years: Seq[String]
  )(using messages: Messages): Mapping[Seq[Option[BigDecimal]]] =
    mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))

  def headOfficeExpensesForm(
    years: Seq[String]
  )(using messages: Messages): Form[(Seq[Option[BigDecimal]], Option[String])] =
    Form {
      tuple(
        "headOfficeExpensesSeq"       -> headOfficeExpensesSeq(years),
        "furtherInformationOrRemarks" -> optional(
          text.verifying(maxLength(2000, "error.turnover.6076.furtherInformationOrRemarks.maxLength"))
        )
      )
    }

}
