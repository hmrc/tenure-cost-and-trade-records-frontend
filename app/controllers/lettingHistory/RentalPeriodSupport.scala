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

package controllers.lettingHistory

import actions.SessionRequest
import models.submissions.lettingHistory.LocalPeriod
import play.api.mvc.AnyContent

import java.time.LocalDate
import java.time.Month.{APRIL, MARCH}

trait RentalPeriodSupport extends FiscalYearSupport:

  def previousRentalPeriod(using request: SessionRequest[AnyContent]) =
    LocalPeriod(
      fromDate = LocalDate.of(previousFiscalYearEnd - numberOfYearsBack, APRIL, 1),
      toDate = LocalDate.of(previousFiscalYearEnd, MARCH, 31)
    )

  def currentRentalPeriod(using request: SessionRequest[AnyContent]) =
    LocalPeriod(
      fromDate = LocalDate.of(currentFiscalYearEnd - numberOfYearsBack, APRIL, 1),
      toDate = LocalDate.of(currentFiscalYearEnd, MARCH, 31)
    )

  def effectiveRentalPeriod(using request: SessionRequest[AnyContent]): LocalPeriod =
    val commercialLetFirstAvailableDate = request.sessionData.aboutYouAndThePropertyPartTwo
      .flatMap(_.commercialLetDate)
      .fold(LocalDate.EPOCH)(_.toYearMonth.atDay(1))

    if !commercialLetFirstAvailableDate.isAfter(previousRentalPeriod.fromDate) then previousRentalPeriod
    else previousRentalPeriod.copy(fromDate = commercialLetFirstAvailableDate)

  // The Welsh journey requires 3 years of data instead of 1 year
  private def numberOfYearsBack(using request: SessionRequest[AnyContent]) =
    val numberOfYearsBack = if request.sessionData.isWelsh then 3 else 1
    numberOfYearsBack
