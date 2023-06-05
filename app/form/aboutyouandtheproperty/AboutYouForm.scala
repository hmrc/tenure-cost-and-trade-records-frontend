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

package form.aboutyouandtheproperty

import form.Errors
import form.MappingSupport.contactDetailsMapping
import models.submissions.aboutyouandtheproperty.CustomerDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, nonEmptyText, text}
import play.api.data.validation.Constraints.nonEmpty

object AboutYouForm {

  val aboutYouForm: Form[CustomerDetails] = Form(
    mapping(
      "fullName"       -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.fullName.required")
      ),
      "contactDetails" -> contactDetailsMapping
    )(CustomerDetails.apply)(CustomerDetails.unapply)
  )
}
