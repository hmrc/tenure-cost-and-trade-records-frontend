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

object TradingSeasonForm extends Object with RentalPeriodSupport:

  def theForm(using request: SessionRequest[AnyContent], messages: Messages, dateUtil: DateUtilLocalised) =
    Form[LocalPeriod](
      mapping(
        "fromDate" -> constrainedLocalDate(
          "lettingHistory.intendedLettings.tradingSeason",
          "fromDate",
          previousRentalPeriod
        ),
        "toDate"   -> constrainedLocalDate(
          "lettingHistory.intendedLettings.tradingSeason",
          "toDate",
          previousRentalPeriod
        )
      )(LocalPeriod.apply)(LocalPeriod.unapply)
        .verifying(
          error = messages("lettingHistory.intendedLettings.tradingSeason.error"),
          constraint = period => period.fromDate.isBefore(period.toDate) || period.fromDate.isEqual(period.toDate)
        )
    )
