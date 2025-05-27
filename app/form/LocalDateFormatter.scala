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

import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import util.DateUtil.nowInUK

import java.time.LocalDate
import scala.util.Try

/**
  * Handles binding 3 date fields to [[java.time.LocalDate LocalDate]] and unbinding from LocalDate to 3 date fields.
  *
  * @author Yuriy Tumakha
  */
class LocalDateFormatter(
  fieldNameKey: String,
  allowPastDates: Boolean,
  allowFutureDates: Boolean,
  years: Option[Seq[Int]] = None
)(using messages: Messages)
    extends Formatter[LocalDate] {

  require(
    allowPastDates || allowFutureDates,
    s"${getClass.getSimpleName} must be configured to allow past or future dates"
  )

  private val allDateFields   = Seq("day", "month", "year")
  private val nineteenHundred = LocalDate.of(1900, 1, 1)

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val indexFromKey = key.split("\\[|\\]")
    val fieldIndex   = indexFromKey.lift(1).flatMap(s => Try(s.toInt).toOption).getOrElse(0)

    val yearForField = years.flatMap(_.lift(fieldIndex))

    val fieldName            = yearForField match {
      case Some(year) => messages(s"fieldName.$fieldNameKey", year.toString)
      case None       => messages(s"fieldName.$fieldNameKey")
    }
    val fieldNameCapitalized = fieldName.capitalize
    val dayKey               = s"$key.day"

    single(
      key -> tuple(
        "day"   -> optional(text),
        "month" -> optional(text),
        "year"  -> optional(text)
      )
    ).bind(data).flatMap {
      case (None, None, None) =>
        oneError(dayKey, "error.date.required", Seq(fieldName, allDateFields))

      case (Some(d), Some(m), Some(y)) =>
        val day          = parseNumber(d, 1 to 31)
        val month        = parseNumber(m, 1 to 12)
        val year         = parseNumber(y, 1000 to 9999)
        val datePartsSeq = Seq(day, month, year)

        val invalidFields = Seq(
          (day, s"$key.day", "error.date.day.invalid"),
          (month, s"$key.month", "error.date.month.invalid"),
          (year, s"$key.year", "error.date.year.invalid")
        ).filter(_._1 == 0)

        invalidFields match {
          case Seq((_, field, error))                      =>
            oneError(field, error, Seq(fieldNameCapitalized, field))
          case multipleErrors if multipleErrors.length > 1 =>
            val errorFields = (datePartsSeq zip allDateFields).filter(_._1 == 0).map(_._2)
            val focusKey    = s"$key.${errorFields.head}"
            oneError(focusKey, "error.date.invalid", Seq(fieldNameCapitalized, errorFields))
          case _                                           =>
            validateDate(day, month, year).left.map { errorKey =>
              Seq(FormError(key, errorKey, Seq(fieldNameCapitalized, allDateFields)))
            }
        }

      case (d, m, y) =>
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

        if date.isBefore(nineteenHundred) then Left("error.date.before1900")
        else if !allowPastDates && date.isBefore(todayUKDate) then Left("error.date.beforeToday")
        else if !allowFutureDates && date.isAfter(todayUKDate) then Left("error.date.mustBeInPast")
        else Right(date)
      }

}
