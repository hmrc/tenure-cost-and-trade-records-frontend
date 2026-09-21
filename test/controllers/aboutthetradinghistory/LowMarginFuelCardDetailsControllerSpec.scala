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

import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import navigation.AboutTheTradingHistoryNavigator
import org.jsoup.Jsoup
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import test.ControllerSpec

class LowMarginFuelCardDetailsControllerSpec extends ControllerSpec:

  private val mockNavigator = mock[AboutTheTradingHistoryNavigator]

  private def createLowMarginFuelCardDetailsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory)
  ) =
    LowMarginFuelCardDetailsController(
      stubMessagesControllerComponents(),
      mockNavigator,
      lowMarginFuelCardsDetailsView,
      preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
      mockSessionRepository
    )

  "GET /" should {
    "return 200" in {
      val result = createLowMarginFuelCardDetailsController().show(None)(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = createLowMarginFuelCardDetailsController().show(None)(getRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "return prefilled HTML" in {
      val result =
        createLowMarginFuelCardDetailsController(prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails).show(
          Some(0)
        )(getRequest)
      val html   = Jsoup.parse(contentAsString(result))
      Option(html.getElementById("name").`val`()).get shouldBe "Low Margin Card"
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = createLowMarginFuelCardDetailsController().submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
