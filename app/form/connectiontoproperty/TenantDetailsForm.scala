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

package form.connectiontoproperty

import form.MappingSupport.correspondenceAddressMapping
import models.submissions.connectiontoproperty.TenantDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object TenantDetailsForm {

  val tenantDetailsForm: Form[TenantDetails] = Form(
    mapping(
      "tenantName"            -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.tenantName.required"),
        maxLength(100, "error.tenantName.maxLength")
      ),
      "descriptionOfLetting"  -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.descriptionOfLetting.required"),
        maxLength(100, "error.descriptionOfLetting.maxLength")
      ),
      "correspondenceAddress" -> correspondenceAddressMapping
    )(TenantDetails.apply)(TenantDetails.unapply)
  )
}