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

package controllers.notconnected

import form.Errors
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import models.submissions.notconnected.RemoveConnectionDetails
import play.api.data.FormError
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class RemoveConnectionControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def removeConnectionController(
    removeConnectionDetails: Option[RemoveConnectionDetails] = Some(prefilledNotConnectedYes)
  ) = new RemoveConnectionController(
    stubMessagesControllerComponents(),
    removeConnectionNavigator,
    removeConnectionView,
    preEnrichedActionRefiner(removeConnectionDetails = removeConnectionDetails),
    mockSessionRepo
  )

  "Remove connection controller" should {
    "return 200" in {
      val result = removeConnectionController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = removeConnectionController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 for empty session" in {
      val controller = removeConnectionController(removeConnectionDetails = None)
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = removeConnectionController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data connectedToLandlord submitted" in {
        val res = removeConnectionController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "removeConnectionFullName" -> "Mr John Smith",
            "removeConnectionDetails.phone" -> "01234567890",
            "removeConnectionDetails.email" -> "blah.blah@test.com"
          )
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }
  "calculateBackLink"            should {
    "return back link to NotConnected CYA page when 'from=CYA' query param is present and user is not connected to the property" in {
      val result = removeConnectionController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url
      )
    }

    "return back link to past connection page if 'from' query param is not present" in {
      val result = removeConnectionController().show(fakeRequest)
      contentAsString(result) should include(controllers.notconnected.routes.PastConnectionController.show().url)
    }
  }

  "Remove connection form" should {

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData + (errorKey.email -> "")
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }

    "error if email is invalid format" in {
      val formData = baseFormData + (errorKey.email -> "invalid_email_address")
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.email, Errors.emailFormat, form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val fullName: String = "removeConnectionFullName"
      val phone            = "removeConnectionDetails.phone"
      val email            = "removeConnectionDetails.email"
    }

    val formErrors: FormErrors = new FormErrors

    class RequiredError {
      val fullName: FormError = FormError(errorKey.fullName, Errors.required)
    }

    class FormErrors {
      val required: RequiredError = new RequiredError
    }

    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"  -> "12345678901",
      "contactDetails.phone"  -> "01234 123123",
      "contactDetails.email1" -> "blah.blah@test.com",
      "fullName"              -> "Mr John Smith"
    )

  }
}
