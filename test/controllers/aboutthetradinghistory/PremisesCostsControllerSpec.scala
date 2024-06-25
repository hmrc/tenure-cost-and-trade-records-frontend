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
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class PremisesCostsControllerSpec extends TestBaseSpec {

  def premisesCostsController =
    new PremisesCostsController(
      stubMessagesControllerComponents(),
      aboutYourTradingHistoryNavigator,
      premisesCostsView,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = premisesCostsController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = premisesCostsController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = premisesCostsController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include "/financial-year-end"
    }

    "return correct backLink when 'from=IES' query param is present" in {
      val result = premisesCostsController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      )
    }
  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = premisesCostsController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
