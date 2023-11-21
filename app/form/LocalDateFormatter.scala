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

import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import util.DateUtil.nowInUK

import java.time.LocalDate
import scala.util.Try

/**
  * Handles binding 3 date fields to {@link java.time.LocalDate LocalDate} and unbinding from LocalDate to 3 date fields.
  *
  * @author Yuriy Tumakha
  */
class LocalDateFormatter(
  fieldNameKey: String,
  allowPastDates: Boolean,
  allowFutureDates: Boolean
)(implicit messages: Messages)
    extends Formatter[LocalDate] {

  require(allowPastDates || allowFutureDates, s"${getClass.getSimpleName} must be configured to allow past or future dates")

  private val allDateFields   = Seq("day", "month", "year")
  private val nineteenHundred = LocalDate.of(1900, 1, 1)

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
    val fieldName            = messages(s"fieldName.$fieldNameKey")
    val fieldNameCapitalized = fieldName.capitalize
    val dayKey               = s"$key.day"

    single(
      key -> tuple(
        "day"   -> optional(text),
        "month" -> optional(text),
        "year"  -> optional(text)
      )
    ).bind(data).flatMap {
      case (None, None, None)          => oneError(dayKey, "error.date.required", Seq(fieldName, allDateFields))
      case (Some(d), Some(m), Some(y)) =>
        val day          = parseNumber(d, 1 to 31)
        val month        = parseNumber(m, 1 to 12)
        val year         = parseNumber(y, 1000 to 9999)
        val datePartsSeq = Seq(day, month, year)
        if (datePartsSeq.forall(_ > 0)) {
          validateDate(day, month, year).left.map { errorKey =>
            Seq(FormError(dayKey, errorKey, Seq(fieldNameCapitalized, allDateFields)))
          }
        } else {
          val invalidFields = (datePartsSeq zip allDateFields).filter(_._1 == 0).map(_._2)
          val focusKey      = s"$key.${invalidFields.head}"
          oneError(focusKey, "error.date.invalid", Seq(fieldNameCapitalized, invalidFields))
        }
      case (d, m, y)                   =>
        val prefix           = messages("error.dateParts.prefix")
        val separator        = messages("error.dateParts.separator")
        val dayText          = messages("error.dateParts.day")
        val monthText        = messages("error.dateParts.month")
        val yearText         = messages("error.dateParts.year")
        val missedFields     = (Seq(d, m, y) zip allDateFields).filter(_._1.isEmpty).map(_._2)
        val missedFieldsText = (Seq(d, m, y) zip Seq(dayText, monthText, yearText)).filter(_._1.isEmpty).map(_._2)
        val arg1             = missedFieldsText.mkString(s"$prefix ", s" $separator ", "")
        val focusKey         = s"$key.${missedFields.head}"
        oneError(focusKey, "error.date.mustInclude", Seq(fieldNameCapitalized, arg1, missedFields))
    }
  }

  override def unbind(key: String, value: LocalDate): Map[String, String] =
    Map(
      s"$key.day"   -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString,
      s"$key.year"  -> value.getYear.toString
    )

  private def parseNumber(str: String, allowedRange: Range): Int =
    Try(str.trim.toInt).filter(allowedRange.contains).getOrElse(0)

  private def oneError(key: String, message: String, args: Seq[Any]): Left[Seq[FormError], LocalDate] =
    Left(Seq(FormError(key, message, args)))

  private def validateDate(day: Int, month: Int, year: Int): Either[String, LocalDate] =
    Try(LocalDate.of(year, month, day)).toEither.left
      .map(_ => "error.date.invalid")
      .flatMap { date =>
        val todayUKDate = nowInUK.toLocalDate

        if (date.isBefore(nineteenHundred)) {
          Left("error.date.before1900")
        } else if (!allowPastDates && date.isBefore(todayUKDate)) {
          Left("error.date.beforeToday")
        } else if (!allowFutureDates && date.isAfter(todayUKDate)) {
          Left("error.date.mustBeInPast")
        } else {
          Right(date)
        }
      }

}
