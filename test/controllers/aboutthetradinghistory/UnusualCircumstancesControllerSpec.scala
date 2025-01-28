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
import models.ForType.*
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class UnusualCircumstancesControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def unusualCircumstancesController6015 = new UnusualCircumstancesController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    unusualCircumstancesView,
    preEnrichedActionRefiner(forType = FOR6015),
    mockSessionRepo
  )

  def unusualCircumstancesController6030 = new UnusualCircumstancesController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    unusualCircumstancesView,
    preEnrichedActionRefiner(forType = FOR6030),
    mockSessionRepo
  )

  def unusualCircumstancesControllerNone = new UnusualCircumstancesController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    unusualCircumstancesView,
    preEnrichedActionRefiner(aboutTheTradingHistory = None),
    mockSessionRepo
  )

  "UnusualCircumstancesController GET /" should {
    "return 200 and HTML for 6015 with unusual circumstances in the session" in {
      val result = unusualCircumstancesController6015.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
      )
    }

    "return 200 and HTML for 6030 with unusual circumstances in the session" in {
      val result = unusualCircumstancesController6030.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.Turnover6030Controller.show().url
      )
    }

    "return 200 and HTML when no unusual circumstances in the session" in {
      val result = unusualCircumstancesControllerNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }
  }

  "UnusualCircumstancesController SUBMIT /" should {
    "Redirect to CYA if an empty form is submitted" in {
      val result = unusualCircumstancesController6015.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        "/send-trade-and-cost-information/check-your-answers-about-the-trading-history"
      )
    }

    "Redirect to CYA if not empty form submitted and save data in the session" in {
      val testData = Map("unusualCircumstances" -> "test text")
      val result   = unusualCircumstancesController6015.submit(FakeRequest().withFormUrlEncodedBody(testData.toSeq*))

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        "/send-trade-and-cost-information/check-your-answers-about-the-trading-history"
      )
    }

  }
}
