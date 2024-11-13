/*
 * Copyright 2024 HM Revenue & Customs
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
import form.DateMappings.requiredDateMapping
import models.submissions.lettingHistory.LocalPeriod
import play.api.data.Forms.text
import play.api.data.validation.Constraints.nonEmpty
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import util.DateUtilLocalised

object FieldMappings:

  def nonEmptyText(errorMessage: String) =
    text.verifying(nonEmpty(errorMessage = errorMessage))

  def constrainedLocalDate(field: String, period: LocalPeriod)(using
    request: SessionRequest[AnyContent],
    messages: Messages,
    dateUtil: DateUtilLocalised
  ) =
    val mapping = requiredDateMapping(fieldNameKey = s"lettingHistory.$field", allowPastDates = true)
    if field == "fromDate" then
      mapping.verifying(
        error = messages(s"lettingHistory.rentalPeriod.$field.error", dateUtil.formatDate(period.fromDate)),
        constraint = { d => d.isAfter(period.fromDate) || d.isEqual(period.fromDate) }
      )
    else if field == "toDate" then
      mapping.verifying(
        error = messages(s"lettingHistory.rentalPeriod.$field.error", dateUtil.formatDate(period.toDate)),
        constraint = { d => d.isBefore(period.toDate) || d.isEqual(period.toDate) }
      )
    else /* unconstrained */ mapping
