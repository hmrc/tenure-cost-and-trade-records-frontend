/*
 * Copyright 2025 HM Revenue & Customs
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
import controllers.aboutthetradinghistory
import models.ForType
import models.ForType.*
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class FinancialYearEndDatesSummaryControllerSpec extends TestBaseSpec {

  def financialYearEndDatesSummaryController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory),
    forType: ForType = FOR6010,
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledTurnoverSections6076)
  ) = new FinancialYearEndDatesSummaryController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearEndDatesSummaryView,
    preEnrichedActionRefiner(
      aboutTheTradingHistory = aboutTheTradingHistory,
      aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne,
      forType = forType
    ),
    mockSessionRepo
  )

  "FinancialYearEndDatesSummaryController" should {
    "return 200" in {
      val result = financialYearEndDatesSummaryController().show()(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "return 200 for 6048" in {
      val session6048    = aboutYourTradingHistory6048YesSession
      val sessionRequest = SessionRequest(session6048, FakeRequest())

      val result = financialYearEndDatesSummaryController(session6048.aboutTheTradingHistory, session6048.forType)
        .show()(sessionRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = financialYearEndDatesSummaryController().show()(FakeRequest())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = financialYearEndDatesSummaryController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      )
    }

    "SUBMIT /" should {
      "return redirect 400 for empty request" in {
        val res = financialYearEndDatesSummaryController().submit()(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
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
          SessionRequest(
            aboutYourTradingHistory6010YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
        )
      }

      "return to CYA " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6010YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6020 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6020YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = FOR6020).submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
        )
      }

      "return CYA for 6020 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6020YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = FOR6020).submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "return 303 for 6030 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6030YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearEndDatesSummaryController(forType = FOR6030).submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
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
          forType = FOR6030
        ).submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "redirect to the next page for 6045 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val session6045     = aboutYourTradingHistory6045YesSession
        val sessionRequest  = SessionRequest(session6045, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6045.aboutTheTradingHistory, session6045.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
        )
      }

      "redirect to CYA for 6045 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val session6045     = aboutYourTradingHistory6045YesSession
        val sessionRequest  = SessionRequest(session6045, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6045.aboutTheTradingHistory, session6045.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "redirect to the next page for 6048 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val session6048     = aboutYourTradingHistory6048YesSession
        val sessionRequest  = SessionRequest(session6048, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6048.aboutTheTradingHistory, session6048.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
        )
      }

      "redirect to CYA for 6048 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val session6048     = aboutYourTradingHistory6048YesSession
        val sessionRequest  = SessionRequest(session6048, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6048.aboutTheTradingHistory, session6048.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "redirect to the next page for 6076 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true"
          )
        val session6076     = aboutYourTradingHistory6076YesSession
        val sessionRequest  = SessionRequest(session6076, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6076.aboutTheTradingHistory, session6076.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
        )
      }

      "redirect to CYA for 6076 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearEndDatesCorrect" -> "true",
            "from"                           -> "CYA"
          )
        val session6076     = aboutYourTradingHistory6076YesSession
        val sessionRequest  = SessionRequest(session6076, requestWithForm)

        val result =
          financialYearEndDatesSummaryController(session6076.aboutTheTradingHistory, session6076.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }
    }

  }

}
