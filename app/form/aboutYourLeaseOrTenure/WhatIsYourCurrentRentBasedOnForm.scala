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

import form.Errors
import form.MappingSupport.whatIsYourRentBasedOnType
import models.submissions.aboutYourLeaseOrTenure.WhatIsYourCurrentRentBasedOnDetails
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.validation.Constraints.maxLength

object WhatIsYourCurrentRentBasedOnForm {

  val whatIsYourCurrentRentBasedOnForm = Form(
    mapping(
      "currentRentBasedOn"    -> whatIsYourRentBasedOnType,
      "whatIsYourRentBasedOn" -> optional(
        text.verifying(
          maxLength(2000, Errors.maxLength)
        )
      )
    )(WhatIsYourCurrentRentBasedOnDetails.apply)(WhatIsYourCurrentRentBasedOnDetails.unapply)
  )
}
