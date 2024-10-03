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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleIBuildingInsuranceForm.ultimatelyResponsibleBuildingInsuranceForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import utils.TestBaseSpec
import scala.language.reflectiveCalls
import play.api.test.*
import play.api.test.Helpers.*

class UltimatelyResponsibleBuildingInsuranceControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def ultimatelyResponsibleBuildingInsuranceController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new UltimatelyResponsibleBuildingInsuranceController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    ultimatelyResponsibleBuildingInsuranceView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "UltimatelyResponsibleBuildingInsuranceController GET /" should {
    "return 200 and HTML with Ultimately Responsible Building Insurance in the session" in {
      val result = ultimatelyResponsibleBuildingInsuranceController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show().url
      )
    }

    "return 200 and HTML when no Ultimately Responsible Building Insurance in the session" in {
      val controller = ultimatelyResponsibleBuildingInsuranceController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show().url
      )
    }
  }

  "UltimatelyResponsibleBuildingInsuranceController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleBuildingInsuranceController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = ultimatelyResponsibleBuildingInsuranceController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("buildingInsurance" -> "landlord")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Ultimately Responsible BI form" should {
    "error if Ultimately Responsible BI is missing" in {
      val formData = baseFormData - errorKey.ultimatelyResponsibleBI
      val form     = ultimatelyResponsibleBuildingInsuranceForm.bind(formData)

      mustContainError(errorKey.ultimatelyResponsibleBI, "error.buildingInsurance.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val ultimatelyResponsibleBI: String = "buildingInsurance"
    }

    val baseFormData: Map[String, String] = Map("buildingInsurance" -> "tenant")
  }
}
