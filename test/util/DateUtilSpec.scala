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

import org.scalatestplus.play.PlaySpec
import util.DateUtil.*

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.util.Locale

/**
  * @author Yuriy Tumakha
  */
class DateUtilSpec extends PlaySpec:

  private val ukTimezone                   = ZoneId.of("Europe/London")
  private val nowInUK                      = ZonedDateTime.now(ukTimezone)
  private val testLocalDate: LocalDate     = LocalDate.of(2024, 2, 12)
  private val dayMonthYearExampleFormatter = DateTimeFormatter.ofPattern("d M yyyy", Locale.UK)
  private val monthYearExampleFormatter    = DateTimeFormatter.ofPattern("M yyyy", Locale.UK)
  private val dayMonthExampleFormatter     = DateTimeFormatter.ofPattern("d M", Locale.UK)

  "localDateHelpers" should {
    "convert LocalDate to Epoch Milliseconds using ukTimezone" in {
      val result: Long = testLocalDate.toEpochMilli

      val expected: Long = testLocalDate.atStartOfDay(ukTimezone).toInstant.toEpochMilli

      result mustBe expected
    }

    "format LocalDate using short date format 'dd/MM/yyyy'" in {
      testLocalDate.shortDate mustBe "12/02/2024"
    }
  }

  "DateUtil" should {
    "return example DayMonthYear" in {
      DateUtil.exampleDayMonthYear(0) mustBe nowInUK.format(dayMonthYearExampleFormatter)
    }

    "return example MonthYear" in {
      DateUtil.exampleMonthYear(0) mustBe nowInUK.format(monthYearExampleFormatter)
    }

    "return example DayMonth" in {
      DateUtil.exampleDayMonth(0) mustBe nowInUK.format(dayMonthExampleFormatter)
    }
  }
