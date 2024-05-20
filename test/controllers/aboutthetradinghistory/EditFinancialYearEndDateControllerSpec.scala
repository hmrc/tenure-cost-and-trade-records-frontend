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

class EditFinancialYearEndDateControllerSpec extends TestBaseSpec {

  def editFinancialYearEndDateController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory),
    forType: String = "FOR6010"
  ) = new EditFinancialYearEndDateController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    editFinancialYearEndDateView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory, forType = forType),
    mockSessionRepo
  )

  "EditFinancialYearEndDateController" should {
    "return 200" in {
      val result = editFinancialYearEndDateController().show(0)(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = editFinancialYearEndDateController().show(0)(FakeRequest())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 for 6020" in {
      val result = editFinancialYearEndDateController(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6020),
        forType = "FOR6020"
      ).show(0)(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "return 200 for 6030" in {
      val result = editFinancialYearEndDateController(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030),
        forType = "FOR6030"
      ).show(0)(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "SUBMIT /" should {
      "return redirect 400 for empty request" in {
        val res = editFinancialYearEndDateController().submit(0)(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }

    "SUBMIT with correct data" should {
      "return 303" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6010YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController().submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show().url
        )
      }

      "return to CYA" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022",
            "from"                     -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6010YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController().submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6020" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6020YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6020),
          forType = "FOR6020"
        ).submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show().url
        )
      }

      "return to CYA for 6020" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022",
            "from"                     -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6020YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6020),
          forType = "FOR6020"
        ).submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6030" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6030YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030),
          forType = "FOR6030"
        ).submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.FinancialYearEndDatesSummaryController.show().url
        )
      }

      "return to CYA for 6030" in {
        val index           = 0
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "financial-year-end.day"   -> "2",
            "financial-year-end.month" -> "2",
            "financial-year-end.year"  -> "2022",
            "from"                     -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(aboutYourTradingHistory6030YesSession, requestWithForm) //aboutYourTradingHistory6010YesSession
        val result          = editFinancialYearEndDateController(
          aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030),
          forType = "FOR6030"
        ).submit(index)(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }
    }

  }

}
