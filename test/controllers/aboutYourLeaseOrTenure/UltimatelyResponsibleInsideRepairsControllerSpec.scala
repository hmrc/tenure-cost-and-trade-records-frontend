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

import form.aboutYourLeaseOrTenure.UltimatelyResponsibleInsideRepairsForm.ultimatelyResponsibleInsideRepairsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import utils.TestBaseSpec
import scala.language.reflectiveCalls
import play.api.test.*
import play.api.test.Helpers.*

class UltimatelyResponsibleInsideRepairsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def ultimatelyResponsibleInsideRepairsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new UltimatelyResponsibleInsideRepairsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    ultimatelyResponsibleInsideRepairsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "UltimatelyResponsibleInsideRepairsController GET /" should {
    "return 200 and HTML with Ultimately Responsible Inside Repairs in the session" in {
      val result = ultimatelyResponsibleInsideRepairsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show().url
      )
    }

    "return 200 and HTML when no Ultimately Responsible Inside Repairs in the session" in {
      val controller = ultimatelyResponsibleInsideRepairsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = ultimatelyResponsibleInsideRepairsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = ultimatelyResponsibleInsideRepairsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("insideRepairs" -> "landlord")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Ultimately Responsible IR form" should {
    "error if Ultimately Responsible IR is missing" in {
      val formData = baseFormData - errorKey.ultimatelyResponsibleIR
      val form     = ultimatelyResponsibleInsideRepairsForm.bind(formData)

      mustContainError(errorKey.ultimatelyResponsibleIR, "error.insideRepairs.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val ultimatelyResponsibleIR: String = "insideRepairs"
    }

    val baseFormData: Map[String, String] = Map("insideRepairs" -> "tenant")
  }
}
