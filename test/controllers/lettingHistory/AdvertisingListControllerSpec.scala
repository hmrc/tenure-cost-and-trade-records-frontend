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
import models.submissions.lettingHistory.LettingHistory.onlineAdvertising
import models.submissions.lettingHistory.{AdvertisingDetail, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.http.Status.*
import play.api.http.MimeTypes.HTML
import play.api.test.Helpers.{charset, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.advertisingList as AdvertisingList

import scala.language.implicitConversions

class AdvertisingListControllerSpec extends LettingHistoryControllerSpec:

  "the AdvertisingList controller" when {
    "the user session is fresh"      should {
      "be handling GET /list by replying 200 with the form showing an empty list of advertising details" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading     shouldBe "lettingHistory.advertisingList.heading.plural"
        // TODO page.backLink    shouldBe None
        page.summaryList shouldBe empty
      }
      "be handling GET /remove?index=0 by replying redirect to the 'Advertising Online List' page" in new ControllerFixture {
        val result = controller.remove(index = 0)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AdvertisingListController.show.url
      }
      "be handling POST /remove?index=0 by replying redirect to the 'Advertising Online List' page" in new ControllerFixture {
        val result = controller.performRemove(index = 0)(fakePostRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AdvertisingListController.show.url
        verify(repository, never).saveOrUpdate(any[Session])(using any[HeaderCarrier])
      }
      "be handling POST /list?hasMoreAdvertisingDetails=yes by replying redirect to the 'Advertising Online Detail' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AdvertisingDetailController.show().url
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"                 should {
        "be handling GET /list and reply 200 by showing the list of known online advertising details" in new ControllerFixture(
          oneAdvertising
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF8
          val page = contentAsJsoup(result)
          page.summaryList     shouldNot be(empty)
          page.summaryList.head shouldBe oneAdvertising.head.websiteAddress
        }
        "be handling GET /remove?index=0 by replying 200 with the 'Confirm remove' page" in new ControllerFixture(
          oneAdvertising
        ) {
          val result = controller.remove(index = 0)(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF8
          val page = contentAsJsoup(result)
          page.submitAction shouldBe routes.AdvertisingListController.performRemove(0).url
        }
        "be handling invalid POST /remove?index=0 by replying 400 with error messages" in new ControllerFixture(
          oneAdvertising
        ) {
          val result = controller.performRemove(index = 0)(fakePostRequest)
          status(result) shouldBe BAD_REQUEST
          val page = contentAsJsoup(result)
          page.error("genericRemoveConfirmation") shouldBe "error.confirmableAction.required"
          verify(repository, never).saveOrUpdate(any[Session])(using any[HeaderCarrier])
        }
        "be handling confirmation POST /remove?index=0 by actually removing the advertising detailsand then replying redirect to the 'Advertising List' page" in new ControllerFixture(
          oneAdvertising
        ) {
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.AdvertisingListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          onlineAdvertising(data)        shouldBe empty
        }
        "be handling denying POST /remove?index=0 by replying redirect to the 'Advertising List' page" in new ControllerFixture(
          oneAdvertising
        ) {
          val result = controller.performRemove(index = 0)(
            fakePostRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.AdvertisingListController.show.url
          verify(repository, never).saveOrUpdate(any[Session])(using any[HeaderCarrier])
        }
      }
      "and the maximum number of web addresses has been reached" should {
        "be handling invalid POST /list by replying 400 with error messages" in new ControllerFixture(
          fiveAdvertisings
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "" // missing
            )
          )
          status(result) shouldBe BAD_REQUEST
          val page   = contentAsJsoup(result)
          page.error("answer") shouldBe "lettingHistory.advertisingList.hasMoreWebsites.required"
        }
        "be handling POST /list?hasMoreAdvertisingDetails=yes by replying redirect to the 'Max Number of Advertising' page" in new ControllerFixture(
          fiveAdvertisings
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.MaxNumberReachedController
            .show(kind = "onlineAdvertising")
            .url
        }
      }
    }
    "regardless of the user session" should {
      "be handling invalid POST /list by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.advertisingList.hasMoreWebsites.required"
      }
      "be handling POST /list?hasMoreAdvertisingDetails=no by replying redirect to the CYA page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "no"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CheckYourAnswersLettingHistoryController.show.url
      }
    }
  }

  trait ControllerFixture(list: List[AdvertisingDetail] = Nil)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new AdvertisingListController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theListView = inject[AdvertisingList],
      theConfirmationView = inject[RemoveConfirmationView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasOnlineAdvertising = Some(list.nonEmpty),
            onlineAdvertising = list
          )
        )
      ),
      repository
    )
