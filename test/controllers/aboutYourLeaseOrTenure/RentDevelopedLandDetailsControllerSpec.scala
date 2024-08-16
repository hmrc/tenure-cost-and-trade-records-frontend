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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.RentDevelopedLandDetailsForm.rentDevelopedLandDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RentDevelopedLandDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def rentDevelopedLandDetailsController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentDevelopedLandDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentDevelopedLandDetailsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "rentDevelopedLandDetailsController GET /" should {
    "return 200 and HTML with Rent Developed Land Details in the session" in {
      val result = rentDevelopedLandDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show().url
      )
    }

    "return 200 and HTML when no Rent Developed Land Details in the session" in {
      val controller = rentDevelopedLandDetailsController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show().url
      )
    }
  }

  "ConnectedToLandlordDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = rentDevelopedLandDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data connectedToLandlordDetails submitted" in {
      val res = rentDevelopedLandDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("rentDevelopedLandDetails" -> "Test content")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Connected to landlord details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.rentDevelopedLandDetails
      val form     = rentDevelopedLandDetailsForm.bind(formData)

      mustContainError(
        errorKey.rentDevelopedLandDetails,
        "error.rentDevelopedLandDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentDevelopedLandDetails: String = "rentDevelopedLandDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "rentDevelopedLandDetails" -> "Test content"
    )
  }
}
