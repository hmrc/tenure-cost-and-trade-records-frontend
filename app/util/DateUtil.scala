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

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.util.Locale

/**
  * @author Yuriy Tumakha
  */
object DateUtil:

  extension (localDate: LocalDate)
    def shortDate: String =
      localDate.format(shortDateFormatter)

    def toEpochMilli: Long =
      localDate.atStartOfDay(ukTimezone).toInstant.toEpochMilli

  val defaultTimeZoneId          = "Europe/London"
  private val ukTimezone: ZoneId = ZoneId.of(defaultTimeZoneId)

  private val shortDateFormatter: DateTimeFormatter           = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
  private val dayMonthYearExampleFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d M yyyy", Locale.UK)
  private val monthYearExampleFormatter: DateTimeFormatter    = DateTimeFormatter.ofPattern("M yyyy", Locale.UK)
  private val dayMonthExampleFormatter: DateTimeFormatter     = DateTimeFormatter.ofPattern("d M", Locale.UK)

  def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

  def exampleDayMonthYear(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(dayMonthYearExampleFormatter)

  def exampleMonthYear(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(monthYearExampleFormatter)

  def exampleDayMonth(minusYears: Int): String =
    nowInUK.minusYears(minusYears).format(dayMonthExampleFormatter)
