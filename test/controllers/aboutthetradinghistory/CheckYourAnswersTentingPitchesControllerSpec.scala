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

import models.ForType.*
import navigation.AboutTheTradingHistoryNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.twirl.api.HtmlFormat
import test.ControllerSpec
import views.html.taskList.taskList

class CheckYourAnswersTentingPitchesControllerSpec extends ControllerSpec:

  private val mockAboutTheTradingHistoryNavigator = mock[AboutTheTradingHistoryNavigator]
  private val mockTaskListView                    = mock[taskList]

  when(mockTaskListView()(using any, any)).thenReturn(HtmlFormat.empty)

  private val checkYourAnswersTentingPitchesController = CheckYourAnswersTentingPitchesController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersTentingPitchesView,
    preEnrichedActionRefiner(
      referenceNumber = "99996045333",
      forType = FOR6045,
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOneCYA6045)
    ),
    mockSessionRepository
  )

  private val checkYourAnswersTentingPitchesControllerYesTent = CheckYourAnswersTentingPitchesController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersTentingPitchesView,
    preEnrichedActionRefiner(
      referenceNumber = "99996045333",
      forType = FOR6045,
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOneTentYes)
    ),
    mockSessionRepository
  )

  private val checkYourAnswersTentingPitchesControllerNoTent = CheckYourAnswersTentingPitchesController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersTentingPitchesView,
    preEnrichedActionRefiner(
      referenceNumber = "99996045333",
      forType = FOR6045,
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOneTentNo)
    ),
    mockSessionRepository
  )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersTentingPitchesController.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = checkYourAnswersTentingPitchesController.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 yes tent" in {
      val result = checkYourAnswersTentingPitchesControllerYesTent.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML yes tent" in {
      val result = checkYourAnswersTentingPitchesControllerYesTent.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 no tent" in {
      val result = checkYourAnswersTentingPitchesControllerNoTent.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML no tent" in {
      val result = checkYourAnswersTentingPitchesControllerNoTent.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersTentingPitchesController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
