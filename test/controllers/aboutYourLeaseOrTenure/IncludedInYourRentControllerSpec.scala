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

import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class IncludedInYourRentControllerSpec extends TestBaseSpec {

  def IncludedInYourRentController = new IncludedInYourRentController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    includedInYourRentView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def IncludedInYourRentControllerNone = new IncludedInYourRentController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    includedInYourRentView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "IncludedInYourRentController GET /" should {
    "return 200 and HTML with Included In Your Rent Details in the session" in {
      val result = IncludedInYourRentController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
      )
    }

    "return 200 and HTML with no Included In Your Rent Details in the session" in {
      val result = IncludedInYourRentControllerNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show().url
      )
    }
  }

  "IncludedInYourRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = IncludedInYourRentController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }
}
