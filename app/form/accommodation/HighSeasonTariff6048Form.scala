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
import form.DateMappings.{betweenDates, requiredDateMapping}
import form.lettingHistory.RentalPeriodForm.effectiveRentalPeriod
import models.submissions.accommodation.HighSeasonTariff
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import util.DateUtilLocalised

/**
  * @author Yuriy Tumakha
  */
object HighSeasonTariff6048Form extends RentalPeriodSupport:

  def highSeasonTariff6048Form(using
    request: SessionRequest[AnyContent],
    messages: Messages,
    dateUtil: DateUtilLocalised
  ): Form[HighSeasonTariff] =
    val finYearStart = effectiveRentalPeriod.fromDate
    val finYearEnd   = effectiveRentalPeriod.toDate

    Form {
      mapping(
        "fromDate" -> requiredDateMapping("accommodation.highSeasonTariff.fromDate", allowPastDates = true)
          .verifying(
            betweenDates(finYearStart, finYearEnd, "error.accommodation.highSeasonTariff.fromDate.range")
          ),
        "toDate"   -> requiredDateMapping("accommodation.highSeasonTariff.toDate", allowPastDates = true)
          .verifying(
            betweenDates(finYearStart, finYearEnd, "error.accommodation.highSeasonTariff.toDate.range")
          )
      )(HighSeasonTariff.apply)(o => Some(Tuple.fromProductTyped(o)))
        .verifying(
          "error.accommodation.highSeasonTariff.fromDate.mustBeBefore.toDate",
          highSeason => highSeason.fromDate.isBefore(highSeason.toDate)
        )
    }
