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

import form.MappingSupport.cateringAddressMapping
import models.submissions.aboutfranchisesorlettings.BusinessDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object FranchiseTypeDetailsForm {

  val theForm = Form[BusinessDetails](
    mapping(
      "operatorName"   -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.operatorName.required"),
        maxLength(50, "error.operatorName.maxLength")
      ),
      "typeOfBusiness" -> default(text, "").verifying(
        nonEmpty(errorMessage = "error.typeOfBusiness.required"),
        maxLength(50, "error.typeOfBusiness.maxLength")
      )
    )((operatorName, typeOfBusiness) => BusinessDetails(operatorName, typeOfBusiness, None)) { obj =>
      Some((obj.operatorName, obj.typeOfBusiness))
    }
  )
}
