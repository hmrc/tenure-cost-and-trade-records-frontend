/*
 * Copyright 2025 HM Revenue & Customs
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

import models.ForType.FOR6010
import models.Session
import play.api.test.Helpers.*
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}
import views.html.aboutthetradinghistory.checkYourAnswerNoFinancialYears as CheckYourAnswerNoFinancialYearsView

import scala.concurrent.Future.successful

class CheckYourAnswersNoFinancialYearsControllerSpec extends TestBaseSpec with JsoupHelpers:

  trait ControllerFixture:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any)(using any)).thenReturn(successful(()))

    val controller = new CheckYourAnswersNoFinancialYearsController(
      mcc = stubMessagesControllerComponents(),
      navigator = aboutYourTradingHistoryNavigator,
      theView = inject[CheckYourAnswerNoFinancialYearsView],
      sessionRefiner = preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory),
        forType = FOR6010
      ),
      repository = repository
    )

  "the CheckYourAnswersNoFinancialYears controller" when {
    "handling GET / requests"  should {
      "reply 200 with unchecked form" in new ControllerFixture {
        val result = controller.show()(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading  shouldBe "checkYourAnswersAboutTheTradingHistory.heading"
        page.backLink shouldBe routes.FinancialYearEndController.show().url
      }
    }
    "handling POST / requests" should {
      "reply with 400 with form errors" in new ControllerFixture {
        val result = controller.submit()(
          fakePostRequest.withFormUrlEncodedBody(
            "correct"   -> "", // missing
            "completed" -> "" // missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.errorMessage("correct")   shouldBe "error.checkYourAnswers.givenInformation.isCorrect"
        page.errorMessage("completed") shouldBe "error.checkYourAnswersRadio.required"
      }
      "reply with 303 redirect to the next page" in new ControllerFixture {
        val result = controller.submit()(
          fakePostRequest.withFormUrlEncodedBody(
            "correct"   -> "true",
            "completed" -> "true"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe controllers.routes.TaskListController
          .show()
          .withFragment("tradingHistory")
          .toString
        val newSession = captor[Session]
        verify(repository).saveOrUpdate(newSession.capture())(using any)
        newSession.getValue.aboutTheTradingHistory.value.checkYourAnswersAboutTheTradingHistory.value.checkYourAnswersAboutTheTradingHistory shouldBe "true"
      }
    }
  }
