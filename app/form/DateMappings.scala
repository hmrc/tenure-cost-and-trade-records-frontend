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

package form

import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.i18n.Messages

import java.time.LocalDate

object DateMappings {

  def requiredDateMapping(
    fieldNameKey: String,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false
  )(implicit messages: Messages): Mapping[LocalDate] =
    of(new LocalDateFormatter(fieldNameKey, allowPastDates, allowFutureDates))

  def monthYearMapping(
    fieldNameKey: String,
    monthYearSfx: Option[String] = None,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false
  )(implicit messages: Messages): Mapping[MonthsYearDuration] =
    of(new MonthYearFormatter(fieldNameKey, monthYearSfx, allowPastDates, allowFutureDates))

  def dayMonthMapping(
    fieldNameKey: String,
    dayMonthSfx: Option[String] = None,
    allow29February: Boolean = false
  )(implicit messages: Messages): Mapping[DayMonthsDuration] =
    of(new DayMonthFormatter(fieldNameKey, dayMonthSfx, allow29February))

}
