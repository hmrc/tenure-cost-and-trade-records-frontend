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

package form.aboutYourLeaseOrTenure

import form.DateMappings.monthYearMapping
import models.submissions.aboutYourLeaseOrTenure.CurrentLeaseOrAgreementBegin
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.nonEmpty
import play.api.i18n.Messages

object CurrentLeaseOrAgreementBeginForm {

  def currentLeaseOrAgreementBeginForm(implicit messages: Messages): Form[CurrentLeaseOrAgreementBegin] = Form(
    mapping(
      "leaseBegin" -> monthYearMapping("leaseBegin", "", allowPastDates = true),
      "grantedFor" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.grantedFor.required")
      )
    )(CurrentLeaseOrAgreementBegin.apply)(CurrentLeaseOrAgreementBegin.unapply)
  )

}
