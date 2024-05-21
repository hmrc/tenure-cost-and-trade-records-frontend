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

import actions.SessionRequest
import models.Session
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory._
import play.api.mvc.AnyContent

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object AccountingInformationUtil {

  def financialYearsRequired(firstOccupy: MonthsYearDuration, financialYear: DayMonthsDuration): Seq[LocalDate] = {
    val now     = LocalDate.now
    val yearNow = now.getYear

    val firstOccupyYear       = firstOccupy.years
    val financialYearEndDay   = financialYear.days
    val financialYearEndMonth = financialYear.months

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

  def previousFinancialYears(implicit request: SessionRequest[AnyContent]): Seq[Int] =
    request.sessionData.aboutTheTradingHistory
      .fold(Seq.empty[Int])(_.turnoverSections.map(_.financialYearEnd.getYear))

  def newFinancialYears(occupationAndAccounting: OccupationalAndAccountingInformation): Seq[Int] =
    occupationAndAccounting.financialYear
      .fold(Seq.empty[Int])(financialYearsRequired(occupationAndAccounting.firstOccupy, _).map(_.getYear))

  def buildUpdatedData6076(
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean
  )(implicit request: SessionRequest[AnyContent]): Session = {

    val firstOccupy                   = newOccupationAndAccounting.firstOccupy
    val financialYear                 = newOccupationAndAccounting.financialYear.get
    val originalTurnoverSections6076  =
      request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6076).getOrElse(Seq.empty)
    val isFinancialYearsListUnchanged = newFinancialYears(newOccupationAndAccounting) == previousFinancialYears

    val turnoverSections6076 =
      if (isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged) {
        originalTurnoverSections6076
      } else if (isFinancialYearsListUnchanged) {
        (originalTurnoverSections6076 zip financialYearsRequired(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      } else {
        financialYearsRequired(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6076(financialYearEnd = finYearEnd, tradingPeriod = 52)
        }
      }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        checkYourAnswersAboutTheTradingHistory = sectionCompleted(isFinancialYearsListUnchanged, aboutTheTradingHistory)
      )
    )
    updatedData.copy(
      aboutTheTradingHistoryPartOne = Some(
        updatedData.aboutTheTradingHistoryPartOne
          .getOrElse(AboutTheTradingHistoryPartOne())
          .copy(turnoverSections6076 = Some(turnoverSections6076))
      )
    )
  }

  private def sectionCompleted(
    isFinancialYearsListUnchanged: Boolean,
    aboutTheTradingHistory: AboutTheTradingHistory
  ): Option[CheckYourAnswersAboutTheTradingHistory] =
    if (isFinancialYearsListUnchanged) {
      aboutTheTradingHistory.checkYourAnswersAboutTheTradingHistory
    } else {
      None
    }

}
