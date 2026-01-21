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

import actions.SessionRequest
import controllers.aboutthetradinghistory
import models.Session
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import models.submissions.aboutthetradinghistory.*
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.common.AnswersYesNo
import navigation.AboutTheTradingHistoryNavigator
import play.api.mvc.AnyContent

import java.time.Month.{APRIL, MARCH}
import java.time.temporal.ChronoUnit
import java.time.{LocalDate, YearMonth}

/**
  * @author Yuriy Tumakha
  */
object AccountingInformationUtil {

  private def yearNow                 = YearMonth.now.getYear
  private def monthNow                = YearMonth.now.getMonthValue
  private val financialYearEndOfMarch = DayMonthsDuration(31, 3)
  private val defaultStart            = MonthsYearDuration(monthNow, yearNow)

  def previousFinancialYear6048: Int =
    if monthNow > 3 then yearNow
    else yearNow - 1

  private def previousFinancialYearStart6048: LocalDate = LocalDate.of(previousFinancialYear6048 - 1, APRIL, 1)
  private def previousFinancialYearEnd6048: LocalDate   = LocalDate.of(previousFinancialYear6048, MARCH, 31)

  def previousFinancialYearFromTo6048: Seq[LocalDate] =
    Seq(previousFinancialYearStart6048, previousFinancialYearEnd6048)

  def financialYearsRequiredAccommodation6048(
    firstOccupy: Option[MonthsYearDuration],
    isWales: Boolean
  ): Seq[LocalDate] =
    if isWales then
      Some(deriveFinancialYearEndDatesFrom(firstOccupy.getOrElse(defaultStart), financialYearEndOfMarch))
        .filter(_.nonEmpty)
        .getOrElse(Seq(previousFinancialYearEnd6048))
    else Seq(previousFinancialYearEnd6048)

  def deriveFinancialYearEndDatesFrom(
    firstOccupy: MonthsYearDuration,
    financialYear: DayMonthsDuration
  ): Seq[LocalDate] = {
    val now     = LocalDate.now
    val yearNow = now.getYear

    val financialYearEndDate = LocalDate.of(yearNow, financialYear.months, financialYear.days)

    val lastCompleteFinancialYear =
      if (!now.isAfter(financialYearEndDate)) yearNow - 1
      else yearNow

    if (firstOccupy.years >= lastCompleteFinancialYear) Seq.empty
    else {
      val firstFinancialYearEnd =
        if (firstOccupy.months > financialYear.months || (firstOccupy.months == financialYear.months && 1 > financialYear.days))
          firstOccupy.years + 1
        else
          firstOccupy.years

      val yearsSinceFirstOccupy = (lastCompleteFinancialYear - firstFinancialYearEnd + 1) max 0
      val maxYears              = yearsSinceFirstOccupy min 3

      (0 until maxYears).map { i =>
        LocalDate.of(lastCompleteFinancialYear - i, financialYear.months, financialYear.days)
      }.filterNot(_.isBefore(LocalDate.of(firstOccupy.years, firstOccupy.months, 1)))
    }
  }

  def maxNightsInFinYear6048(year: Int)(using request: SessionRequest[AnyContent]): Int =
    val commercialLetFirstAvailableDate = request.sessionData.aboutYouAndThePropertyPartTwo
      .flatMap(_.commercialLetDate)
      .fold(LocalDate.EPOCH)(_.toYearMonth.atDay(1))

    val daysSinceStart =
      ChronoUnit.DAYS.between(commercialLetFirstAvailableDate, LocalDate.of(year, MARCH, 31)).toInt + 1
    if daysSinceStart > 0 && daysSinceStart < 365 then daysSinceStart else 365

  def maxWeeksInFinYear6048(year: Int)(using request: SessionRequest[AnyContent]): Int =
    val commercialLetFirstAvailableDate = request.sessionData.aboutYouAndThePropertyPartTwo
      .flatMap(_.commercialLetDate)
      .fold(LocalDate.EPOCH)(_.toYearMonth.atDay(1))

    val weeksSinceStart =
      ChronoUnit.WEEKS.between(commercialLetFirstAvailableDate, LocalDate.of(year, MARCH, 31)).toInt + 1
    if weeksSinceStart > 0 && weeksSinceStart < 52 then weeksSinceStart else 52

  def previousFinancialYears(using request: SessionRequest[AnyContent]): Seq[Int] =
    request.sessionData.aboutTheTradingHistory
      .fold(Seq.empty[Int])(_.turnoverSections.map(_.financialYearEnd.getYear))

