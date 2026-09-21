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

import models.ForType.FOR6010
import models.Session
import models.submissions.common.AnswersYesNo.*
import org.jsoup.nodes.Document
import org.mockito.ArgumentCaptor
import play.api.mvc.Result
import play.api.test.Helpers.*
import repositories.SessionRepo
import test.JsoupHelpers
import test.ControllerSpec
import views.html.aboutthetradinghistory.checkYourAnswerNoFinancialYears as CheckYourAnswerNoFinancialYearsView

import scala.concurrent.Future

class CheckYourAnswersNoFinancialYearsControllerSpec extends ControllerSpec with JsoupHelpers:

  trait ControllerFixture:
    val repository: SessionRepo = mock[SessionRepo]
    when(repository.saveOrUpdate(any)(using any)).thenReturn(Future.unit)

    def controller(emptyTurnoverSections: Boolean = false): CheckYourAnswersNoFinancialYearsController =
      CheckYourAnswersNoFinancialYearsController(
        mcc = stubMessagesControllerComponents(),
        navigator = aboutYourTradingHistoryNavigator,
        theView = inject[CheckYourAnswerNoFinancialYearsView],
        sessionRefiner = preEnrichedActionRefiner(
          aboutTheTradingHistory = Some(
            prefilledAboutYourTradingHistory.copy(
              turnoverSections =
                if emptyTurnoverSections
                then Seq.empty
                else prefilledAboutYourTradingHistory.turnoverSections
            )
          ),
          forType = FOR6010
        ),
        repository = repository
      )

  "GET /" should {
    "reply 200 with unchecked form" in new ControllerFixture {
      val result: Future[Result] = controller().show()(getRequest)
      status(result)          shouldBe OK
      contentType(result).get shouldBe HTML
      charset(result).get     shouldBe UTF8

      val page: Document = contentAsJsoup(result)
      page.heading  shouldBe "checkYourAnswersAboutTheTradingHistory.heading"
      page.backLink shouldBe routes.FinancialYearEndController.show().url
    }
  }

  "POST /" should {
    "reply with 303 redirect to the next page" in new ControllerFixture {
      val result: Future[Result] = controller().submit()(
        postRequest.withFormUrlEncodedBody(
          "correct"   -> "true",
          "completed" -> "yes"
        )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe controllers.routes.TaskListController.show.withFragment("tradingHistory").toString

      val newSession: ArgumentCaptor[Session] = captor[Session]
      verify(repository).saveOrUpdate(newSession.capture())(using any)
      newSession.getValue.aboutTheTradingHistory.get.checkYourAnswersAboutTheTradingHistory.get shouldBe AnswerYes
    }

    "eventually reset turnover section" in new ControllerFixture {
      val result: Future[Result] = controller(emptyTurnoverSections = true).submit()(
        postRequest.withFormUrlEncodedBody(
          "correct"   -> "true",
          "completed" -> "yes"
        )
      )
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe controllers.routes.TaskListController.show.withFragment("tradingHistory").toString

      val newSession: ArgumentCaptor[Session] = captor[Session]
      verify(repository).saveOrUpdate(newSession.capture())(using any)
      newSession.getValue.aboutTheTradingHistory.get.checkYourAnswersAboutTheTradingHistory.get shouldBe AnswerYes
    }
  }
