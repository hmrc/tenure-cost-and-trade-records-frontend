/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class StaffCostsControllerSpec extends ControllerSpec:

  val mockAudit: Audit = mock[Audit]

  def staffCostsController: StaffCostsController =
    StaffCostsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      staffCostsView,
      preEnrichedActionRefiner(
        aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes),
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepository
    )

  def staffCostsBaseloadController: StaffCostsController =
    StaffCostsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      staffCostsView,
      preEnrichedActionRefiner(
        aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYesBaseload),
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepository
    )

  "GET /" should {
    "return 200" in {
      val result = staffCostsController.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = staffCostsController.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = staffCostsController.show(getRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include "/financial-year-end"
    }

    "return correct backLink when 'from=IES' query param is present" in {
      val result = staffCostsController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      )
    }

    "render back link to CYA if come from CYA Baseload" in {
      val result  = staffCostsBaseloadController.show(getRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include "/financial-year-end"
    }

    "return correct backLink when 'from=IES' query param is present Baseload" in {
      val result = staffCostsBaseloadController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      )
    }
  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = staffCostsController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }
