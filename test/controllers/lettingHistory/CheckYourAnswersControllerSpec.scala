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

package controllers.lettingHistory

import models.Session
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.checkYourAnswers.template as CheckYourAnswerView

class CheckYourAnswersControllerSpec extends LettingHistoryControllerSpec:

  "the CheckYourAnswers controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET by replying 200 with the empty form" in new ControllerFixture(
        lettingHistory = Some(
          LettingHistory(
            hasOnlineAdvertising = Some(false)
          )
        )
      ) {
        val result = controller.show(fakeGetRequest)
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.checkYourAnswers.heading"
        page.backLink          shouldBe routes.HasOnlineAdvertisingController.show.url
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveNoneChecked
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "error.checkYourAnswersRadio.required"
      }
    }
    "the user has already answered"            should {
      "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
        lettingHistory = Some(
          LettingHistory(
            hasOnlineAdvertising = Some(true),
            sectionCompleted = Some(true)
          )
        )
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveChecked("yes")
        page.radios("answer") shouldNot haveChecked("no")
      }
      "be handling POST answer='yes' by replying 303 redirect to the 'TaskList' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe controllers.routes.TaskListController
          .show()
          .withFragment("letting-history")
          .toString
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        sectionCompleted(data).value   shouldBe true
      }
    }
  }

  trait ControllerFixture(lettingHistory: Option[LettingHistory] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:

    val controller = new CheckYourAnswersController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[CheckYourAnswerView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = lettingHistory
      ),
      repository
    )
