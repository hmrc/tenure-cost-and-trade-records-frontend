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

import form.Form6010.ConditionalMapping._
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

object DateMappings {

  def requiredDateMapping(
    fieldNameKey: String,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false
  )(implicit messages: Messages): Mapping[LocalDate] =
    of(new LocalDateFormatter(fieldNameKey, allowPastDates, allowFutureDates))

  def monthYearMapping(
    fieldNameKey: String,
    allowPastDates: Boolean = false,
    allowFutureDates: Boolean = false
  )(implicit messages: Messages): Mapping[MonthsYearDuration] =
    of(new MonthYearFormatter(fieldNameKey, allowPastDates, allowFutureDates))

  def isDayMonthValidDate(day: Int, month: Int): Boolean =
    Try(LocalDate.of(LocalDate.now().getYear, month, day)).isSuccess

  def dayMonthsDurationMapping(prefix: String, fieldErrorPart: String = ""): Mapping[DayMonthsDuration] = tuple(
    "day"   -> nonEmptyTextOr(
      prefix + ".day",
      text.verifying(
        Errors.invalidDurationDays,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 0 && x.trim.toInt <= 31
      ),
      s"error$fieldErrorPart.day.required"
    ),
    "month" -> nonEmptyTextOr(
      prefix + ".month",
      text.verifying(
        Errors.invalidDurationMonths,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 0 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.month.required"
    )
  ).verifying(
    "error.invalid_date",
    formData => {
      val (day, month) = (formData._1.trim.toInt, formData._2.trim.toInt)
      isDayMonthValidDate(day, month)
    }
  ).transform(
    { case (days, months) =>
      DayMonthsDuration(days.trim.toInt, months.trim.toInt)
    },
    (my: DayMonthsDuration) => (my.days.toString, my.months.toString)
  )

}
