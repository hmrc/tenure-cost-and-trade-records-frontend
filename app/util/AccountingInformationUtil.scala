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

import models.submissions.aboutthetradinghistory.OccupationalAndAccountingInformation

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object AccountingInformationUtil {

  def financialYearsRequired(accountingInfo: OccupationalAndAccountingInformation): Seq[LocalDate] = {
    val now     = LocalDate.now
    val yearNow = now.getYear

    val firstOccupyYear       = accountingInfo.firstOccupy.years
    val financialYearEndDay   = accountingInfo.financialYear.days
    val financialYearEndMonth = accountingInfo.financialYear.months

    val currentFinancialYear =
      if (now isBefore LocalDate.of(yearNow, financialYearEndMonth, financialYearEndDay)) {
        yearNow
      } else {
        yearNow + 1
      }

    val yearDifference = currentFinancialYear - firstOccupyYear

    (1 to (yearDifference min 3)).map { yearsAgo =>
      LocalDate.of(
        currentFinancialYear - yearsAgo,
        financialYearEndMonth,
        financialYearEndDay
      )
    }
  }

}
