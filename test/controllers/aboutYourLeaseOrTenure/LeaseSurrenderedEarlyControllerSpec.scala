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
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LeaseSurrenderedEarlyControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def leaseSurrenderedEarlyController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  )                    = new LeaseSurrenderedEarlyController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    leaseSurrenderedEarlyView,
    preEnrichedActionRefiner(
      aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo,
      aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
    ),
    mockSessionRepo
  )

  "LeaseSurrenderedEarlyController GET /" should {
    "return 200 and HTML with additional disregarded yes in the session" in {
      val result = leaseSurrenderedEarlyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show().url
      )
    }

    "return 200 and HTML with additional disregarded no in the session" in {
      val controller =
        leaseSurrenderedEarlyController(aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }

    "return 200 and HTML with none data in the session" in {
      val controller =
        leaseSurrenderedEarlyController(aboutLeaseOrAgreementPartTwo = None, aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show().url
      )
    }
  }

  "LeaseSurrenderedEarlyController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = leaseSurrenderedEarlyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = leaseSurrenderedEarlyController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("leaseSurrenderedEarly" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
