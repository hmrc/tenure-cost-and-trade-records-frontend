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
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentOpenMarketValueController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentOpenMarketValueView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def rentOpenMarketValueController6020(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentOpenMarketValueController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentOpenMarketValueView,
      preEnrichedActionRefiner(forType = ForTypes.for6020, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = rentOpenMarketValueController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = rentOpenMarketValueController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 6020" in {
      val result = rentOpenMarketValueController6020().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML 6020" in {
      val result = rentOpenMarketValueController6020().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentOpenMarketValueController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
