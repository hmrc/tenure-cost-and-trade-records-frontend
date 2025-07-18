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

package form.lettingHistory

import controllers.lettingHistory.RentalPeriodSupport
import form.DateMappings.requiredDateMapping as requiredDate
import play.api.data.Form
import play.api.data.Forms.single
import play.api.i18n.Messages

import java.time.LocalDate

object WhenWasLastLetForm extends Object with RentalPeriodSupport:

  def theForm(using messages: Messages): Form[LocalDate] =
    Form[LocalDate](
      single(
        "date" -> requiredDate(fieldNameKey = "lettingHistory.date", allowPastDates = true)
      )
    )
