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

import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentOpenMarketValueControllerSpec extends TestBaseSpec {

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def rentOpenMarketValueController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new RentOpenMarketValueController(
    stubMessagesControllerComponents(),
    mockAboutYourLeaseOrTenureNavigator,
    rentOpenMarketValueView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "RentOpenMarketValueController GET /" should {
    "return 200 and HTML with Open market and Rent Include Fixture and Fittings Details with Yes in the sessions" in {
      val result = rentOpenMarketValueController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show().url
      )
    }

    "return 200 and HTML with Open market and Rent Include Fixture and Fittings Details with None in the sessions" in {
      val controller = rentOpenMarketValueController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }

    "return 200 and HTML with Open market and Rent Include Fixture and Fittings Details with Yes in the sessions with 6020" in {
      val controller = rentOpenMarketValueController(forType = ForTypes.for6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show().url
      )
    }

    "return 200 and HTML with Open market and Rent Include Fixture and Fittings Details with No in the sessions with 6020" in {
      val controller = rentOpenMarketValueController(ForTypes.for6020, Some(prefilledAboutLeaseOrAgreementPartOneNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show().url
      )
    }
  }

  "RentOpenMarketValueController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentOpenMarketValueController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
