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

package controllers.aboutthetradinghistory

import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class GrossReceiptsForBaseLoadControllerSpec extends TestBaseSpec {

  def grossReceiptsForBaseLoadController =
    new GrossReceiptsForBaseLoadController(
      stubMessagesControllerComponents(),
      aboutYourTradingHistoryNavigator,
      grossReceiptsForBaseLoadView,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = grossReceiptsForBaseLoadController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = grossReceiptsForBaseLoadController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = grossReceiptsForBaseLoadController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
