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

import actions.SessionRequest
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FinancialYearEndDatesSummaryControllerSpec extends TestBaseSpec {

  def financialYearEndDatesSummaryController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory),
    forType: String = "FOR6010"
  ) = new FinancialYearEndDatesSummaryController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearEndDatesSummaryView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory, forType = forType),
    mockSessionRepo
  )

  "FinancialYearEndDatesSummaryController" should {
    "return 200" in {
      val result = financialYearEndDatesSummaryController().show()(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = financialYearEndDatesSummaryController().show()(FakeRequest())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "return redirect 400 for empty request" in {
        val res = financialYearEndDatesSummaryController().submit()(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }

    "SUBMIT with correct data" should {
      "return 303 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6010YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.aboutthetradinghistory.routes.TurnoverController.show().url)
      }

      "return to CYA " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6010YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6020 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6020YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = "FOR6020").submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
        )
      }

      "return CYA for 6020 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6020YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = "FOR6020").submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6030 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6030YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = "FOR6030").submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.Turnover6030Controller.show().url
        )
      }

      "return to CYA for 6030 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6030YesSession, requestWithForm)
        val result          = financialYearEndDatesSummaryController(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030),
          forType = "FOR6030"
        ).submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }
    }

  }
}
