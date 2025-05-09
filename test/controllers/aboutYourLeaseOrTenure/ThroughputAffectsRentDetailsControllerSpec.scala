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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class ThroughputAffectsRentDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def throughputAffectsRentDetailsController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new ThroughputAffectsRentDetailsController(
    throughputAffectsRentDetailsView,
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "ThroughputAffectsRentDetailsController GET /" should {
    "return 200 and HTML with Through Put Affect Rent Details in the session" in {
      val result = throughputAffectsRentDetailsController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController.show().url
      )
    }

    "return 200 and HTML Through Put Affect Rent Details with none in the session" in {
      val controller = throughputAffectsRentDetailsController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController.show().url
      )
    }
  }

  "ThroughputAffectsRentDetailsController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = throughputAffectsRentDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = throughputAffectsRentDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("throughputAffectsRentDetails" -> "Throughput Details")
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if tenantsAdditionsDisregardedDetails is greater than max length is submitted" in {
      val res = throughputAffectsRentDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("throughputAffectsRentDetails" -> "x" * 2001)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
