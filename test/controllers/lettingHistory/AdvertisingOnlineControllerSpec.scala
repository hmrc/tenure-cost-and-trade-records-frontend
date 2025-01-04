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
import models.submissions.lettingHistory.LettingHistory.hasOnlineAdvertising
import models.submissions.lettingHistory.{AdvertisingOnline, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.Result

import scala.concurrent.Future
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.advertisingOnline as AdvertisingOnlineView

class AdvertisingOnlineControllerSpec extends LettingHistoryControllerSpec:

  "the AdvertisingOnline controller" when {
    "the user session is fresh" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new FreshSessionFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${controllers.routes.TaskListController
            .show()
            .toString}" class="govuk-back-link"""")
        content                     should include("lettingHistory.advertisingOnline.heading")
        content                     should not include "checked"
      }
      "be handling invalid POST AdvertisingOnline=null and reply 400 with error message" in new FreshSessionFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "AdvertisingOnline" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("error.lettingHistory.advertisingOnline.required")
      }
      "be handling POST AdvertisingOnline='yes' and reply 303 redirect to the 'Advertising Online Details' page" in new FreshSessionFixture {
        val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("advertisingOnline" -> "yes"))
        status(result)                   shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe routes.AdvertisingOnlineDetailsController.show().url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        hasOnlineAdvertising(data).value shouldBe true
      }
    }
    "the user session is stale" should {
      "regardless of the given number online advertising" should {
        "be handling GET and reply 200 with the HTML form having checked radios" in new StaleSessionFixture(
          oneOnlineAdvertising
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          contentAsString(result)     should include("checked")
        }
        "be handling POST AdvertisingOnline='yes' and reply 303 redirect to the 'Advertising Online Details' page" in new StaleSessionFixture(
          oneOnlineAdvertising
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("advertisingOnline" -> "yes"))
          status(result)                   shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.AdvertisingOnlineDetailsController.show().url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasOnlineAdvertising(data).value shouldBe true
        }
        "be handling POST AdvertisingOnline='no' and reply 303 redirect to the 'CYA' page" in new StaleSessionFixture(
          oneOnlineAdvertising
        ) {
          val result: Future[Result] =
            controller.submit(fakePostRequest.withFormUrlEncodedBody("advertisingOnline" -> "no"))
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe controllers.routes.TaskListController.show().url // TODO!!!
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasOnlineAdvertising(data).value shouldBe false
        }
      }
    }
  }

  // It represents the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new AdvertisingOnlineController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[AdvertisingOnlineView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(advertisingOnline: List[AdvertisingOnline])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new AdvertisingOnlineController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[AdvertisingOnlineView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Option(
          LettingHistory(
            advertisingOnline = Some(advertisingOnline.nonEmpty),
            advertisingOnlineDetails = advertisingOnline
          )
        )
      ),
      repository
    )
