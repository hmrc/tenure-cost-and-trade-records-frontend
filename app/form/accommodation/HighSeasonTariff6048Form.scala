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

package form.accommodation

import actions.SessionRequest
import controllers.lettingHistory.RentalPeriodSupport
import form.DateMappings.{betweenDates, dayMonthMapping, requiredDateMapping}
import models.submissions.Form6010.DayMonthsDuration
import models.submissions.accommodation.HighSeasonTariff
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import util.AccountingInformationUtil.previousFinancialYear6048
import util.DateUtilLocalised

import java.time.{LocalDate, Year}
import java.time.Month.{APRIL, MARCH}

/**
  * @author Yuriy Tumakha
  */
object HighSeasonTariff6048Form extends RentalPeriodSupport:

  private def previousFinancialYearStart6048: LocalDate = LocalDate.of(previousFinancialYear6048 - 1, APRIL, 1)

  private def previousFinancialYearEnd6048: LocalDate = LocalDate.of(previousFinancialYear6048, MARCH, 31)

  
  private def toLocalDate(dm: DayMonthsDuration): LocalDate = {
    previousFinancialYearStart6048
  }

  private def fromLocalDate(date: LocalDate): LocalDate =
    previousFinancialYearEnd6048

  def highSeasonTariff6048Form(using
    request: SessionRequest[AnyContent],
    messages: Messages,
    dateUtil: DateUtilLocalised
  ): Form[HighSeasonTariff] =
    
    Form {
      mapping(
        "fromDate" -> dayMonthMapping("fromDate").transform[LocalDate](toLocalDate, fromLocalDate),
        "toDate"   -> dayMonthMapping("toDate").transform[LocalDate](toLocalDate, fromLocalDate)

      )(HighSeasonTariff.apply)(o => Some(Tuple.fromProductTyped(o)))
        .verifying(
          "error.accommodation.highSeasonTariff.fromDate.mustBeBefore.toDate",
          highSeason => highSeason.fromDate.isBefore(highSeason.toDate)
        )
    }
