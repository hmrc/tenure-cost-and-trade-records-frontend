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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentIncludeFixtureAndFittingsDetailsControllerSpec extends TestBaseSpec {

  def rentIncludeFixtureAndFittingsDetailsController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new RentIncludeFixtureAndFittingsDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentIncludeFixtureAndFittingsDetailsView,
    rentIncludeFixtureAndFittingsDetailsTextAreaView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "RentIncludeFixtureAndFittingsDetailsController GET /" should {
    "return 200 data" in {
      val result = rentIncludeFixtureAndFittingsDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }

    "return 200 none" in {
      val controller = rentIncludeFixtureAndFittingsDetailsController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }
  }

  "RentIncludeFixtureAndFittingsDetailsController SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {
      val res = rentIncludeFixtureAndFittingsDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a See_Other if an empty form is submitted with no data in session" in {
      val controller = rentIncludeFixtureAndFittingsDetailsController(aboutLeaseOrAgreementPartOne = None)
      val res        = controller.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe SEE_OTHER
    }
  }

}
