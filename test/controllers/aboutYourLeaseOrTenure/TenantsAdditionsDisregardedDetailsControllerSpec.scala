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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class TenantsAdditionsDisregardedDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def tenantsAdditionsDisregardedDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new TenantsAdditionsDisregardedDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      tenantsAdditionsDisregardedDetailsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "TenantsAdditionsDisregardedDetailsController GET /" should {
    "return 200 and HTML with Tenants Additional Disregard Details in the session" in {
      val result = tenantsAdditionsDisregardedDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }

    "return 200 and HTML Tenants Additional Disregard Details with none in the session" in {
      val controller = tenantsAdditionsDisregardedDetailsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }
  }

  "TenantsAdditionsDisregardedDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tenantsAdditionsDisregardedDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = tenantsAdditionsDisregardedDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("tenantsAdditionsDisregardedDetails" -> "Tenants Details")
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if tenantsAdditionsDisregardedDetails is greater than max length is submitted" in {
      val res = tenantsAdditionsDisregardedDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "tenantsAdditionsDisregardedDetails" -> "x" * 2001
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
