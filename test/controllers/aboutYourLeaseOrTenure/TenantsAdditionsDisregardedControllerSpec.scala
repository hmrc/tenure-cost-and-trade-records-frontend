/*
 * Copyright 2025 HM Revenue & Customs
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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class TenantsAdditionsDisregardedControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def tenantsAdditionsDisregardedController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new TenantsAdditionsDisregardedController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      tenantsAdditionsDisregardedView,
      preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "TenantsAdditionsDisregardedController GET /" should {
    "return 200 and HTML with Tenants Additional Disregard in the session" in {
      val result = tenantsAdditionsDisregardedController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show().url
      )
    }

    "return 200 and HTML Tenants Additional Disregard with none in the session" in {
      val controller = tenantsAdditionsDisregardedController(forType = FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show().url
      )
    }

    "return 200 None" in {
      val controller = tenantsAdditionsDisregardedController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show().url
      )
    }

    "return 200 and HTML Tenants Additional Disregard with formerLeaseSurrendered is yes in the session" in {
      val controller = tenantsAdditionsDisregardedController(forType = FOR6045)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController.show().url
      )
    }

    "return 200 and HTML Tenants Additional Disregard with one in the session" in {
      val controller = tenantsAdditionsDisregardedController(
        forType = FOR6045,
        aboutLeaseOrAgreementPartTwo = None
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show().url
      )
    }
  }

  "TenantsAdditionsDisregardedController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tenantsAdditionsDisregardedController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = tenantsAdditionsDisregardedController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("tenantsAdditionsDisregarded" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
