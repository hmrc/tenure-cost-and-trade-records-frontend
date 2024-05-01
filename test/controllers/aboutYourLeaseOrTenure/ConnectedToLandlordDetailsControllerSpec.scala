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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.ConnectedToLandlordDetailsForm.connectedToLandlordDetailsForm
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class ConnectedToLandlordDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def connectedToLandlordDetailsController = new ConnectedToLandlordDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    connectedToLandlordDetailsView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def connectedToLandlordDetailsControllerNone = new ConnectedToLandlordDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    connectedToLandlordDetailsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "ConnectedToLandlordDetailsController GET /" should {
    "return 200 and HTML with data in the session" in {
      val result = connectedToLandlordDetailsController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return 200 and html with none in the session" in {
      val result = connectedToLandlordDetailsControllerNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }
  }

  "ConnectedToLandlordDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = connectedToLandlordDetailsController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Connected to landlord details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.connectedToLandlordDetails
      val form     = connectedToLandlordDetailsForm.bind(formData)

      mustContainError(
        errorKey.connectedToLandlordDetails,
        "error.connectedToLandlordDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val connectedToLandlordDetails: String
    } = new {
      val connectedToLandlordDetails: String = "connectedToLandlordDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "connectedToLandlordDetails" -> "Test content"
    )
  }
}
