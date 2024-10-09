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

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class ConcessionTypeFeesControllerSpec extends TestBaseSpec {

  def controller =
    new ConcessionTypeFeesController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      feeReceivedView,
      preEnrichedActionRefiner(
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045),
        aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6045)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = controller.show(0)(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller.show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render a correct back link to concession type details if no query parameters in the url " in {
      val result  = controller.show(0)(fakeRequest)
      val content = contentAsString(result)
      content should include("/concession-type-details")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST on empty form submission" in {

      val res = controller.submit(0)(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
