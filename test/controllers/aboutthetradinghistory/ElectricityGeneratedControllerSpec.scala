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

class ElectricityGeneratedControllerSpec extends TestBaseSpec {

  def electricityGeneratedController =
    new ElectricityGeneratedController(
      stubMessagesControllerComponents(),
      aboutYourTradingHistoryNavigator,
      electricityGenerated6076View,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = electricityGeneratedController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = electricityGeneratedController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include("/financial-year-end")
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = electricityGeneratedController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include "/financial-year-end"
    }
  }

  private def electricityGeneratedForYear(idx: Int, weeks: Int = 52): Seq[(String, String)] =
    Seq(
      s"turnover[$idx].weeks"                -> weeks.toString,
      s"turnover[$idx].electricityGenerated" -> s"${(idx + 1) * 1000} MWh"
    )

  private def electricityGeneratedFormData: Seq[(String, String)] =
    electricityGeneratedForYear(0) ++
      electricityGeneratedForYear(1) ++
      electricityGeneratedForYear(2)

  private def invalidWeeksFormData: Seq[(String, String)] =
    electricityGeneratedForYear(0, 53) ++
      electricityGeneratedForYear(1) ++
      electricityGeneratedForYear(2)

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = electricityGeneratedController.submit(
        fakePostRequest.withFormUrlEncodedBody(electricityGeneratedFormData: _*)
      )
      status(res)           shouldBe Status.SEE_OTHER
      redirectLocation(res) shouldBe Some(aboutthetradinghistory.routes.GrossReceiptsExcludingVATController.show().url)
    }

    "return 400 and error message for invalid weeks" in {
      val res = electricityGeneratedController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidWeeksFormData: _*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include("""<a href="#turnover[0].weeks">error.weeksMapping.invalid</a>""")
    }

    "return 400 for empty turnoverSections" in {
      val res = electricityGeneratedController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
