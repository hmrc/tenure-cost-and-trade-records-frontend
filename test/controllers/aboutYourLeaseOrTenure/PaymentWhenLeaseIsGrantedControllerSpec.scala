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

import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class PaymentWhenLeaseIsGrantedControllerSpec extends TestBaseSpec {

  def paymentWhenLeaseIsGrantedController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new PaymentWhenLeaseIsGrantedController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      paymentWhenLeaseIsGrantedView,
      preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "PaymentWhenLeaseIsGrantedController GET /" should {
    "return 200 and HTML with pay capital sum details yes in the session" in {
      val result = paymentWhenLeaseIsGrantedController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return 200 and HTML with pay capital sum details no in the session" in {
      val controller = paymentWhenLeaseIsGrantedController(aboutLeaseOrAgreementPartTwo =
        Some(prefilledAboutLeaseOrAgreementPartTwoNo)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return 200 and HTML payment when lease granted with none in the session" in {
      val controller = paymentWhenLeaseIsGrantedController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with pay capital sum details yes in the session for 6030" in {
      val controller = paymentWhenLeaseIsGrantedController(forType = ForTypes.for6030)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = paymentWhenLeaseIsGrantedController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "PaymentWhenLeaseIsGrantedController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = paymentWhenLeaseIsGrantedController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
