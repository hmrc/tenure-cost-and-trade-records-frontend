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

import connectors.Audit
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class TotalPayrollCostsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  val aboutYourTradingHistoryController = new TotalPayrollCostsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    totalPayrollCostsView,
    preEnrichedActionRefiner(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory)),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = aboutYourTradingHistoryController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYourTradingHistoryController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutYourTradingHistoryController.show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=IES' query param is present" in {
      val result = aboutYourTradingHistoryController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutYourTradingHistoryController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
