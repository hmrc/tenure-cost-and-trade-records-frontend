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

import form.aboutYourLeaseOrTenure.RentDevelopedLandForm.rentDevelopedLandForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RentDevelopedLandControllerSpec extends TestBaseSpec {

  import TestData.*
  import utils.FormBindingTestAssertions.*

  def rentDevelopedLandController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentDevelopedLandController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentDevelopedLandView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "RentDevelopedLandController GET /" should {
    "return 200 and HTML with rent developed land in the session" in {
      val result = rentDevelopedLandController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController.show().url
      )
    }

    "return 200 and HTML when no Connected To Landlord in the session" in {
      val controller = rentDevelopedLandController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController.show().url
      )
    }
  }

  "ConnectedToLandlordController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = rentDevelopedLandController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data rentDevelopedLand submitted" in {
      val res = rentDevelopedLandController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("rentDevelopedLand" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Rent developed land form" should {
    "error if connected to landlord answer is missing" in {
      val formData = baseFormData - errorKey.rentDevelopedLand
      val form     = rentDevelopedLandForm.bind(formData)

      mustContainError(errorKey.rentDevelopedLand, "error.rentDevelopedLand.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentDevelopedLand: String = "rentDevelopedLand"
    }

    val baseFormData: Map[String, String] = Map("rentDevelopedLand" -> "yes")
  }
}
