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
import controllers.aboutthetradinghistory
import models.ForType
import models.ForType.*
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class FinancialYearsControllerSpec extends TestBaseSpec {

  def financialYearsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory),
    forType: ForType = FOR6045,
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(prefilledTurnoverSections6076)
  ) = new FinancialYearsController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearsView,
    preEnrichedActionRefiner(
      aboutTheTradingHistory = aboutTheTradingHistory,
      aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne,
      forType = forType
    ),
    mockSessionRepo
  )

  "FinancialYearsController" should {
    "return 200" in {
      val result = financialYearsController().show()(FakeRequest())
      status(result) shouldBe Status.OK
    }

    "return 200 for 6048" in {
      val session6048    = aboutYourTradingHistory6048YesSession
      val sessionRequest = SessionRequest(session6048, requestWithForm)

      val result = financialYearsController(session6048.aboutTheTradingHistory, session6048.forType).show()(
        sessionRequest
      )
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = financialYearsController().show()(FakeRequest())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = financialYearsController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      )
    }

    "SUBMIT /" should {
      "return redirect 400 for empty request" in {
        val res = financialYearsController().submit()(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }

    "SUBMIT with correct data" should {
      "return 303 " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearsCorrect" -> "true"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6045YesSession,
            requestWithForm
          ) // aboutYourTradingHistory6010YesSession
        val result          = financialYearsController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(aboutthetradinghistory.routes.StaticCaravansController.show().url)
      }

      "return to CYA " in {
        val requestWithForm = FakeRequest(POST, "/path-to-form-handler")
          .withFormUrlEncodedBody(
            "isFinancialYearsCorrect" -> "true",
            "from"                    -> "CYA"
          )
        val sessionRequest  =
          SessionRequest(
            aboutYourTradingHistory6045YesSession,
            requestWithForm
          )
        val result          = financialYearsController().submit()(sessionRequest)
        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
        )
      }

      "redirect to the next page for 6045 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearsCorrect" -> "true"
          )
        val session6045     = aboutYourTradingHistory6045YesSession
        val sessionRequest  = SessionRequest(session6045, requestWithForm)

        val result =
          financialYearsController(session6045.aboutTheTradingHistory, session6045.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.StaticCaravansController.show().url
        )
      }

      "redirect to CYA for 6045 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearsCorrect" -> "true",
            "from"                    -> "CYA"
          )
        val session6045     = aboutYourTradingHistory6045YesSession
        val sessionRequest  = SessionRequest(session6045, requestWithForm)

        val result =
          financialYearsController(session6045.aboutTheTradingHistory, session6045.forType).submit()(
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
            "isFinancialYearsCorrect" -> "true"
          )
        val session6048     = aboutYourTradingHistory6048YesSession
        val sessionRequest  = SessionRequest(session6048, requestWithForm)

        val result =
          financialYearsController(session6048.aboutTheTradingHistory, session6048.forType).submit()(
            sessionRequest
          )

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          aboutthetradinghistory.routes.StaticCaravansController.show().url // TODO: Income6048Controller
        )
      }

      "redirect to CYA for 6048 " in {
        val requestWithForm = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(
            "isFinancialYearsCorrect" -> "true",
            "from"                    -> "CYA"
          )
        val session6048     = aboutYourTradingHistory6048YesSession
        val sessionRequest  = SessionRequest(session6048, requestWithForm)

        val result =
          financialYearsController(session6048.aboutTheTradingHistory, session6048.forType).submit()(
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
