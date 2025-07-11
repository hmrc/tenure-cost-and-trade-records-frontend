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

package form.aboutYourLeaseOrTenure

import form.DateMappings.requiredDateMapping
import form.Errors
import form.MappingSupport.createYesNoType
import models.submissions.aboutYourLeaseOrTenure.CurrentRentPayableWithin12Months
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import play.api.i18n.Messages

object CurrentRentPayableWithin12MonthsForm:

  def currentRentPayableWithin12MonthsForm(using messages: Messages): Form[CurrentRentPayableWithin12Months] =
    Form(
      mapping(
        "rentPayable" -> createYesNoType(Errors.currentRentPayableWithin12Months),
        "dateReview"  -> optional(
          requiredDateMapping("dateReview", allowFutureDates = true)
        )
      )(CurrentRentPayableWithin12Months.apply)(o => Some(Tuple.fromProductTyped(o)))
    )
