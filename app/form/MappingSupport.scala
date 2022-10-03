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

import form.ConditionalMapping.nonEmptyTextOr
import models.submissions._
import form.Formats._
import form.Formats.userTypeFormat
import play.api.data.Forms.{default, email, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, minLength, nonEmpty, pattern}
import play.api.data.{Forms, Mapping}


object MappingSupport {

  val postcodeRegex = """(GIR ?0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) ?[0-9][A-Z-[CIKMOV]]{2})""" //scalastyle:ignore
  val phoneRegex = """^^[0-9\s\+()-]+$"""
  val userType: Mapping[UserType] = Forms.of[UserType]
  val aboutYourPropertyType: Mapping[CurrentPropertyUsed] = Forms.of[CurrentPropertyUsed]
  val addressConnectionType: Mapping[AddressConnectionType] = Forms.of[AddressConnectionType]
  val postcode: Mapping[String] = PostcodeMapping.postcode()

  val contactDetailsMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> default(text, "").verifying(
        nonEmpty(errorMessage = Errors.contactPhoneRequired),
        pattern(phoneRegex.r, error = Errors.invalidPhone),
        minLength(11, "error.contact.phone.minLength"),
        maxLength(20, "error.contact.phone.maxLength")
      ),
      "email1" -> default(email, "").verifying(
        nonEmpty(errorMessage = Errors.contactEmailRequired),
        maxLength(50, "contactDetails.email1.email.tooLong")
      )
    )(ContactDetails.apply)(ContactDetails.unapply)

  def addressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1" -> optional(text(maxLength = 50)),
    "street2" -> optional(text(maxLength = 50)),
    "postcode" ->  nonEmptyTextOr("postcode", postcode, "error.postcode.required")
  )(Address.apply)(Address.unapply)




}