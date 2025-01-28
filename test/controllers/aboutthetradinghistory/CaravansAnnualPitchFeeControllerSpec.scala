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
import play.api.test.Helpers.*
import utils.TestBaseSpec

class CaravansAnnualPitchFeeControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  private val previousPage = aboutthetradinghistory.routes.CaravansPerServiceController.show().url

  private val nextPage = aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url

  def caravansAnnualPitchFeeController =
    new CaravansAnnualPitchFeeController(
      caravansAnnualPitchFeeView,
      mockAudit,
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
      val result = caravansAnnualPitchFeeController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = caravansAnnualPitchFeeController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = caravansAnnualPitchFeeController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormData: Seq[(String, String)] =
    Seq(
      "totalPitchFee"                 -> "11000",
      "servicesIncludedInPitchFee[0]" -> "electricity",
      "servicesIncludedInPitchFee[1]" -> "other",
      "electricity"                   -> "1000",
      "otherPitchFeeDetails"          -> "food - 2000, cleaning - 500"
    )

  private def invalidNumberFormData: Seq[(String, String)] =
    Seq(
      "totalPitchFee"                 -> "abcdef",
      "servicesIncludedInPitchFee[0]" -> "electricity",
      "servicesIncludedInPitchFee[1]" -> "other",
      "electricity"                   -> "1000",
      "otherPitchFeeDetails"          -> "food - 2000, cleaning - 500"
    )

  private def missedTotalPitchFee: Seq[(String, String)] =
    Seq(
      "servicesIncludedInPitchFee[0]" -> "electricity",
      "servicesIncludedInPitchFee[1]" -> "other",
      "electricity"                   -> "1000",
      "otherPitchFeeDetails"          -> "food - 2000, cleaning - 500"
    )

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = caravansAnnualPitchFeeController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid number" in {
      val res = caravansAnnualPitchFeeController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidNumberFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#totalPitchFee">error.caravans.totalPitchFee.nonNumeric</a>"""
      )
    }

    "return 400 and error message for missed totalPitchFee" in {
      val res = caravansAnnualPitchFeeController.submit(
        fakePostRequest.withFormUrlEncodedBody(missedTotalPitchFee*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#totalPitchFee">error.caravans.totalPitchFee.required</a>"""
      )
    }

    "return 400 for empty form data" in {
      val res = caravansAnnualPitchFeeController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
