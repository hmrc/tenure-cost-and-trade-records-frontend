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
import play.api.test.Helpers._
import utils.TestBaseSpec

class ServicePaidSeparatelyChargeControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def servicePaidSeparatelyChargeController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new ServicePaidSeparatelyChargeController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    servicePaidSeparatelyChargeView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "Service paid separately charge controller" should {
    "return 200 and HTML with Service Paid Separately Charge in the session" in {
      val result = servicePaidSeparatelyChargeController().show(0)(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(0)).url
      )
    }

    "return 200 and HTML without Service Paid Separately Charge in the session" in {
      val controller = servicePaidSeparatelyChargeController(
        aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThreeNo)
      )
      val result     = controller.show(0)(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(0)).url
      )
    }

    "return 200 and HTML with none in the session" in {
      val controller = servicePaidSeparatelyChargeController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(0)(fakeRequest)
      status(result) shouldBe SEE_OTHER
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = servicePaidSeparatelyChargeController().submit(0)(
          FakeRequest().withFormUrlEncodedBody(Seq.empty*)
        )
        status(result) shouldBe BAD_REQUEST
      }

      "Redirect when form data servicePaidSeparatelyCharge submitted" in {
        val res = servicePaidSeparatelyChargeController().submit(0)(
          FakeRequest(POST, "/").withFormUrlEncodedBody("annualCharge" -> "1000")
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }
}
