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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FinancialYearEndDatesControllerSpec extends TestBaseSpec {

  def financialYearEndDatesController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory)
  ) = new FinancialYearEndDatesController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearEndDatesView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "FinancialYearEndDatesController" should {
    "return 200" in {
      val result = financialYearEndDatesController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = financialYearEndDatesController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "return redirect 303 for empty turnoverSections" in {
        val res = financialYearEndDatesController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe SEE_OTHER
      }
    }
  }

}
