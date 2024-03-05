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

package controllers.aboutfranchisesorlettings

import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FeeReceivedControllerSpec extends TestBaseSpec {

  def feeReceivedController =
    new FeeReceivedController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      feeReceivedView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings)),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = feeReceivedController.show(0)(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = feeReceivedController.show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {

      val res = feeReceivedController.submit(0)(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
