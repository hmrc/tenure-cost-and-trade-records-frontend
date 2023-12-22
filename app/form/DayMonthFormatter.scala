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

import models.submissions.Form6010.DayMonthsDuration
import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages

import java.time.LocalDate
import scala.collection.immutable.Seq
import scala.util.Try

/**
  * Handles binding [dd] [mm] fields to DayMonthsDuration and unbinding DayMonthsDuration to [dd] [mm] fields.
  *
  * @author Yuriy Tumakha
  */
class DayMonthFormatter(
  fieldNameKey: String,
  allow29February: Boolean
)(implicit messages: Messages)
    extends Formatter[DayMonthsDuration] {

  private val dayMonthFields = Seq("day", "month")
  private val validationYear = if (allow29February) 2020 else 2021

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DayMonthsDuration] = {

    val dateText = messages("error.dateParts.date")
    val dMText   = messages("error.dateParts.dayMonth")

    val fieldName         = messages(s"fieldName.$fieldNameKey", dateText)
    val dayMonthFieldName = messages(s"fieldName.$fieldNameKey", dMText)

    val fieldCapitalized = fieldName.capitalize
    val prefix           = messages("error.dateParts.prefix")
    val dayText          = messages("error.dateParts.day")
    val monthText        = messages("error.dateParts.month")
    val dayKey           = s"$key.day"
    val monthKey         = s"$key.month"

    single(
      key -> tuple(
        "day"   -> optional(text),
        "month" -> optional(text)
      )
    ).bind(data).flatMap {
      case (None, None)       => oneError(dayKey, "error.date.required", Seq(dayMonthFieldName, dayMonthFields))
      case (None, Some(_))    =>
        oneError(dayKey, "error.date.mustInclude", Seq(fieldCapitalized, s"$prefix $dayText", Seq("day")))
      case (Some(_), None)    =>
        oneError(monthKey, "error.date.mustInclude", Seq(fieldCapitalized, s"$prefix $monthText", Seq("month")))
      case (Some(d), Some(m)) =>
        val day   = parseNumber(d, 1 to 31)
        val month = parseNumber(m, 1 to 12)
        if (Seq(day, month).forall(_ > 0)) {
          validateDayMonth(day, month).left.map { errorKey =>
            Seq(FormError(dayKey, errorKey, Seq(fieldCapitalized, dayMonthFields)))
          }
        } else {
          Left(
            Seq(
              Option.when(day == 0)(FormError(dayKey, "error.date.day.invalid")),
              Option.when(month == 0)(FormError(monthKey, "error.date.month.invalid"))
            ).flatten
          )
        }
    }
  }

  override def unbind(key: String, value: DayMonthsDuration): Map[String, String] =
    Map(
      s"$key.day"   -> value.days.toString,
      s"$key.month" -> value.months.toString
    )

  private def parseNumber(str: String, allowedRange: Range): Int =
    Try(str.trim.toInt).filter(allowedRange.contains).getOrElse(0)

  private def oneError(key: String, message: String, args: Seq[Any]): Left[Seq[FormError], DayMonthsDuration] =
    Left(Seq(FormError(key, message, args)))

  private def validateDayMonth(day: Int, month: Int): Either[String, DayMonthsDuration] =
    Try(LocalDate.of(validationYear, month, day)).toEither.left
      .map(_ => "error.date.invalid")
      .map(_ => DayMonthsDuration(day, month))

}
