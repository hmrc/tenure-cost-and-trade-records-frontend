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
import form.DateMappings.dayMonthMapping
import models.submissions.Form6010.DayMonthsDuration
import models.submissions.lettingHistory.LocalPeriod
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import java.time.{LocalDate, Year}

object TradingSeasonForm extends Object with RentalPeriodSupport:

  private def toLocalDate(dm: DayMonthsDuration): LocalDate =
    LocalDate.of(Year.now.getValue, dm.months, dm.days)

  private def fromLocalDate(date: LocalDate): DayMonthsDuration =
    DayMonthsDuration(date.getDayOfMonth, date.getMonthValue)

  def theForm(using messages: Messages): Form[LocalPeriod] =
    Form[LocalPeriod](
      mapping(
        "fromDate" -> dayMonthMapping("fromDate").transform[LocalDate](toLocalDate, fromLocalDate),
        "toDate"   -> dayMonthMapping("toDate").transform[LocalDate](toLocalDate, fromLocalDate)
      )(LocalPeriod.apply)(LocalPeriod.unapply)
    )
