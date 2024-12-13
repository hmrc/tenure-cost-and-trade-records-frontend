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
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.residentList as ResidentListView

class ResidentListControllerSpec extends LettingHistoryControllerSpec:

  "the ResidentList controller" when {
    "the user session is fresh"      should {
      "be handling GET /list by replying 200 with the form showing an empty list of residents" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading     shouldBe "lettingHistory.residentList.heading.plural"
        page.backLink    shouldBe routes.PermanentResidentsController.show.url
        page.summaryList shouldBe empty
      }
      "be handling GET /remove?index=0 by replying redirect to the 'Resident List' page" in new ControllerFixture {
        val result = controller.remove(index = 0)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentListController.show.url
      }
      "be handling POST /remove?index=0 by replying redirect to the 'Resident List' page" in new ControllerFixture {
        val result = controller.performRemove(index = 0)(fakePostRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentListController.show.url
        verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
      }
      "be handling POST /list?hasMoreResidents=yes by replying redirect to the 'Resident Detail' page" in new ControllerFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentDetailController.show().url
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET /list and reply 200 by showing the list of known residents" in new ControllerFixture(
          oneResident
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.summaryList   shouldNot be(empty)
          page.summaryList(0) shouldBe oneResident.head.name
        }
        "be handling GET /remove?index=0 by replying 200 with the 'Confirm remove' page" in new ControllerFixture(
          oneResident
        ) {
          val result = controller.remove(index = 0)(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.submitAction shouldBe routes.ResidentListController.performRemove(0).url
        }
        "be handling invalid POST /remove?index=0 by replying 400 with error messages" in new ControllerFixture(
          oneResident
        ) {
          val result = controller.performRemove(index = 0)(fakePostRequest) // genericRemoveConfirmation is missing
          status(result) shouldBe BAD_REQUEST
          val page = contentAsJsoup(result)
          page.error("genericRemoveConfirmation") shouldBe "error.confirmableAction.required"
          verify(repository, never).saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])
        }
        "be handling confirmation POST /remove?index=0 by actually removing the resident and then replying redirect to the 'Resident List' page" in new ControllerFixture(
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
        "be handling denying POST /remove?index=0 by replying redirect to the 'Resident List' page" in new ControllerFixture(
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
        "be handling invalid POST /list by replying 400 with error messages" in new ControllerFixture(
          fiveResidents
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "" // yes or no is missing!
            )
          )
          status(result) shouldBe BAD_REQUEST
          val page   = contentAsJsoup(result)
          page.error("answer") shouldBe "lettingHistory.residentList.hasMoreResidents.required"
        }
        "be handling POST /list?hasMoreResidents=yes by replying redirect to the 'Max Number of Residents' page" in new ControllerFixture(
          fiveResidents
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.MaxNumberReachedController
            .show(kind = "permanentResidents")
            .url
        }
      }
    }
    "regardless of the user session" should {
      "be handling invalid POST /list by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // yes or no is missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.residentList.hasMoreResidents.required"
      }
      "be handling POST /list?hasMoreResidents=no by replying redirect to the 'Commercial Lettings' page" in new ControllerFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "no"))
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CompletedLettingsController.show.url
      }
    }
  }

  trait ControllerFixture(permanentResidents: List[ResidentDetail] = Nil)
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
            hasPermanentResidents = Some(permanentResidents.nonEmpty),
            permanentResidents = permanentResidents
          )
        )
      ),
      repository
    )
