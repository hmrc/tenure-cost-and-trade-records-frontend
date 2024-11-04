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
import models.submissions.common.{AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.LettingHistory
import navigation.LettingHistoryNavigator
import org.mockito.ArgumentCaptor
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import views.html.lettingHistory.permanentResidents as PermanentResidentsView

import scala.concurrent.Future.successful

class PermanentResidentsControllerSpec extends TestBaseSpec:

  "the PermanentResidents controller" when {
    "the user session is fresh" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new FreshSessionFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("lettingHistory.permanentResidents.heading")
        content                     should not include "checked"
      }
      "be handling POST isPermanentResident=null and reply 400 with error message" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest)
        status(result)        shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.isPermanentResidence.error")
      }
      "be handling POST isPermanentResident='yes' and reply 303 with redirect location of 'Residents Details' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("isPermanentResidence" -> "yes"))
        status(result)                   shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe "/path/to/residents-details"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        isPermanentResidenceOf(data).value should beAnswerYes
      }
    }
    "the user session is stale" should {
      "be handling GET and reply 200 with the HTML form having checked radios" in new StaleSessionFixture(
        isPermanentResidence = AnswerYes
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        contentAsString(result)     should include("checked")
      }
      "be handling POST isPermanentResident='no' and reply 303 with redirect location of 'Completed Commercial Lettings' page" in new StaleSessionFixture(
        isPermanentResidence = AnswerYes
      ) {
        // Update the form with a different answer
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("isPermanentResidence" -> "no"))
        status(result)                   shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe "/path/to/completed-commercial-lettings"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        isPermanentResidenceOf(data).value should beAnswerNo
      }
    }
  }

  trait MockRepositoryFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

  trait SessionCapturingFixture:
    def isPermanentResidenceOf(session: ArgumentCaptor[Session]) =
      LettingHistory.isPermanentResidence(session.getValue)

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
  trait StaleSessionFixture(isPermanentResidence: AnswersYesNo)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new PermanentResidentsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[PermanentResidentsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            isPermanentResidence = isPermanentResidence
          )
        )
      ),
      repository
    )

    // TODO test the case of session with letting history
