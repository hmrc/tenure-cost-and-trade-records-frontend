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

package controllers.requestReferenceNumber

import form.Errors
import form.requestReferenceNumber.RequestReferenceNumberContactDetailsForm.requestReferenceNumberContactDetailsForm
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class NoReferenceNumberContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  private val postRequest = FakeRequest("POST", "/")

  def requestReferenceNumberContactDetailsController(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA)
  ) = new RequestReferenceNumberContactDetailsController(
    stubMessagesControllerComponents(),
    requestReferenceNumberNavigator,
    requestReferenceNumberContactDetailsView,
    preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
    mockSessionRepo
  )

  def requestReferenceNumberContactDetailsControllerBlank(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumBlank)
  ) = new RequestReferenceNumberContactDetailsController(
    stubMessagesControllerComponents(),
    requestReferenceNumberNavigator,
    requestReferenceNumberContactDetailsView,
    preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = requestReferenceNumberContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = requestReferenceNumberContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 with empty session" in {
      val result = requestReferenceNumberContactDetailsControllerBlank().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = requestReferenceNumberContactDetailsController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "return redirect to next page for completed form SEE_OTHER" in {
      val result = requestReferenceNumberContactDetailsController().submit()(
        postRequest.withFormUrlEncodedBody(
          "requestReferenceNumberContactDetailsFullName"              -> "Contact Name",
          "requestReferenceNumberContactDetails.phone"                -> "12345678902",
          "requestReferenceNumberContactDetails.email"                -> "TestEmail@gmail.com",
          "requestReferenceNumberContactDetailsAdditionalInformation" -> "Additional information"
        )
      )
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.requestReferenceNumber.routes.CheckYourAnswersRequestReferenceNumberController.show().url
      )
    }
  }

  "no Reference Number Contact Details form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.fullName, "error.requestReferenceNumberContactDetailsFullName.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData + (errorKey.email -> "")
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }

    "error if email is invalid format" in {
      val formData = baseFormData + (errorKey.email -> "invalid_email_address")
      val form     = requestReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.emailFormat, form)
    }
  }

  object TestData {
    val errorKey = new {
      val fullName = "requestReferenceNumberContactDetailsFullName"
      val phone    = "requestReferenceNumberContactDetails.phone"
      val email    = "requestReferenceNumberContactDetails.email"
    }

    val baseFormData: Map[String, String] = Map(
      "requestReferenceNumberContactDetailsFullName" -> "John Smith",
      "requestReferenceNumberContactDetails.phone"   -> "01234 123123",
      "requestReferenceNumberContactDetails.email1"  -> "blah.blah@test.com"
    )
  }
}
