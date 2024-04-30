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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleIBuildingInsuranceForm.ultimatelyResponsibleBuildingInsuranceForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class UltimatelyResponsibleBuildingInsuranceControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def ultimatelyResponsibleBuildingInsuranceController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new UltimatelyResponsibleBuildingInsuranceController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleBuildingInsuranceView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  def ultimatelyResponsibleBuildingInsuranceControllerNone =
    new UltimatelyResponsibleBuildingInsuranceController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      ultimatelyResponsibleBuildingInsuranceView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
      mockSessionRepo
    )

  "UltimatelyResponsibleBuildingInsuranceController GET /" should {
    "return 200 and HTML with Ultimately Responsible Building Insurance in the session" in {
      val result = ultimatelyResponsibleBuildingInsuranceController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no Ultimately Responsible Building Insurance in the session" in {
      val result = ultimatelyResponsibleBuildingInsuranceControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleBuildingInsuranceController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val ultimatelyResponsibleBI: String
    } = new {
      val ultimatelyResponsibleBI: String = "buildingInsurance"
    }

    val baseFormData: Map[String, String] = Map("buildingInsurance" -> "tenant")
  }
}
