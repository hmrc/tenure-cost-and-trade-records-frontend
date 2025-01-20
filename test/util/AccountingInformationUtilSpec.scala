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

  private val today                   = LocalDate.now
  private val endOfMarch              = LocalDate.of(yearNow, 3, 31)
  private val financialYearEndOfMarch = DayMonthsDuration(31, 3)
  private def yearNow                 = YearMonth.now.getYear
  private def monthNow                = YearMonth.now.getMonthValue

  "AccountingInformationUtil.financialYearsRequired" should "return 3 dates" in {
    val firstOccupy      = MonthsYearDuration(monthNow, yearNow - 4)
    val financialYearEnd = DayMonthsDuration(1, monthNow)

    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 3)
  }

  it should "return 1 date" in {
    val firstOccupy      = MonthsYearDuration(monthNow, yearNow)
    val financialYearEnd = DayMonthsDuration(1, monthNow)

    verifyFinancialYearEndDates(firstOccupy, financialYearEnd, 1)
  }

  it should "return 2/3 dates (6048)" in {
    val firstOccupy = MonthsYearDuration(monthNow, yearNow - 2)

    verifyFinancialYearEndDates(firstOccupy, financialYearEndOfMarch, if endOfMarch.isAfter(today) then 2 else 3)
  }

  it should "return 1/2 dates (6048)" in {
    val firstOccupy = MonthsYearDuration(monthNow, yearNow - 1)

    verifyFinancialYearEndDates(firstOccupy, financialYearEndOfMarch, if endOfMarch.isAfter(today) then 1 else 2)
  }

  it should "return 0/1 date (6048)" in {
    val firstOccupy = MonthsYearDuration(monthNow, yearNow)

    verifyFinancialYearEndDates(firstOccupy, financialYearEndOfMarch, if endOfMarch.isAfter(today) then 0 else 1)
  }

  it should "return 0 dates (6048)" in {
    val firstOccupy = MonthsYearDuration(monthNow, yearNow + (if endOfMarch.isAfter(today) then 0 else 1))

    verifyFinancialYearEndDates(firstOccupy, financialYearEndOfMarch, 0)
  }

  private def getFirstYearEnd(financialYearEnd: MonthDay): LocalDate =
    val firstYearEnd = LocalDate.of(yearNow, financialYearEnd.getMonthValue, financialYearEnd.getDayOfMonth)
    if !today.isBefore(firstYearEnd) then firstYearEnd else firstYearEnd.minusYears(1)

  private def verifyFinancialYearEndDates(
    firstOccupy: MonthsYearDuration,
    financialYearEnd: DayMonthsDuration,
    expectedColumns: Int
  ): Unit =
    val firstYearEnd  = getFirstYearEnd(financialYearEnd.toMonthDay)
    val expectedDates = (1 to expectedColumns).map(y => firstYearEnd.minusYears(y - 1))

    val endDates = AccountingInformationUtil.financialYearsRequired(firstOccupy, financialYearEnd)
    logger.info(s"FirstOccupy: ${firstOccupy.toYearMonth}, FinEnd: ${financialYearEnd.toMonthDay} - ${endDates.toList}")

    endDates shouldBe expectedDates
