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

import form.DateMappings.requiredDateMapping
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

object FinancialYearEndDatesForm {

  private def dateMapping(implicit messages: Messages): Mapping[LocalDate] =
    single(
      "date" -> requiredDateMapping(
        "financialYearEnd",
        allowPastDates = true,
        allowFutureDates = true
      )
    )

  def financialYearEndDatesForm(implicit messages: Messages): Form[Seq[LocalDate]] = Form(
    single(
      "financial-year-end" -> seq(dateMapping)
    )
  )

}
