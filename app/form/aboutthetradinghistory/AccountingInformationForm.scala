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

import form.DateMappings.dayMonthMapping
import models.submissions.Form6010.DayMonthsDuration
import play.api.data.Form
import play.api.data.Forms.{optional, text, tuple}
import play.api.i18n.Messages

object AccountingInformationForm {

  def accountingInformationForm(using messages: Messages): Form[(DayMonthsDuration, Boolean)] = Form(
    tuple(
      "financialYear"  -> dayMonthMapping("financialYear", allow29February = false),
      "yearEndChanged" -> optional(text)
        .transform[Boolean](_.contains("true"), b => Some(b.toString))
    )
  )

}
