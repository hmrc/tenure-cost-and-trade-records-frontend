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

import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class BenefitsGivenControllerSpec extends TestBaseSpec {

  def benefitsGivenController = new BenefitsGivenController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    benefitsGivenView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def benefitsGivenControllerNone =
    new BenefitsGivenController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      benefitsGivenView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = None),
      mockSessionRepo
    )

  "BenefitsGivenController GET /" should {
    "return 200 and HTML with Benefits Given in the session" in {
      val result = benefitsGivenController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show().url
      )
    }

    "return 200 and HTML when no Benefits Given in the session" in {
      val result = benefitsGivenControllerNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show().url
      )
    }
  }

  "BenefitsGivenController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = benefitsGivenController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
