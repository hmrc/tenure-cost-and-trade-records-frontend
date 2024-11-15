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
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.residentList as ResidentListView

class ResidentListControllerSpec extends LettingHistoryControllerSpec:

  "the ResidentList controller" when {
    "the user session is fresh"      should {
      "be handling GET /list by replying 200 with the form showing an empty list of residents" in new FreshSessionFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        contentAsString(result)     should include("""  <dl class="govuk-summary-list">
                                                     |  </dl>""".stripMargin)
      }
      "be handling GET /remove?index=0 by replying redirect to the 'Resident List' page" in new FreshSessionFixture {
        val result = controller.remove(index = 0)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentListController.show.url
      }
      "be handling POST /remove?index=0 by replying redirect to the 'Resident List' page" in new FreshSessionFixture {
        val result = controller.performRemove(index = 0)(fakePostRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentListController.show.url
        verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
      }
      "be handling POST /list?hasMoreResidents=yes by replying redirect to the 'Resident Detail' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasMoreResidents" -> "yes"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentDetailController.show().url
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET /list and reply 200 with the form showing the list of known residents" in new StaleSessionFixture(
          oneResident
        ) {
          val result  = controller.show(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include("Mr. One")
          content                     should include("Address One")
        }
        "be handling GET /remove?index=0 by replying 200 with the 'Confirm remove' page" in new StaleSessionFixture(
          oneResident
        ) {
          val result = controller.remove(index = 0)(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val content = contentAsString(result)
          content should include(s"""action="${routes.ResidentListController.performRemove(0)}"""")
        }
        "be handling invalid POST /remove?index=0 by replying 400 with error messages" in new StaleSessionFixture(
          oneResident
        ) {
          val result = controller.performRemove(index = 0)(fakePostRequest) // genericRemoveConfirmation is missing
          status(result)        shouldBe BAD_REQUEST
          contentAsString(result) should include("error.confirmableAction.required")
          verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
        }
        "be handling confirmation POST /remove?index=0 by actually removing the resident and then replying redirect to the 'Resident List' page" in new StaleSessionFixture(
          oneResident
        ) {
          // Confirm the removal of the resident at index 0 (who is "Mr. One")
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          permanentResidents(data)       shouldBe empty // instead of having size 1
        }
        "be handling denying POST /remove?index=0 by replying redirect to the 'Resident List' page" in new StaleSessionFixture(
          oneResident
        ) {
          // Deny the removal of the resident at index 0 (who is "Mr. One")
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.ResidentListController.show.url
          verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling invalid POST /list by replying 400 with error messages" in new StaleSessionFixture(
          fiveResidents
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "hasMoreResidents" -> ""
            )
          )
          status(result) shouldBe BAD_REQUEST
          contentAsString(result) should include("lettingHistory.residentList.hasMoreResidents.required")
        }
        "be handling POST /list?hasMoreResidents=yes by replying redirect to the 'Max Number of Residents' page" in new StaleSessionFixture(
          fiveResidents
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasMoreResidents" -> "yes"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe "/path/to/max-permanent-residents"
        }
      }
    }
    "regardless of the user session" should {
      "be handling invalid POST /list by replying 400 with error messages" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "hasMoreResidents" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.residentList.hasMoreResidents.required")
      }
      "be handling POST /list?hasMoreResidents=no by replying redirect to the 'Commercial Lettings' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("hasMoreResidents" -> "no"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CompletedLettingsController.show.url
      }
    }
  }

  // It provides the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new ResidentListController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theListView = inject[ResidentListView],
      theConfirmationView = inject[RemoveConfirmationView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(permanentResidents: List[ResidentDetail])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new ResidentListController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theListView = inject[ResidentListView],
      theConfirmationView = inject[RemoveConfirmationView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(if permanentResidents.isEmpty then AnswerNo else AnswerYes),
            permanentResidents = permanentResidents
          )
        )
      ),
      repository
    )
