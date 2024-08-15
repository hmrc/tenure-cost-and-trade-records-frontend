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

class CaravansPerServiceControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.CaravansTotalSiteCapacityController.show().url

  private val nextPage = aboutthetradinghistory.routes.CaravansAnnualPitchFeeController.show().url

  def caravansPerServiceController =
    new CaravansPerServiceController(
      caravansPerServiceView,
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
      val result = caravansPerServiceController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = caravansPerServiceController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = caravansPerServiceController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormData: Seq[(String, String)] =
    Seq(
      "fleetWaterElectricityDrainage"   -> "111",
      "fleetElectricityOnly"            -> "222",
      "privateWaterElectricityDrainage" -> "333",
      "privateElectricityOnly"          -> "444"
    )

  private def invalidNumberFormData: Seq[(String, String)] =
    Seq(
      "fleetWaterElectricityDrainage"   -> "abcdef",
      "fleetElectricityOnly"            -> "222",
      "privateWaterElectricityDrainage" -> "333",
      "privateElectricityOnly"          -> "444"
    )

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = caravansPerServiceController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid number" in {
      val res = caravansPerServiceController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidNumberFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#fleetWaterElectricityDrainage">error.caravans.fleetWaterElectricityDrainage.nonNumeric</a>"""
      )
    }

    "return 400 for empty form data" in {
      val res = caravansPerServiceController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
