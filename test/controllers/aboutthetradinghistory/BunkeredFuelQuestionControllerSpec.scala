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

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class BunkeredFuelQuestionControllerSpec extends TestBaseSpec {

  val bunkeredFuelQuestionController = new BunkeredFuelQuestionController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    bunkeredFuelQuestionView,
    preEnrichedActionRefiner(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory)),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = bunkeredFuelQuestionController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = bunkeredFuelQuestionController.show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = bunkeredFuelQuestionController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
