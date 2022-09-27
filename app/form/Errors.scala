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

object Errors {

  //Login
  val invalidRefNum = "error.invalid_refnum"
  val invalidPostcode = "error.invalid_postcode"
  val invalidPostcodeOnLetter = "error.invalid_postcode_as_on_letter"

  //About You Errors
  val userTypeRequired = "error.userType.required"
  val contactDetailsMissing = "error.contact.details.missing"
  val contactPhoneRequired = "error.contact.phone.required"
  val contactEmailRequired = "error.contact.email.required"
  val alternativeAddressMissing = "error.alternative.address.missing"
  val alternativeContactMissing  = "error.alternative.contact.missing"

  //generic errors
  val required = "error.required"
  val booleanMissing = "error.boolean_missing"
  val noValueSelected = "error.no_value_selected"
  val maxLength = "error.maxLength"
  val invalidPhone = "error.invalid_phone"
  val invalidCurrency = "error.invalid_currency"
  val password = "error.invalid_password"


}