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
import models.ForType
import models.ForType.*
import form.aboutYourLeaseOrTenure.LegalOrPlanningRestrictionsForm.legalPlanningRestrictionsForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class LegalOrPlanningRestrictions6048ControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  val mockAudit: Audit = mock[Audit]

  def legalOrPlanningRestrictionsController(
    forType: ForType = FOR6048,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new LegalOrPlanningRestrictionsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      legalOrPlanningRestrictionsView,
      preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "LegalOrPlanningRestrictionsController GET /" should {
    "return 200 and HTML with legal or planning restrictions in the session" in {
      val result = legalOrPlanningRestrictionsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumAmountDetailsController.show().url
      )
    }

    "return 200 and HTML with capital sum with yes in the session" in {
      val controller = legalOrPlanningRestrictionsController(FOR6048)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumAmountDetailsController.show().url
      )
    }

    "return 200 and HTML with capital sum with no in the session" in {
      val controller =
        legalOrPlanningRestrictionsController(FOR6048, Some(prefilledAboutLeaseOrAgreementPartTwoNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return 200 and HTML legal or planning restrictions is none in the session for 6020" in {
      val controller = legalOrPlanningRestrictionsController(FOR6048, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }
  }

  "LegalOrPlanningRestrictionsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = legalOrPlanningRestrictionsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = legalOrPlanningRestrictionsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("legalOrPlanningRestrictions" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Legal or planning restrictions form" should {
    "error if Legal or planning restrictions answer is missing" in {
      val formData = baseFormData - errorKey.legalOrPlanningRestrictions
      val form     = legalPlanningRestrictionsForm.bind(formData)

      mustContainError(errorKey.legalOrPlanningRestrictions, "error.legalOrPlanningRestrictions.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val legalOrPlanningRestrictions: String = "legalOrPlanningRestrictions"
    }

    val baseFormData: Map[String, String] = Map("legalOrPlanningRestrictions" -> "yes")
  }
}
