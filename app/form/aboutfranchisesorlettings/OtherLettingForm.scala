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

package form.aboutfranchisesorlettings

import models.submissions.aboutfranchisesorlettings.OtherLetting
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object OtherLettingForm {

  val theForm = Form(
    mapping(
      "lettingType" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.lettingType.required"),
        maxLength(50, "error.lettingType.maxLength")
      ),
      "tenantName"  -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.tenantName.required"),
        maxLength(50, "error.otherLetting.tenantName.maxLength")
      )
    )((lettingType, tenantName) => OtherLetting(Some(lettingType), Some(tenantName), None, None))(otherLetting =>
      Some(
        (
          otherLetting.lettingType.getOrElse(""),
          otherLetting.tenantName.getOrElse("")
        )
      )
    )
  )
}
