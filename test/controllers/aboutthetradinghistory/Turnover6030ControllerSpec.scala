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

import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class Turnover6030ControllerSpec extends TestBaseSpec {

  def turnoverController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory6030)
  ) = new Turnover6030Controller(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    turnover6030View,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "About your trading history controller for 6030" should {
    "return 200" in {
      val result = turnoverController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = turnoverController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = turnoverController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

}
