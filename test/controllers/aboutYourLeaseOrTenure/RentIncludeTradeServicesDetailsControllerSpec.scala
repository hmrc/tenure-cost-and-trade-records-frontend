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

import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class RentIncludeTradeServicesDetailsControllerSpec extends TestBaseSpec {

  def rentIncludeTradeServicesDetailsController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentIncludeTradeServicesDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentIncludeTradeServicesDetailsView,
    rentIncludeTradeServicesDetailsTextAreaView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "RentIncludeTradeServicesDetailsController GET /" should {
    "return 200 and HTML with Rent Include Trade Services Details in the session" in {
      val result = rentIncludeTradeServicesDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML Rent Include Trade Services Details with none in the session" in {
      val controller = rentIncludeTradeServicesDetailsController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML with Rent Include Trade Services Details in the session for 6045" in {
      val result = rentIncludeTradeServicesDetailsController(forType = FOR6045).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML Rent Include Trade Services Details with none in the session for 6045" in {
      val controller = rentIncludeTradeServicesDetailsController(
        forType = FOR6045,
        aboutLeaseOrAgreementPartThree = None
      )
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
      val res = rentIncludeTradeServicesDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if an empty form is submitted for 6045" in {
      val res = rentIncludeTradeServicesDetailsController(forType = FOR6045).submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = rentIncludeTradeServicesDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("describeServices" -> "text")
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted for 6045" in {
      val res = rentIncludeTradeServicesDetailsController(forType = FOR6045).submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("describeServicesTextArea" -> "text")
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
