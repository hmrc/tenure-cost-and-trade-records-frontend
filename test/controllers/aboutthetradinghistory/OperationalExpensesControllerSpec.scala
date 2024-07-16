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

import controllers.aboutthetradinghistory
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

    "return correct backLink when 'from=IES' query param is present" in {
      val result = operationalExpensesController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      )
    }

  }

  private def operationalExpensesForYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"operationalExpensesSeq.turnover[$idx].advertising"    -> "100.10",
      s"operationalExpensesSeq.turnover[$idx].administration" -> "200.99",
      s"operationalExpensesSeq.turnover[$idx].insurance"      -> "300",
      s"operationalExpensesSeq.turnover[$idx].legalFees"      -> "400",
      s"operationalExpensesSeq.turnover[$idx].interest"       -> "5000",
      s"operationalExpensesSeq.turnover[$idx].other"          -> "0"
    )

  private def operationalExpensesFormData: Seq[(String, String)] =
    operationalExpensesForYear(0) ++
      operationalExpensesForYear(1) ++
      operationalExpensesForYear(2)

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = operationalExpensesController.submit(
        fakePostRequest.withFormUrlEncodedBody(operationalExpensesFormData*)
      )

      status(res)           shouldBe Status.SEE_OTHER
      redirectLocation(res) shouldBe Some(aboutthetradinghistory.routes.HeadOfficeExpensesController.show().url)
    }

    "return 400 for empty turnoverSections" in {
      val res = operationalExpensesController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
