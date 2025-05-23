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

import models.submissions.aboutfranchisesorlettings.TelecomMastLetting
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object TelecomMastLettingForm:

  val theForm = Form[TelecomMastLetting](
    mapping(
      "operatingCompanyName" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.operatingCompanyName.required"),
        maxLength(500, "error.operatingCompanyName.maxLength")
      ),
      "siteOfMast"           -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.siteOfMast.required"),
        maxLength(500, "error.siteOfMast.maxLength")
      )
    )((operatingCompanyName, siteOfMast) =>
      TelecomMastLetting(Some(operatingCompanyName), Some(siteOfMast), None, None)
    )(telecomMastLetting =>
      Some(
        (
          telecomMastLetting.operatingCompanyName.getOrElse(""),
          telecomMastLetting.siteOfMast.getOrElse("")
        )
      )
    )
  )
