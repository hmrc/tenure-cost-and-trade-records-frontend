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

package form.aboutfranchisesorlettings

import form.DateMappings.requiredDateMapping
import form.MappingSupport.currencyMapping
import models.submissions.aboutfranchisesorlettings.RentDetails
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

object RentDetailsForm {

  def rentDetailsForm(implicit messages: Messages): Form[RentDetails] =
    Form(
      mapping(
        "annualRent" -> currencyMapping(".annualRent"),
        "dateInput"  -> requiredDateMapping("lettingPartOfPropertyFixedRentDate", allowPastDates = true)
      )(RentDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

}
