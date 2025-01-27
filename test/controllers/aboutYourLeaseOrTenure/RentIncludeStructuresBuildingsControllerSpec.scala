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
import form.aboutYourLeaseOrTenure.RentIncludeStructuresBuildingsForm.rentIncludeStructuresBuildingsForm
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartThree}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RentIncludeStructuresBuildingsControllerSpec extends TestBaseSpec {

  import TestData.*
  import utils.FormBindingTestAssertions.*
  val mockAudit: Audit = mock[Audit]

  def rentIncludeStructuresBuildingsController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    ),
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(
      prefilledAboutLeaseOrAgreementPartFour
    )
  ) = new RentIncludeStructuresBuildingsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    rentIncludeStructuresBuildingsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour),
    mockSessionRepo
  )

  "RentDevelopedLandController GET /" should {
    "return 200 and HTML with rent Include Structures Buildings in the session" in {
      val result = rentIncludeStructuresBuildingsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController.show().url
      )
    }

    "return 200 and HTML when no rent Include Structures Buildings in the session" in {
      val controller = rentIncludeStructuresBuildingsController(aboutLeaseOrAgreementPartFour = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController.show().url
      )
    }

    "return 200 and HTML when no rent Include Structures Buildings in the session with no rentDevelopedLand" in {
      val controller = rentIncludeStructuresBuildingsController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController.show().url
      )
    }
  }

  "rentIncludeStructuresBuildingsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = rentIncludeStructuresBuildingsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data rentIncludeStructuresBuildings submitted" in {
      val res = rentIncludeStructuresBuildingsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("rentIncludeStructuresBuildings" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Rent developed land form" should {
    "error if connected to landlord answer is missing" in {
      val formData = baseFormData - errorKey.rentIncludeStructuresBuildings
      val form     = rentIncludeStructuresBuildingsForm.bind(formData)

      mustContainError(errorKey.rentIncludeStructuresBuildings, "error.rentIncludeStructuresBuildings.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentIncludeStructuresBuildings: String = "rentIncludeStructuresBuildings"
    }

    val baseFormData: Map[String, String] = Map("rentIncludeStructuresBuildings" -> "yes")
  }
}
