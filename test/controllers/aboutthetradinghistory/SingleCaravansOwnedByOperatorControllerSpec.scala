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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class SingleCaravansOwnedByOperatorControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.GrossReceiptsCaravanFleetHireController.show().url
  private val nextPage     = aboutthetradinghistory.routes.SingleCaravansAgeCategoriesController.show().url

  def singleCaravansOwnedByOperatorController =
    new SingleCaravansOwnedByOperatorController(
      caravansTrading6045View,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  "GET /" should {
    "return 200" in {
      val result = singleCaravansOwnedByOperatorController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = singleCaravansOwnedByOperatorController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = singleCaravansOwnedByOperatorController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormDataPerYear(idx: Int, weeks: Int = 52): Seq[(String, String)] =
    Seq(
      s"turnover[$idx].weeks"         -> weeks.toString,
      s"turnover[$idx].grossReceipts" -> (idx * 1000).toString,
      s"turnover[$idx].vans"          -> "777"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidWeeksFormData: Seq[(String, String)] =
    validFormDataPerYear(0, 53) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = singleCaravansOwnedByOperatorController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid weeks" in {
      val res = singleCaravansOwnedByOperatorController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidWeeksFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include("""<a href="#turnover[0].weeks">error.weeksMapping.invalid</a>""")
    }

    "return 400 for empty turnoverSections" in {
      val res = singleCaravansOwnedByOperatorController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
