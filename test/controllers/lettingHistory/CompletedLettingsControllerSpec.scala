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

package controllers.lettingHistory

import models.Session
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.completedLettings as CompletedLettingsView

class CompletedLettingsControllerSpec extends LettingHistoryControllerSpec:

  "the CompletedLettings controller" when {
    "the user session is fresh" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new FreshSessionFixture {
        val result  = controller.show(fakeGetRequest.withSession("from" -> "permanentResidentsPage"))
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.PermanentResidentsController.show.url}" class="govuk-back-link"""")
        content                     should include("lettingHistory.completedLettings.heading")
        content                     should not include "checked"
      }
      "be handling invalid POST hasCompletedLettings=null and reply 400 with error message" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "hasCompletedLettings" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.hasCompletedLettings.required")
      }
      "be handling POST hasCompletedLettings='yes' and reply 303 redirect to 'Occupier Detail' page" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "hasCompletedLettings" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierDetailController.show(index = None).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        hasCompletedLettings(data).value should beAnswerYes
      }
    }
    "the user session is stale" should {
      "regardless of the given number of occupiers"          should {
        "be handling GET and reply 200 with the HTML form having checked radios" in new StaleSessionFixture(
          hasCompletedLettings = AnswerYes
        ) {
          val result  = controller.show(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include(s"""${routes.ResidentListController.show.url}" class="govuk-back-link"""")
          content                     should include("checked")
        }
        "be handling POST hasCompletedLettings='yes' and reply 303 redirect to the 'Occupier Detail' page" in new StaleSessionFixture(
          hasCompletedLettings = AnswerNo
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasCompletedLettings" -> "yes"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.OccupierDetailController.show(index = None).url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value should beAnswerYes
        }
        "be handling POST hasCompletedLettings='no' and reply 303 redirect to the 'Letting intention' page" in new StaleSessionFixture(
          hasCompletedLettings = AnswerYes
        ) {
          // Answering 'no' will clear out all residents details
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasCompletedLettings" -> "no"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe "/path/to/how-many-nights"
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value should beAnswerNo
          // TODO completedLettings(data)       shouldBe Nil
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling POST hasCompletedLettings='yes' and reply 303 redirect to the 'Occupiers List' page" in new StaleSessionFixture(
          hasCompletedLettings = AnswerYes
        ) {
          pending
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasCompletedLettings" -> "yes"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe "/path/to/occupier-list"
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value should beAnswerYes
          // TODO completedLettings(data) should have size 5
        }
      }
    }
  }

  // It represents the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new CompletedLettingsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[CompletedLettingsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(hasCompletedLettings: AnswersYesNo)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new CompletedLettingsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[CompletedLettingsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(AnswerYes),
            permanentResidents = List(
              ResidentDetail(name = "Mr. One", address = "1, Street")
            ),
            hasCompletedLettings = Some(hasCompletedLettings)
          )
        )
      ),
      repository
    )
