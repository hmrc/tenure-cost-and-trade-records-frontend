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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class SurrenderedLeaseAgreementControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def surrenderedLeaseAgreementController(
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(prefilledAboutLeaseOrAgreementPartFour)
  ) = new SurrenderLeaseAgreementDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    surrenderedLeaseAgreementView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour),
    mockSessionRepo
  )

  "IncentivesPaymentsConditionsController GET /" should {
    "return 200 and HTML with Incentives Payments Conditions in the session" in {
      val result = surrenderedLeaseAgreementController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController.show().url
      )
    }

    "return 200 and HTML with no Incentives Payments Conditions in the session" in {
      val controller = surrenderedLeaseAgreementController(aboutLeaseOrAgreementPartFour = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController.show().url
      )
    }

  }

  "IncentivesPaymentsConditionsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = surrenderedLeaseAgreementController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data surrenderedLeaseAgreement submitted" in {
      val res = surrenderedLeaseAgreementController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "surrenderedLeaseAgreementAmount" -> "1000",
          "surrenderedLeaseAgreementMonths" -> "Lease agreement months"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
