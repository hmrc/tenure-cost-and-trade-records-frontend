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

package util

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.{TimeZone, ULocale}
import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils
import util.DateUtil.{dayMonthAbbrYearFormat, dayMonthFormat, localDateHelpers, monthYearFormat}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, MonthDay, YearMonth, ZoneId, ZonedDateTime}
import java.util.Locale
import javax.inject.{Inject, Singleton}

/**
  * @author Yuriy Tumakha
  */
object DateUtil {

  implicit class localDateHelpers(localDate: LocalDate) {
    def shortDate: String =
      localDate.format(shortDateFormatter)

    def abbrMonthDate(langCode: String): String =
      langCode match {
        case "cy" => localDate.format(dayMonAbbrYearFormat.withLocale(Locale.forLanguageTag(langCode)))
        case _    => localDate.format(dayMonAbbrYearFormat)
      }

    def toEpochMilli: Long =
      localDate.atStartOfDay(ukTimezone).toInstant.toEpochMilli

  }

  private val defaultTimeZoneId     = "Europe/London"
  private val ukTimezone: ZoneId    = ZoneId.of(defaultTimeZoneId)
  private val ibmTimeZone: TimeZone = TimeZone.getTimeZone(defaultTimeZoneId)

  private val shortDateFormatter: DateTimeFormatter           = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
  private val dayMonAbbrYearFormat: DateTimeFormatter         = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK)
  private val dayMonthYearExampleFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d M yyyy", Locale.UK)
  private val monthYearExampleFormatter: DateTimeFormatter    = DateTimeFormatter.ofPattern("M yyyy", Locale.UK)
  private val dayMonthExampleFormatter: DateTimeFormatter     = DateTimeFormatter.ofPattern("d M", Locale.UK)

  private val monthYearFormatEN: SimpleDateFormat = createDateFormatForPattern("MMMM yyyy", "en")
  private val monthYearFormatCY: SimpleDateFormat = createDateFormatForPattern("MMMM yyyy", "cy")

  private val monthYearFormat: Map[String, SimpleDateFormat] = Map(
    "en" -> monthYearFormatEN,
    "cy" -> monthYearFormatCY
  ).withDefaultValue(monthYearFormatEN)

  private val dayMonthFormatEN: SimpleDateFormat = createDateFormatForPattern("d MMMM", "en")
  private val dayMonthFormatCY: SimpleDateFormat = createDateFormatForPattern("d MMMM", "cy")

  private val dayMonthFormat: Map[String, SimpleDateFormat] = Map(
    "en" -> dayMonthFormatEN,
    "cy" -> dayMonthFormatCY
  ).withDefaultValue(dayMonthFormatEN)

  private val dayMonthAbbrYearFormatEN: SimpleDateFormat = createDateFormatForPattern("d MMM yyyy", "en")
  private val dayMonthAbbrYearFormatCY: SimpleDateFormat = createDateFormatForPattern("d MMM yyyy", "cy")

  private val dayMonthAbbrYearFormat: Map[String, SimpleDateFormat] = Map(
    "en" -> dayMonthAbbrYearFormatEN,
    "cy" -> dayMonthAbbrYearFormatCY
  ).withDefaultValue(dayMonthAbbrYearFormatEN)

  private def createDateFormatForPattern(pattern: String, langCode: String): SimpleDateFormat = {
    val uLocale         = new ULocale(langCode)
    val locale: ULocale = if (ULocale.getAvailableLocales.contains(uLocale)) uLocale else ULocale.getDefault
    val sdf             = new SimpleDateFormat(pattern, locale)
    sdf.setTimeZone(ibmTimeZone)
    sdf
  }

  def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

  def exampleDayMonthYear(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(dayMonthYearExampleFormatter)

  def exampleMonthYear(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(monthYearExampleFormatter)

  def exampleDayMonth(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(dayMonthExampleFormatter)

}

@Singleton
class DateUtil @Inject() (langUtil: LanguageUtils) {

  /**
    * Date format "d MMMM y".
    */
  def formatDate(date: LocalDate)(implicit messages: Messages): String =
    langUtil.Dates.formatDate(date)

  /**
    * Date format "MMMM yyyy".
    */
  def formatYearMonth(yearMonth: YearMonth)(implicit messages: Messages): String =
    monthYearFormat(messages.lang.code)
      .format(yearMonth.atDay(1).toEpochMilli)

  /**
    * Date format "d MMMM".
    */
  def formatMonthDay(monthDay: MonthDay)(implicit messages: Messages): String =
    dayMonthFormat(messages.lang.code)
      .format(monthDay.atYear(2023).toEpochMilli)

  /**
    * Date format "d MMM yyyy".
    */
  def formatDayMonthAbbrYear(date: LocalDate)(implicit messages: Messages): String =
    dayMonthAbbrYearFormat(messages.lang.code)
      .format(date.toEpochMilli)

}
