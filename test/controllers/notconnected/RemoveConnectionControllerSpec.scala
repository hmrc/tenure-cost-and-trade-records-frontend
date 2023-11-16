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

package controllers.notconnected

import form.Errors
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import models.submissions.notconnected.RemoveConnectionDetails
import play.api.data.FormError
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

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

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = removeConnectionController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
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
      val formData = baseFormData - errorKey.email
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val fullName: String

      val phone: String

      val email: String
    } = new {
      val fullName: String = "removeConnectionFullName"
      val phone            = "removeConnectionDetails.phone"
      val email            = "removeConnectionDetails.email"
    }

    val formErrors: Object {
      val required: Object {
        val fullName: FormError
      }
    } = new {
      val required: Object {
        val fullName: FormError
      } = new {
        val fullName: FormError = FormError(errorKey.fullName, Errors.required)
      }
    }

    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"  -> "12345678901",
      "contactDetails.phone"  -> "01234 123123",
      "contactDetails.email1" -> "blah.blah@test.com",
      "fullName"              -> "Mr John Smith"
    )

  }
}
