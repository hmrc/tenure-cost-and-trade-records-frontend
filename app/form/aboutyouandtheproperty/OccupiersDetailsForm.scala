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

package form.aboutyouandtheproperty

import models.submissions.aboutyouandtheproperty.OccupiersDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object OccupiersDetailsForm {

  val occupiersDetailsForm: Form[OccupiersDetails] = Form(
    mapping(
      "occupiersDetailsName"    -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.aboutYou.occupiersDetails.name.required"),
        maxLength(100, "error.aboutYou.occupiersDetails.name.maxLength")
      ),
      "occupiersDetailsAddress" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.aboutYou.occupiersDetails.address.required"),
        maxLength(1000, "error.aboutYou.occupiersDetails.address.maxLength")
      )
    )(OccupiersDetails.apply)(occupiersDetails => Option((occupiersDetails.name, occupiersDetails.address)))
  )
}
