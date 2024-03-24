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

import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import navigation.AboutTheTradingHistoryNavigator
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class BunkerFuelCardDetailsControllerSpec extends TestBaseSpec {

  val mockNavigator = mock[AboutTheTradingHistoryNavigator]

  def createBunkerFuelCardDetailsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory)
  ) =
    new BunkerFuelCardDetailsController(
      stubMessagesControllerComponents(),
      mockNavigator,
      bunkerFuelCardDetailsView,
      preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = createBunkerFuelCardDetailsController().show(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = createBunkerFuelCardDetailsController().show(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "return prefilled HTML" in {
      val result = createBunkerFuelCardDetailsController(prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails).show(Some(0))(fakeRequest)
      val html   = Jsoup.parse(contentAsString(result))
      Option(html.getElementById("name").`val`()).value      shouldBe "Card 1"
    }

  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = createBunkerFuelCardDetailsController().submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
