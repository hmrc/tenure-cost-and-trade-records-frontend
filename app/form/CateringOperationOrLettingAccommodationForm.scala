/*
 * Copyright 2022 HM Revenue & Customs
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

package form

import form.DateMappings.dateFieldsMapping
import form.MappingSupport._
import models.submissions.CateringOperationOrLettingAccommodationDetails
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}

object CateringOperationOrLettingAccommodationForm {

  val cateringOperationOrLettingAccommodationForm = Form(
    mapping(
      "operatorName" -> nonEmptyText(maxLength = 100),
      "typeOfBusiness" -> nonEmptyText(maxLength = 100),
      "cateringAddress" -> cateringAddressMapping,
      "annualRent" -> currencyMapping(".annualRent"),
      "dateInput" -> dateFieldsMapping("dateInput", fieldErrorPart = ".dateInput"),

    )(CateringOperationOrLettingAccommodationDetails.apply)(CateringOperationOrLettingAccommodationDetails.unapply)
  )
}
