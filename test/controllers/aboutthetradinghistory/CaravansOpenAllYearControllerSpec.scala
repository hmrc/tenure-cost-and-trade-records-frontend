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
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CaravansOpenAllYearControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.StaticCaravansController.show().url

  private val nextPage = aboutthetradinghistory.routes.GrossReceiptsCaravanFleetHireController.show().url

  def caravansOpenAllYearController =
    new CaravansOpenAllYearController(
      caravansOpenAllYearView,
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
      val result = caravansOpenAllYearController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = caravansOpenAllYearController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = caravansOpenAllYearController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  "SUBMIT /" should {
    "save the form data and redirect to the next page on answer Yes" in {
      val res = caravansOpenAllYearController.submit(
        fakePostRequest.withFormUrlEncodedBody("areCaravansOpenAllYear" -> AnswerYes.name)
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "save the form data and redirect to the next page on answer No with weeks field filled" in {
      val res = caravansOpenAllYearController.submit(
        fakePostRequest.withFormUrlEncodedBody("areCaravansOpenAllYear" -> AnswerNo.name, "weeksPerYear" -> "33")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 on answer No and empty weeks field" in {
      val res = caravansOpenAllYearController.submit(
        fakePostRequest.withFormUrlEncodedBody("areCaravansOpenAllYear" -> AnswerNo.name)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "return 400 for empty form data" in {
      val res = caravansOpenAllYearController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
