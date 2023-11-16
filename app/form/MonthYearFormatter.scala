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

import models.submissions.Form6010.MonthsYearDuration
import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import util.DateUtil.nowInUK

import java.time.LocalDate
import scala.collection.immutable.Seq
import scala.util.Try

/**
  * Handles binding [mm] [yyyy] fields to MonthsYearDuration and unbinding MonthsYearDuration to [mm] [yyyy] fields.
  *
  * @author Yuriy Tumakha
  */
class MonthYearFormatter(
  fieldNameKey: String,
  allowPastDates: Boolean,
  allowFutureDates: Boolean
)(implicit messages: Messages)
    extends Formatter[MonthsYearDuration] {

  private val monthYearFields = Seq("month", "year")

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], MonthsYearDuration] = {
    val fieldName        = messages(s"fieldName.$fieldNameKey")
    val fieldCapitalized = fieldName.capitalize
    val monthText        = messages("error.dateParts.month")
    val yearText         = messages("error.dateParts.year")
    val monthKey         = s"$key.month"
    val yearKey          = s"$key.year"

    single(
      key -> tuple(
        "month" -> optional(text),
        "year"  -> optional(text)
      )
    ).bind(data).flatMap {
      case (None, None)       => oneError(monthKey, "error.date.required", Seq(fieldName, monthYearFields))
      case (None, Some(_))    =>
        oneError(monthKey, "error.date.mustInclude", Seq(fieldCapitalized, monthText, Seq("month")))
      case (Some(_), None)    =>
        oneError(yearKey, "error.date.mustInclude", Seq(fieldCapitalized, yearText, Seq("year")))
      case (Some(m), Some(y)) =>
        val month = Try(m.trim.toInt).filter((1 to 12) contains _).getOrElse(0)
        val year  = Try(y.trim.toInt).filter((1900 to 9999) contains _).getOrElse(0)
        if (Seq(month, year).forall(_ > 0)) {
          validateDate(month, year).left.map { errorKey =>
            Seq(FormError(monthKey, errorKey, Seq(fieldCapitalized, monthYearFields)))
          }
        } else {
          Left(
            Seq(
              Option.when(month == 0)(FormError(monthKey, "error.date.month.invalid")),
              Option.when(year == 0)(FormError(yearKey, "error.date.year.invalid", Seq(fieldCapitalized)))
            ).flatten
          )
        }
    }
  }

  override def unbind(key: String, value: MonthsYearDuration): Map[String, String] =
    Map(
      s"$key.month" -> value.months.toString,
      s"$key.year"  -> value.years.toString
    )

  private def oneError(key: String, message: String, args: Seq[Any]): Left[Seq[FormError], MonthsYearDuration] =
    Left(Seq(FormError(key, message, args)))

  private def validateDate(month: Int, year: Int): Either[String, MonthsYearDuration] =
    Try(LocalDate.of(year, month, 1)).toEither.left
      .map(_ => "error.date.invalid")
      .flatMap { date =>
        val today        = nowInUK.toLocalDate
        val startOfMonth = LocalDate.of(today.getYear, today.getMonthValue, 1)

        if (!allowPastDates && date.isBefore(startOfMonth)) {
          Left("error.date.beforeToday")
        } else if (!allowFutureDates && date.isAfter(startOfMonth)) {
          Left("error.date.mustBeInPast")
        } else {
          Right(MonthsYearDuration(date.getMonthValue, date.getYear))
        }
      }

}
