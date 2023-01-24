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
import navigation.RemoveConnectionNavigator
import controllers.removeconnection.RemoveConnectionController
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import play.api.data.FormError
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.notconnected.removeConnection

class RemoveConnectionControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.{mustContainError, mustContainRequiredErrorFor}

  val mockRemoveConnectionNavigator = mock[RemoveConnectionNavigator]
  val mockRemoveConnectionView      = mock[removeConnection]
  when(mockRemoveConnectionView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val removeConnectionController = new RemoveConnectionController(
    stubMessagesControllerComponents(),
    mockRemoveConnectionNavigator,
    mockRemoveConnectionView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = removeConnectionController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = removeConnectionController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "Remove connection form" should {
    "error if phone is missing" in {
      val formData = baseFormData - errorKey.phone
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }

    "error if email is missing" in {
      val formData = baseFormData - errorKey.email
      val form     = removeConnectionForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }
  }

  object TestData {
    val errorKey = new {
      val fullName: String = "removeConnectionFullName"
      val phone            = "removeConnectionDetails.phone"
      val email            = "removeConnectionDetails.email"
    }

    val formErrors = new {
      val required = new {
        val fullName = FormError(errorKey.fullName, Errors.required)
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
