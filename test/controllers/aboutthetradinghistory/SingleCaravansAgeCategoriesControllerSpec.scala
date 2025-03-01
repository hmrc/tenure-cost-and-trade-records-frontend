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
import controllers.aboutthetradinghistory
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class SingleCaravansAgeCategoriesControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.SingleCaravansSubletController.show().url

  val mockAudit: Audit                      = mock[Audit]
  def singleCaravansAgeCategoriesController =
    new SingleCaravansAgeCategoriesController(
      caravansAgeCategoriesView,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents(),
      mockAudit
    )

  "GET /" should {
    "return 200" in {
      val result = singleCaravansAgeCategoriesController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = singleCaravansAgeCategoriesController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = singleCaravansAgeCategoriesController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  "SUBMIT /" should {
    "return 400 for empty form data" in {
      val res = singleCaravansAgeCategoriesController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
