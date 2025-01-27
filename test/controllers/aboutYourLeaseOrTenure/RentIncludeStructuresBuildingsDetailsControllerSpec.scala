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

import connectors.Audit
import form.aboutYourLeaseOrTenure.RentIncludeStructuresBuildingsDetailsForm.rentIncludeStructuresBuildingsDetailsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RentIncludeStructuresBuildingsDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]

  def rentIncludeStructuresBuildingsDetailsController(
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(
      prefilledAboutLeaseOrAgreementPartFour
    )
  ) = new RentIncludeStructuresBuildingsDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    rentIncludeStructuresBuildingsDetailsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour),
    mockSessionRepo
  )

  "rentDevelopedLandDetailsController GET /" should {
    "return 200 and HTML with rentIncludeStructuresBuildingsDetails in the session" in {
      val result = rentIncludeStructuresBuildingsDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show().url
      )
    }

    "return 200 and HTML when no rentIncludeStructuresBuildingsDetails in the session" in {
      val controller = rentIncludeStructuresBuildingsDetailsController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show().url
      )
    }
  }

  "ConnectedToLandlordDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = rentIncludeStructuresBuildingsDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data rentIncludeStructuresBuildingsDetails submitted" in {
      val res = rentIncludeStructuresBuildingsDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("rentIncludeStructuresBuildingsDetails" -> "Test content")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "rentIncludeStructuresBuildingsDetails form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.rentIncludeStructuresBuildingsDetails
      val form     = rentIncludeStructuresBuildingsDetailsForm.bind(formData)

      mustContainError(
        errorKey.rentIncludeStructuresBuildingsDetails,
        "error.rentIncludeStructuresBuildingsDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentIncludeStructuresBuildingsDetails: String = "rentIncludeStructuresBuildingsDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "rentIncludeStructuresBuildingsDetails" -> "Test content"
    )
  }
}
