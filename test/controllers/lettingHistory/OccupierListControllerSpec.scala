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
import models.submissions.lettingHistory.LettingHistory.completedLettings
import models.submissions.lettingHistory.{LettingHistory, OccupierDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.occupierList as OccupierListView

class OccupierListControllerSpec extends LettingHistoryControllerSpec:

  "the OccupierList controller" when {
    "the user session is fresh"      should {
      "be handling GET /list by replying 200 with the form showing an empty list of occupiers" in new FreshSessionFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.CompletedLettingsController.show.url}" class="govuk-back-link"""")
        content                     should include("""lettingHistory.occupierList.heading.plural""")
        content                     should include("""  <dl class="govuk-summary-list">
            |  </dl>""".stripMargin)
      }
      "be handling GET /remove?index=0 by replying redirect to the 'Occupiers List' page" in new FreshSessionFixture {
        val result = controller.remove(index = 0)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierListController.show.url
      }
      "be handling POST /remove?index=0 by replying redirect to the 'Occupier List' page" in new FreshSessionFixture {
        val result = controller.performRemove(index = 0)(fakePostRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierListController.show.url
        verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
      }
      "be handling POST /list?hadMoreOccupiers=yes by replying redirect to the 'Occupiers Detail' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierDetailController.show().url
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET /list and reply 200 by showing the list of known residents" in new StaleSessionFixture(
          oneOccupier
        ) {
          val result  = controller.show(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include(oneOccupier.head.name)
        }
        "be handling GET /remove?index=0 by replying 200 with the 'Confirm remove' page" in new StaleSessionFixture(
          oneOccupier
        ) {
          val result = controller.remove(index = 0)(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val content = contentAsString(result)
          content should include(s"""action="${routes.OccupierListController.performRemove(0)}"""")
        }
        "be handling invalid POST /remove?index=0 by replying 400 with error messages" in new StaleSessionFixture(
          oneOccupier
        ) {
          val result = controller.performRemove(index = 0)(fakePostRequest) // genericRemoveConfirmation is missing
          status(result)        shouldBe BAD_REQUEST
          contentAsString(result) should include("error.confirmableAction.required")
          verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
        }
        "be handling confirmation POST /remove?index=0 by actually removing the occupier and then replying redirect to the 'Occupier List' page" in new StaleSessionFixture(
          oneOccupier
        ) {
          // Confirm the removal of the resident at index 0 (who is "Mr. One")
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.OccupierListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          completedLettings(data)        shouldBe empty // instead of having size 1
        }
        "be handling denying POST /remove?index=0 by replying redirect to the 'Occupier List' page" in new StaleSessionFixture(
          oneOccupier
        ) {
          // Deny the removal of the resident at index 0 (who is "Mr. One")
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.OccupierListController.show.url
          verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling invalid POST /list by replying 400 with error messages" in new StaleSessionFixture(
          fiveOccupiers
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "" // yer or no is missing!
            )
          )
          status(result) shouldBe BAD_REQUEST
          contentAsString(result) should include("lettingHistory.occupierList.hadMoreOccupiers.required")
        }
        "be handling POST /list?hadMoreOccupiers=yes by replying redirect to the 'Max Number of Occupiers' page" in new StaleSessionFixture(
          fiveOccupiers
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.MaxNumberReachedController
            .show(kind = "temporaryOccupiers")
            .url
        }
      }
    }
    "regardless of the user session" should {
      "be handling invalid POST /list by replying 400 with error messages" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // yes or no is missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.occupierList.hadMoreOccupiers.required")
      }
      "be handling POST /list?hadMoreOccupiers=no by replying redirect to the 'Letting intention' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "no"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/path/to/intended-nights"
      }
    }
  }

  // It provides the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new OccupierListController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theListView = inject[OccupierListView],
      theConfirmationView = inject[RemoveConfirmationView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(completedLettings: List[OccupierDetail])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new OccupierListController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theListView = inject[OccupierListView],
      theConfirmationView = inject[RemoveConfirmationView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = Some(completedLettings.nonEmpty),
            completedLettings = completedLettings
          )
        )
      ),
      repository
    )
