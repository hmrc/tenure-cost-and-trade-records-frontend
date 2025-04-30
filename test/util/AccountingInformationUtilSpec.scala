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

import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.Logging

import java.time.{LocalDate, MonthDay, YearMonth}

/**
  * @author Yuriy Tumakha
  */
class AccountingInformationUtilSpec extends AnyFlatSpec with should.Matchers with Logging:

  private val today   = LocalDate.now
  private def yearNow = YearMonth.now.getYear

  private val financialYearEnd = DayMonthsDuration(5, 4)

  "AccountingInformationUtil.financialYearsRequired" should "return 3 dates" in {
    val firstOccupy = MonthsYearDuration(1, yearNow - 5)
    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 3)

    val firstOccupy2 = MonthsYearDuration(1, yearNow - 4)
    verifyFinancialYearEndDates(firstOccupy2, financialYearEnd, 3)
  }

  it should "return more dates" in {
    val firstOccupy = MonthsYearDuration(1, yearNow - 3)
    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 3)
  }

  it should "return 2 dates" in {
    val firstOccupy = MonthsYearDuration(1, yearNow - 2)
    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 2)
  }

  it should "return 3 dates with partial year " in {
    val firstOccupy = MonthsYearDuration(1, yearNow - 3)
    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 3)
  }

  private def getFirstYearEnd(financialYearEnd: MonthDay): LocalDate =
    val firstYearEnd = LocalDate.of(yearNow, financialYearEnd.getMonthValue, financialYearEnd.getDayOfMonth)
    if (!today.isBefore(firstYearEnd)) firstYearEnd else firstYearEnd.minusYears(1)

  private def verifyFinancialYearEndDates(
    firstOccupy: MonthsYearDuration,
    financialYearEnd: DayMonthsDuration,
    expectedColumns: Int
  ): Unit =
    val firstYearEnd  = getFirstYearEnd(financialYearEnd.toMonthDay)
    val expectedDates = (1 to expectedColumns).map(y => firstYearEnd.minusYears(y - 1))

    val endDates = AccountingInformationUtil.deriveFinancialYearEndDatesFrom(firstOccupy, financialYearEnd)
    logger.info(s"FirstOccupy: ${firstOccupy.toYearMonth}, FinEnd: ${financialYearEnd.toMonthDay} - ${endDates.toList}")

    endDates shouldBe expectedDates
