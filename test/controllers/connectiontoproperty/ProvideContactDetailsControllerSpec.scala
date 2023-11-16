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

package controllers.connectiontoproperty

import form.Errors
import models.submissions.connectiontoproperty.StillConnectedDetails
import form.connectiontoproperty.ProvideContactDetailsForm.provideContactDetailsForm
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class ProvideContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def provideContactDetailsController(
    provideContactDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = provideContactDetails),
    mockSessionRepo
  )

  "Provide contact details controller" should {
    "return 200" in {
      val result = provideContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = provideContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = provideContactDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Provide contact details form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.fullName, "error.fullName.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData - errorKey.email
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val fullName: String
      val phone: String
      val email: String
      val email1TooLong: String
    } = new {
      val fullName: String = "yourContactDetails.fullName"
      val phone            = "yourContactDetails.contactDetails.phone"
      val email            = "yourContactDetails.contactDetails.email"
      val email1TooLong    = "yourContactDetails.contactDetails.email.email.tooLong"
    }

    val tooLongEmail                      = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"  -> "12345678901",
      "contactDetails.phone"  -> "01234 123123",
      "contactDetails.email1" -> "blah.blah@test.com",
      "fullName"              -> "Mr John Smith"
    )

  }
}
