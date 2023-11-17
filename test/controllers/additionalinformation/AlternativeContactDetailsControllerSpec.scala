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

package controllers.additionalinformation

import form.Errors
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec
import form.additionalinformation.AlternativeContactDetailsForm.alternativeContactDetailsForm
import models.submissions.additionalinformation.AdditionalInformation
import play.api.test.FakeRequest

class AlternativeContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def alternativeContactDetailsController(
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation)
  ) = new AlternativeContactDetailsController(
    stubMessagesControllerComponents(),
    additionalInformationNavigator,
    alternativeContactDetailsView,
    preEnrichedActionRefiner(additionalInformation = additionalInformation),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = alternativeContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = alternativeContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = alternativeContactDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(result) shouldBe BAD_REQUEST
    }
  }

  "Additional information form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.fullName, "error.fullName.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData + (errorKey.email -> "")
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }

    "error if email is invalid format" in {
      val formData = baseFormData + (errorKey.email -> "invalid_email_address")
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.emailFormat, form)
    }

    "error if buildingNameNumber is missing" in {
      val formData = baseFormData - errorKey.buildingNameNumber
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.buildingNameNumber, "error.buildingNameNumber.required", form)
    }

    "error if town or city is missing" in {
      val formData = baseFormData - errorKey.town
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.town, "error.townCity.required", form)
    }

    "error if postcode is missing" in {
      val formData = baseFormData - errorKey.postcode
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.postcode, "error.postcode.required", form)
    }

  }

  object TestData {
    val errorKey = new {
      val fullName: String   = "alternativeContactFullName"
      val phone              = "alternativeContactDetails.phone"
      val email              = "alternativeContactDetails.email"
      val email1TooLong      = "contactDetails.email.email.tooLong"
      val buildingNameNumber = "alternativeContactAddress.buildingNameNumber"
      val town               = "alternativeContactAddress.town"
      val postcode           = "alternativeContactAddress.postcode"
    }

    val tooLongEmail                      = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"                         -> "12345678901",
      "contactDetails.phone"                         -> "01234 123123",
      "contactDetails.email1"                        -> "blah.blah@test.com",
      "alternativeContactFullName"                   -> "Mr John Smith",
      "alternativeContactAddress.buildingNameNumber" -> "001",
      "alternativeContactAddress.town"               -> "Manchester",
      "alternativeContactAddress.postcode"           -> "BN12 4AX"
    )
  }

}
