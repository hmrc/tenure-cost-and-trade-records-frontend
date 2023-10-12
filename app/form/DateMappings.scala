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
import models.RoughDate
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid}
import util.DateUtil.nowInUK

import java.time.{LocalDate, LocalDateTime}
import scala.util.Try

object DateMappings {

  private val nineteenHundred     = LocalDateTime.of(1900, 1, 1, 0, 0)
  private val nineteenHundredDate = nineteenHundred.toLocalDate

  val dateTooEarlyConstraint: Constraint[LocalDate] = Constraint[LocalDate]("dateTooEarlyConstraint") { date =>
    if (date.isAfter(nineteenHundredDate)) Valid else Invalid(Errors.dateBefore1900)
  }

  private def fullDateIsAfterToday(fieldErrorPart: String): Constraint[(String, String, String)] =
    Constraint("fullDateIsAfterToday") { x =>
      val day   = x._1.trim.toInt
      val month = x._2.trim.toInt
      val year  = x._3.trim.toInt

      if (Try(LocalDateTime.of(year, month, day, 23, 59)).isFailure)
        Invalid(Errors.invalidDate + fieldErrorPart)
      else if (LocalDateTime.of(year, month, day, 23, 59).isBefore(nowInUK.toLocalDateTime))
        Invalid(Errors.dateBeforeToday + fieldErrorPart)
      else
        Valid
    }
  private def dateIsInPastAndAfter1900(fieldErrorPart: String): Constraint[(String, String)]     =
    Constraint("dateInPastAndAfter1900") { x =>
      val month                   = x._1.trim.toInt
      val year                    = x._2.trim.toInt
      val lastDayOfSpecifiedMonth = LocalDateTime.of(year, month, 1, 0, 0)
      if (!lastDayOfSpecifiedMonth.isBefore(LocalDateTime.now()))
        Invalid(Errors.dateMustBeInPast + fieldErrorPart)
      else if (lastDayOfSpecifiedMonth.isBefore(nineteenHundred))
        Invalid(Errors.dateBefore1900 + fieldErrorPart)
      else
        Valid
    }

  private def fullDateIsAfter1900(fieldErrorPart: String): Constraint[(String, String, String)] =
    Constraint("fullDateIsAfter1900") { x =>
      val day   = x._1.trim.toInt
      val month = x._2.trim.toInt
      val year  = x._3.trim.toInt

      if (Try(LocalDateTime.of(year, month, day, 23, 59)).isFailure)
        Invalid(Errors.invalidDate + fieldErrorPart)
      else if (LocalDateTime.of(year, month, day, 23, 59).isBefore(nineteenHundred))
        Invalid(Errors.dateBefore1900 + fieldErrorPart)
      else
        Valid
    }

  private def fullDateIsInPastAndAfter1900(fieldErrorPart: String): Constraint[(String, String, String)] =
    Constraint("fullDateIsInPastAndAfter1900") { x =>
      val day   = x._1.trim.toInt
      val month = x._2.trim.toInt
      val year  = x._3.trim.toInt

      if (Try(LocalDateTime.of(year, month, day, 23, 59)).isFailure)
        Invalid(Errors.invalidDate + fieldErrorPart)
      else {
        val date = LocalDateTime.of(year, month, day, 23, 59)
        if (date.isAfter(LocalDateTime.now()))
          Invalid(Errors.dateMustBeInPast + fieldErrorPart)
        else if (date.isBefore(nineteenHundred))
          Invalid(Errors.dateBefore1900 + fieldErrorPart)
        else
          Valid
      }
    }

  def monthYearRoughDateMapping(prefix: String, fieldErrorPart: String = ""): Mapping[RoughDate] = tuple(
    "month" -> nonEmptyTextOr(
      prefix + ".month",
      text.verifying(
        Errors.invalidDate,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.month.required"
    ),
    "year"  -> nonEmptyTextOr(
      prefix + ".year",
      text.verifying(Errors.invalidDate, x => x.trim.forall(Character.isDigit) && x.trim.length == 4),
      s"error$fieldErrorPart.year.required"
    )
  ).verifying(
    dateIsInPastAndAfter1900(fieldErrorPart)
  ).transform(
    { case (month, year) =>
      new RoughDate(month.trim.toInt, year.trim.toInt)
    },
    (date: RoughDate) => (date.month.getOrElse(0).toString, date.year.toString)
  )

  //  for precise dates, where all fields must be present and accurate
  def dateFieldsMapping(
    prefix: String,
    allowFutureDates: Boolean = false,
    fieldErrorPart: String = ""
  ): Mapping[LocalDate] = tuple(
    "day"   -> nonEmptyTextOr(
      prefix + ".day",
      text.verifying(
        Errors.invalidDate,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 31
      ),
      s"error$fieldErrorPart.day.required"
    ),
    "month" -> nonEmptyTextOr(
      prefix + ".month",
      text.verifying(
        Errors.invalidDate,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.month.required"
    ),
    "year"  -> nonEmptyTextOr(
      prefix + ".year",
      text.verifying(Errors.invalidDate, x => x.trim.forall(Character.isDigit) && x.trim.length == 4),
      s"error$fieldErrorPart.year.required"
    )
  ).verifying(
    if (allowFutureDates) fullDateIsAfter1900(fieldErrorPart)
    else fullDateIsInPastAndAfter1900(fieldErrorPart)
  ).transform(
    { case (day, month, year) => LocalDate.of(year.trim.toInt, month.trim.toInt, day.trim.toInt) },
    (date: LocalDate) => (date.getDayOfMonth.toString, date.getMonthValue.toString, date.getYear.toString)
  )

  def dateFieldsAfterTodayMapping(
    prefix: String,
    allowFutureDates: Boolean = false,
    fieldErrorPart: String = ""
  ): Mapping[LocalDate] = tuple(
    "day"   -> nonEmptyTextOr(
      prefix + ".day",
      text.verifying(
        Errors.invalidDate,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 31
      ),
      s"error$fieldErrorPart.day.required"
    ),
    "month" -> nonEmptyTextOr(
      prefix + ".month",
      text.verifying(
        Errors.invalidDate,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.month.required"
    ),
    "year"  -> nonEmptyTextOr(
      prefix + ".year",
      text.verifying(Errors.invalidDate, x => x.trim.forall(Character.isDigit) && x.trim.length == 4),
      s"error$fieldErrorPart.year.required"
    )
  ).verifying(
    if (allowFutureDates) fullDateIsAfterToday(fieldErrorPart)
    else fullDateIsAfterToday(fieldErrorPart)
  ).transform(
    { case (day, month, year) => LocalDate.of(year.trim.toInt, month.trim.toInt, day.trim.toInt) },
    (date: LocalDate) => (date.getDayOfMonth.toString, date.getMonthValue.toString, date.getYear.toString)
  )

  def monthsYearDurationMapping(prefix: String, fieldErrorPart: String = ""): Mapping[MonthsYearDuration] = tuple(
    "month" -> nonEmptyTextOr(
      prefix + ".month",
      text.verifying(
        Errors.invalidDurationMonths,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.month.required"
    ),
    "year"  -> nonEmptyTextOr(
      prefix + ".year",
      text.verifying(
        Errors.dateBefore1900,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 1900 && x.trim.toInt <= 9999
      ),
      s"error$fieldErrorPart.year.required"
    )
  ).transform(
    { case (months, years) =>
      MonthsYearDuration(months.trim.toInt, years.trim.toInt)
    },
    (my: MonthsYearDuration) => (my.months.toString, my.years.toString)
  )

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
