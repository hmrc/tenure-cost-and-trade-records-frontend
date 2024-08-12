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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class RentIncludeTradeServicesDetails6045ControllerSpec extends TestBaseSpec {

  def rentIncludeTradeServicesDetails6045Controller(
    forType: String = forType6045,
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentIncludeTradeServicesDetailsController(
    stubMessagesControllerComponents(),
    inject[AboutYourLeaseOrTenureNavigator],
    rentIncludeTradeServicesDetailsView,
    rentIncludeTradeServicesDetailsTextAreaView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "RentIncludeTradeServicesDetailsController GET /" should {
    "return 200 and HTML with Rent Include Trade Services Details in the session" in {
      val result = rentIncludeTradeServicesDetails6045Controller().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML Rent Include Trade Services Details with none in the session" in {
      val controller = rentIncludeTradeServicesDetails6045Controller(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }
  }

  "RentIncludeTradeServicesDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentIncludeTradeServicesDetails6045Controller().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
