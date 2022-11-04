/*
 * Copyright 2022 HM Revenue & Customs
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
import models.submissions.Form6010.MonthsYearDuration
import org.joda.time.{DateTime, LocalDate}
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.util.Try

object DateMappings {

  private val nineteenHundred = new DateTime(1900, 1, 1, 0, 0)

  private def dateIsInPastAndAfter1900(fieldErrorPart: String): Constraint[(String, String)] =
    Constraint("dateInPastAndAfter1900") { x =>
      val month                   = x._1.trim.toInt
      val year                    = x._2.trim.toInt
      val lastDayOfSpecifiedMonth = new DateTime(year, month, 1, 0, 0)
      if (!lastDayOfSpecifiedMonth.isBeforeNow)
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

      if (Try(new DateTime(year, month, day, 23, 59)).isFailure)
        Invalid(Errors.invalidDate + fieldErrorPart)
      else if (new DateTime(year, month, day, 23, 59).isBefore(nineteenHundred))
        Invalid(Errors.dateBefore1900 + fieldErrorPart)
      else
        Valid
    }

  private def fullDateIsInPastAndAfter1900(fieldErrorPart: String): Constraint[(String, String, String)] =
    Constraint("fullDateIsInPastAndAfter1900") { x =>
      val day   = x._1.trim.toInt
      val month = x._2.trim.toInt
      val year  = x._3.trim.toInt

      if (Try(new DateTime(year, month, day, 23, 59)).isFailure)
        Invalid(Errors.invalidDate + fieldErrorPart)
      else {
        val date = new DateTime(year, month, day, 23, 59)
        if (date.isAfterNow)
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
    { case (day, month, year) => new LocalDate(year.trim.toInt, month.trim.toInt, day.trim.toInt) },
    (date: LocalDate) => (date.getDayOfMonth.toString, date.getMonthOfYear.toString, date.getYear.toString)
  )

  def monthsYearDurationMapping(prefix: String, fieldErrorPart: String = ""): Mapping[MonthsYearDuration] = tuple(
    "years"  -> nonEmptyTextOr(
      prefix + ".years",
      text.verifying(
        Errors.invalidDurationYears,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 0 && x.trim.toInt <= 999
      ),
      s"error$fieldErrorPart.years.required"
    ),
    "months" -> nonEmptyTextOr(
      prefix + ".months",
      text.verifying(
        Errors.invalidDurationMonths,
        x => x.trim.forall(Character.isDigit) && x.trim.toInt >= 0 && x.trim.toInt <= 12
      ),
      s"error$fieldErrorPart.months.required"
    )
  ).transform(
    { case (years, months) =>
      MonthsYearDuration(months.trim.toInt, years.trim.toInt)
    },
    (my: MonthsYearDuration) => (my.months.toString, my.years.toString)
  )

}
