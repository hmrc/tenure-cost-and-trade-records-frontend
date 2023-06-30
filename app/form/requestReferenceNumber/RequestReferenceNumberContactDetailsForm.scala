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

package form.requestReferenceNumber

import form.MappingSupport.contactDetailsMapping
import models.submissions.requestReferenceNumber.RequestReferenceNumberContactDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object RequestReferenceNumberContactDetailsForm {
  val requestReferenceNumberContactDetailsForm: Form[RequestReferenceNumberContactDetails] = Form(
    mapping(
      "requestReferenceNumberContactDetailsFullName"              -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.requestReferenceNumberContactDetailsFullName.required")
      ),
      "requestReferenceNumberContactDetails"                      -> contactDetailsMapping,
      "requestReferenceNumberContactDetailsAdditionalInformation" ->
        optional(
          default(text, "").verifying(
            maxLength(2000, "error.char.count.maxLength")
          )
        )
    )(RequestReferenceNumberContactDetails.apply)(RequestReferenceNumberContactDetails.unapply)
  )
}