  def previousFinancialYears6045(using request: SessionRequest[AnyContent]): Seq[Int] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6045)
      .fold(Seq.empty[Int])(_.map(_.financialYearEnd.getYear))

  def previousFinancialYears6048(using request: SessionRequest[AnyContent]): Seq[Int] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6048)
      .fold(Seq.empty[Int])(_.map(_.financialYearEnd.getYear))

  def previousFinancialYears6076(using request: SessionRequest[AnyContent]): Seq[Int] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .fold(Seq.empty[Int])(_.map(_.financialYearEnd.getYear))

  def financialYearsList(occupationAndAccounting: OccupationalAndAccountingInformation): Seq[Int] =
    occupationAndAccounting.currentFinancialYearEnd
      .fold(Seq.empty[Int])(deriveFinancialYearEndDatesFrom(occupationAndAccounting.firstOccupy, _).map(_.getYear))

  def buildUpdatedData6045(
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean
  )(using request: SessionRequest[AnyContent]): Session = {

    val firstOccupy                   = newOccupationAndAccounting.firstOccupy
    val financialYear                 = newOccupationAndAccounting.currentFinancialYearEnd.get
    val originalTurnoverSections6045  =
      request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6045).getOrElse(Seq.empty)
    val isFinancialYearsListUnchanged = financialYearsList(newOccupationAndAccounting) == previousFinancialYears6045

    val turnoverSections6045 =
      if isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged then originalTurnoverSections6045
      else if isFinancialYearsListUnchanged then
        (originalTurnoverSections6045 zip deriveFinancialYearEndDatesFrom(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      else
        deriveFinancialYearEndDatesFrom(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6045(financialYearEnd = finYearEnd)
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
          .copy(
            turnoverSections6045 = Some(turnoverSections6045)
          )
      )
    )
  }

  def buildUpdatedData6048(
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean
  )(using request: SessionRequest[AnyContent]): Session = {

    val firstOccupy                   = newOccupationAndAccounting.firstOccupy
    val financialYear                 = newOccupationAndAccounting.currentFinancialYearEnd.get
    val originalTurnoverSections6048  =
      request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6048).getOrElse(Seq.empty)
    val isFinancialYearsListUnchanged = financialYearsList(newOccupationAndAccounting) == previousFinancialYears6048

    val turnoverSections6048 =
      if isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged then originalTurnoverSections6048
      else if isFinancialYearsListUnchanged then
        (originalTurnoverSections6048 zip deriveFinancialYearEndDatesFrom(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      else
        deriveFinancialYearEndDatesFrom(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6048(financialYearEnd = finYearEnd)
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
          .copy(
            turnoverSections6048 = Some(turnoverSections6048)
          )
      )
    )
  }

  def buildUpdatedData6076(
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean
  )(using request: SessionRequest[AnyContent]): Session = {

    val firstOccupy                   = newOccupationAndAccounting.firstOccupy
    val financialYear                 = newOccupationAndAccounting.currentFinancialYearEnd.get
    val originalTurnoverSections6076  =
      request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6076).getOrElse(Seq.empty)
    val isFinancialYearsListUnchanged = financialYearsList(newOccupationAndAccounting) == previousFinancialYears6076

    val turnoverSections6076 =
      if isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged then originalTurnoverSections6076
      else if isFinancialYearsListUnchanged then
        (originalTurnoverSections6076 zip deriveFinancialYearEndDatesFrom(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      else
        deriveFinancialYearEndDatesFrom(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6076(financialYearEnd = finYearEnd)
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
          .copy(
            turnoverSections6076 = Some(turnoverSections6076)
          )
      )
    )
  }

  private def sectionCompleted(
    isFinancialYearsListUnchanged: Boolean,
    aboutTheTradingHistory: AboutTheTradingHistory
  ): Option[AnswersYesNo] =
    if isFinancialYearsListUnchanged then aboutTheTradingHistory.checkYourAnswersAboutTheTradingHistory
    else None

  def backLinkToFinancialYearEndDates(
    navigator: AboutTheTradingHistoryNavigator
  )(using request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        navigator.cyaPage
          .getOrElse(aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show())
          .url
      case _     =>
        request.sessionData.aboutTheTradingHistory
          .flatMap(_.occupationAndAccountingInformation)
          .flatMap(_.financialYearEndHasChanged) match {
          case Some(true) => aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show().url
          case _          => aboutthetradinghistory.routes.FinancialYearEndController.show().url
        }
    }

}
