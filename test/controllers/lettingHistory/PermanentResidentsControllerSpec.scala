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
import models.submissions.common.{AnswerNo, AnswerYes}
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import models.submissions.lettingHistory.LettingHistory.{hasPermanentResidents, permanentResidents}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.permanentResidents as PermanentResidentsView

class PermanentResidentsControllerSpec extends LettingHistoryControllerSpec:

  "the PermanentResidents controller" when {
    "the user session is fresh" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new FreshSessionFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${controllers.routes.TaskListController
            .show()
            .withFragment("lettingHistory")
            .toString}" class="govuk-back-link"""")
        content                     should include("lettingHistory.permanentResidents.heading")
        content                     should not include "checked"
      }
      "be handling invalid POST hasPermanentResidents=null and reply 400 with error message" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.hasPermanentResidents.error")
      }
      "be handling POST hasPermanentResidents='yes' and reply 303 redirect to the 'Residents Details' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
        status(result)                    shouldBe SEE_OTHER
        redirectLocation(result).value    shouldBe routes.ResidentDetailController.show().url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        hasPermanentResidents(data).value shouldBe true
      }
    }
    "the user session is stale" should {
      "regardless of the given number of residents"          should {
        "be handling GET and reply 200 with the HTML form having checked radios" in new StaleSessionFixture(
          oneResident
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          contentAsString(result)     should include("checked")
        }
        "be handling POST hasPermanentResidents='yes' and reply 303 redirect to the 'Resident Detail' page" in new StaleSessionFixture(
          oneResident
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                    shouldBe SEE_OTHER
          redirectLocation(result).value    shouldBe routes.ResidentDetailController.show().url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value shouldBe true
        }
        "be handling POST hasPermanentResidents='no' and reply 303 redirect to the 'Commercial Lettings' page" in new StaleSessionFixture(
          oneResident
        ) {
          // Answering 'no' will clear out all residents details
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "no"))
          status(result)                    shouldBe SEE_OTHER
          redirectLocation(result).value    shouldBe routes.CompletedLettingsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value shouldBe false
          permanentResidents(data)          shouldBe Nil
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling POST hasPermanentResidents='yes' and reply 303 redirect to the 'Resident List' page" in new StaleSessionFixture(
          fiveResidents
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                    shouldBe SEE_OTHER
          redirectLocation(result).value    shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value shouldBe true
          permanentResidents(data)            should have size 5
        }
      }
    }
  }

  // It represents the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new PermanentResidentsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[PermanentResidentsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(permanentResidents: List[ResidentDetail])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new PermanentResidentsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[PermanentResidentsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(permanentResidents.nonEmpty),
            permanentResidents = permanentResidents
          )
        )
      ),
      repository
    )
