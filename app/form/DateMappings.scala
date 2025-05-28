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

package form

import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import play.api.data.Forms.*
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.Messages
import util.DateUtilLocalised

import java.time.LocalDate

object DateMappings {
  def requiredDateMapping(
    fieldNameKey: String,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false,
    years: Option[Seq[Int]] = None
  )(using messages: Messages): Mapping[LocalDate] =
    of(using new LocalDateFormatter(fieldNameKey, allowPastDates, allowFutureDates, years))

  def monthYearMapping(
    fieldNameKey: String,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false
  )(using messages: Messages): Mapping[MonthsYearDuration] =
    of(using new MonthYearFormatter(fieldNameKey, allowPastDates, allowFutureDates))

  def dayMonthMapping(
    fieldNameKey: String,
    allow29February: Boolean = false
  )(using messages: Messages): Mapping[DayMonthsDuration] =
    of(using new DayMonthFormatter(fieldNameKey, allow29February))

  def betweenDates(
    startDate: LocalDate,
    endDate: LocalDate,
    errorMessage: String = "error.range"
  )(using messages: Messages, dateUtil: DateUtilLocalised): Constraint[LocalDate] =
    Constraint[LocalDate]("constraint.between.dates", startDate, endDate) { date =>
      if date.isBefore(startDate) || date.isAfter(endDate) then
        val rangeStartEnd = Seq(startDate, endDate).map(dateUtil.formatDate)
        Invalid(ValidationError(errorMessage, rangeStartEnd*))
      else Valid
    }

}
