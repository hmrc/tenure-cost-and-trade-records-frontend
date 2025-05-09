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

package form.lettingHistory

import actions.SessionRequest
import controllers.lettingHistory.RentalPeriodSupport
import form.lettingHistory.FieldMappings.constrainedLocalDate
import models.submissions.lettingHistory.LocalPeriod
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import util.DateUtilLocalised

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object RentalPeriodForm extends RentalPeriodSupport:

  def theForm(using
    request: SessionRequest[AnyContent],
    messages: Messages,
    dateUtil: DateUtilLocalised
  ): Form[LocalPeriod] =
    Form[LocalPeriod](
      mapping(
        "fromDate" -> constrainedLocalDate("lettingHistory.rentalPeriod", "fromDate", effectiveRentalPeriod),
        "toDate"   -> constrainedLocalDate("lettingHistory.rentalPeriod", "toDate", effectiveRentalPeriod)
      )(LocalPeriod.apply)(LocalPeriod.unapply)
        .verifying(
          "lettingHistory.rentalPeriod.error",
          period => period.fromDate.isBefore(period.toDate)
        )
        .transform[LocalPeriod](identity, identity) // Check the next constraint only after passing previous
        .verifying(
          "error.lettingHistory.rentalPeriod.less29nights",
          period => ChronoUnit.DAYS.between(period.fromDate, period.toDate) >= 29
        )
    )
