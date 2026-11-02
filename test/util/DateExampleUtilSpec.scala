/*
 * Copyright 2026 HM Revenue & Customs
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

import uk.gov.hmrc.vo.unit.test.BaseSpec
import util.DateExampleUtil.*

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.util.Locale

/**
  * @author Yuriy Tumakha
  */
class DateExampleUtilSpec extends BaseSpec:

  private val ukTimezone                   = ZoneId.of("Europe/London")
  private val nowInUK                      = ZonedDateTime.now(ukTimezone)
  private val testLocalDate: LocalDate     = LocalDate.of(2024, 2, 12)
  private val dayMonthYearExampleFormatter = DateTimeFormatter.ofPattern("d M yyyy", Locale.UK)
  private val monthYearExampleFormatter    = DateTimeFormatter.ofPattern("M yyyy", Locale.UK)
  private val dayMonthExampleFormatter     = DateTimeFormatter.ofPattern("d M", Locale.UK)

  "DateExampleUtil" should {
    "return example DayMonthYear" in {
      exampleDayMonthYear(0) shouldBe nowInUK.format(dayMonthYearExampleFormatter)
    }

    "return example MonthYear" in {
      exampleMonthYear(0) shouldBe nowInUK.format(monthYearExampleFormatter)
    }

    "return example DayMonth" in {
      exampleDayMonth(0) shouldBe nowInUK.format(dayMonthExampleFormatter)
    }
  }

  "LocalDate extension" should {
    "convert LocalDate to Epoch Milliseconds using ukTimezone" in {
      val result: Long = testLocalDate.toEpochMilli

      val expected: Long = testLocalDate.atStartOfDay(ukTimezone).toInstant.toEpochMilli

      result shouldBe expected
    }

    "format LocalDate using short date format 'dd/MM/yyyy'" in {
      testLocalDate.shortSlashDate shouldBe "12/02/2024"
    }
  }
