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
import form.requestReferenceNumber.RequestReferenceNumberContactDetailsForm.noReferenceNumberContactDetailsForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec

class NoReferenceNumberContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def noReferenceNumberContactDetailsController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new RequestReferenceNumberContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    noReferenceNumberContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = noReferenceNumberContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = noReferenceNumberContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = noReferenceNumberContactDetailsController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "no Reference Number Contact Details form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = noReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.fullName, "error.noReferenceNumberContactDetailsFullName.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData - errorKey.phone
      val form     = noReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }

    "error if email is missing" in {
      val formData = baseFormData - errorKey.email
      val form     = noReferenceNumberContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }
  }

  object TestData {
    val errorKey = new {
      val fullName = "noReferenceNumberContactDetailsFullName"
      val phone    = "noReferenceNumberContactDetails.phone"
      val email    = "noReferenceNumberContactDetails.email"
    }

    val baseFormData: Map[String, String] = Map(
      "noReferenceNumberContactDetailsFullName" -> "John Smith",
      "noReferenceNumberContactDetails.phone"   -> "01234 123123",
      "noReferenceNumberContactDetails.email1"  -> "blah.blah@test.com"
    )
  }
}
