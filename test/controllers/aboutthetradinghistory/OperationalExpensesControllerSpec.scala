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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class OperationalExpensesControllerSpec extends TestBaseSpec {

  def operationalExpensesController =
    new OperationalExpensesController(
      stubMessagesControllerComponents(),
      aboutYourTradingHistoryNavigator,
      operationalExpenses6076View,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = operationalExpensesController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = operationalExpensesController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      val content = contentAsString(result)
      content should not include "/check-your-answers-about-the-trading-history"
    }

    "render back link to CYA if come from CYA" in {
      val result  = operationalExpensesController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("""href="/send-trade-and-cost-information/check-your-answers-about-the-trading-history"""")
      content should not include "/premises-costs"
    }

  }

  "SUBMIT /" should {
    "return 400 for empty turnoverSections" in {
      val res = operationalExpensesController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
