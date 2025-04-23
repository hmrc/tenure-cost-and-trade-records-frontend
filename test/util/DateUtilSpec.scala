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
import util.DateUtil.localDateHelpers

import java.text.SimpleDateFormat
import java.time.{LocalDate, Year, ZoneId}
import java.util.Date

/**
  * @author Yuriy Tumakha
  */
class DateUtilSpec extends PlaySpec:

  private val ukTimezone               = ZoneId.of("Europe/London")
  private val testDate: Date           = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-02-12 12:34:56")
  private val testLocalDate: LocalDate = testDate.toInstant.atZone(ukTimezone).toLocalDate
  private val testMinusYears           = Year.now.getValue - 2025

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
      DateUtil.exampleDayMonthYear(testMinusYears) mustBe "22 4 2025"
    }

    "return example MonthYear" in {
      DateUtil.exampleMonthYear(testMinusYears) mustBe "4 2025"
    }

    "return example DayMonth" in {
      DateUtil.exampleDayMonth(testMinusYears) mustBe "22 4"
    }
  }
