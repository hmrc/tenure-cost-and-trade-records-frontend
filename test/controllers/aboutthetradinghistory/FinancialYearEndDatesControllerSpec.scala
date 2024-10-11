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
import models.Session
import play.api.http.Status
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class FinancialYearEndDatesControllerSpec extends TestBaseSpec {

  def financialYearEndDatesController(session: Session) = new FinancialYearEndDatesController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    financialYearEndDatesView,
    preEnrichedActionRefiner(
      forType = session.forType,
      aboutTheTradingHistory = session.aboutTheTradingHistory,
      aboutTheTradingHistoryPartOne = session.aboutTheTradingHistoryPartOne
    ),
    mockSessionRepo
  )

  private def sessionRequest6010(request: FakeRequest[AnyContent] = fakeRequest) =
    SessionRequest(aboutYourTradingHistory6010YesSession, request)

  private def sessionRequest6030(request: FakeRequest[AnyContent] = fakeRequest) =
    SessionRequest(aboutYourTradingHistory6030YesSession, request)

  "FinancialYearEndDatesController" should {
    "return 200 for 6010" in {
      val result = financialYearEndDatesController(aboutYourTradingHistory6010YesSession).show(sessionRequest6010())
      status(result) shouldBe Status.OK
    }

    "return HTML for 6010" in {
      val result = financialYearEndDatesController(aboutYourTradingHistory6010YesSession).show(sessionRequest6010())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return redirect 303 on submit for empty turnoverSections for 6010" in {
      val res = financialYearEndDatesController(aboutYourTradingHistory6010YesSession).submit(
        sessionRequest6010(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      )
      status(res) shouldBe SEE_OTHER
    }

    "return 200 for 6030" in {
      val result = financialYearEndDatesController(aboutYourTradingHistory6030YesSession).show(sessionRequest6030())
      status(result) shouldBe Status.OK
    }

    "return HTML for 6030" in {
      val result = financialYearEndDatesController(aboutYourTradingHistory6030YesSession).show(sessionRequest6030())
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return redirect 303 on submit for empty turnoverSections for 6030" in {
      val res = financialYearEndDatesController(aboutYourTradingHistory6030YesSession).submit(
        sessionRequest6030(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      )
      status(res) shouldBe SEE_OTHER
    }
  }

}
