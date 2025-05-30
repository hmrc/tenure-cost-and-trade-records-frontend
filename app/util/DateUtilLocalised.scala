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

package util

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.{TimeZone, ULocale}
import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils
import util.DateUtil.*

import java.time.{LocalDate, MonthDay, YearMonth}
import javax.inject.{Inject, Singleton}

@Singleton
class DateUtilLocalised @Inject() (langUtil: LanguageUtils) {

  private val ibmTimeZone: TimeZone = TimeZone.getTimeZone(defaultTimeZoneId)

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

  /**
    * Date format "d MMMM y".
    */
  def formatDate(date: LocalDate)(using messages: Messages): String =
    langUtil.Dates.formatDate(date)

  /**
    * Date format "MMMM yyyy".
    */
  def formatYearMonth(yearMonth: YearMonth)(using messages: Messages): String =
    monthYearFormat(messages.lang.code)
      .format(yearMonth.atDay(1).toEpochMilli)

  /**
    * Date format "d MMMM".
    */
  def formatMonthDay(monthDay: MonthDay)(using messages: Messages): String =
    dayMonthFormat(messages.lang.code)
      .format(monthDay.atYear(2023).toEpochMilli)

  /**
    * Date format "d MMM yyyy".
    */
  def formatDayMonthAbbrYear(date: LocalDate)(using messages: Messages): String =
    dayMonthAbbrYearFormat(messages.lang.code)
      .format(date.toEpochMilli)

}
