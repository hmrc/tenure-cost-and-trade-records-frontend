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

import form.MappingSupport.lettingPartOfPropertyAddressMapping
import models.submissions.aboutfranchisesorlettings.AdvertisingRightLetting
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object AdvertisingRightLettingForm {

  val advertisingRightLettingForm = Form(
    mapping(
      "descriptionOfSpace"     -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.descriptionOfSpace.required"),
        maxLength(500, "error.descriptionOfSpace.maxLength")
      ),
      "advertisingCompanyName" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.advertisingCompanyName.required"),
        maxLength(500, "error.advertisingCompanyName.maxLength")
      ),
      "correspondenceAddress"  -> optional(lettingPartOfPropertyAddressMapping)
        .verifying("error.correspondenceAddress.required", _.isDefined)
    )((descriptionOfSpace, advertisingCompanyName, correspondenceAddress) =>
      AdvertisingRightLetting(Some(descriptionOfSpace), Some(advertisingCompanyName), correspondenceAddress, None)
    )(advertisingRightLetting =>
      Some(
        (
          advertisingRightLetting.descriptionOfSpace.getOrElse(""),
          advertisingRightLetting.advertisingCompanyName.getOrElse(""),
          advertisingRightLetting.correspondenceAddress
        )
      )
    )
  )
}
