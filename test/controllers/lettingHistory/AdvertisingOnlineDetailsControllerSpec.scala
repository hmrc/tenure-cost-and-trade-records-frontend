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
import models.submissions.lettingHistory.LettingHistory.getAdvertisingOnlineDetails
import models.submissions.lettingHistory.{AdvertisingOnline, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.http.Status.*
import play.api.libs.json.Writes
import play.api.http.MimeTypes.HTML
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.advertisingOnlineDetails as AdvertisingOnlineDetailsView

class AdvertisingOnlineDetailsControllerSpec extends LettingHistoryControllerSpec:

  "the Advertising online details controller" when {
    "the user session is fresh"                        should {
      "be handling GET /detail by replying 200 with the form showing name and address fields" in new ControllerFixture {
        val result = controller.show(None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                        shouldBe "lettingHistory.advertisingOnlineDetails.heading"
        page.backLink                       shouldBe routes.AdvertisingOnlineController.show.url
        page.input("websiteAddress")          should beEmpty
        page.input("propertyReferenceNumber") should beEmpty
      }
      "be handling good POST /detail by replying 303 redirect to the 'Advertising Online List' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "websiteAddress"          -> "123.uk",
          "propertyReferenceNumber" -> "123abc"
        )
        val result  = controller.submit(None)(request)
        status(result)                       shouldBe SEE_OTHER
        redirectLocation(result).value       shouldBe routes.AdvertisingListController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        getAdvertisingOnlineDetails(data)      should have size 1
        getAdvertisingOnlineDetails(data)(0) shouldBe AdvertisingOnline(
          websiteAddress = "123.uk",
          propertyReferenceNumber = "123abc"
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"                              should {
        "be handling GET /detail?index=0 by replying 200 with the form pre-filled values" in new ControllerFixture(
          oneOnlineAdvertising
        ) {
          val result = controller.show(index = Some(0))(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.input("websiteAddress") should haveValue("123.com")

        }
        "be handling POST by replying 303 redirect to 'Advertising Online List' page" in new ControllerFixture(
          oneOnlineAdvertising
        ) {
          // Post an unknown resident detail and expect it to become the third resident
          val request = fakePostRequest.withFormUrlEncodedBody(
            "websiteAddress"          -> "test.pl",
            "propertyReferenceNumber" -> "1234ref"
          )
          val result  = controller.submit(None)(request)
          status(result)                                               shouldBe SEE_OTHER
          redirectLocation(result).value                               shouldBe routes.AdvertisingListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          getAdvertisingOnlineDetails(data)                              should have size 2 // instead of 1
          getAdvertisingOnlineDetails(data)(0)                         shouldBe oneOnlineAdvertising.head
          getAdvertisingOnlineDetails(data)(1).websiteAddress          shouldBe "test.pl"
          getAdvertisingOnlineDetails(data)(1).propertyReferenceNumber shouldBe "1234ref"
        }
        "be handling POST /detail?overwrite by replying 303 redirect to 'Advertising Online List' page" in new ControllerFixture(
          twoOnlineAdvertising
        ) {
          val request = fakePostRequest.withFormUrlEncodedBody(
            "websiteAddress"          -> twoOnlineAdvertising.last.websiteAddress,
            "propertyReferenceNumber" -> "otherReference123"
          )
          val result  = controller.submit(None)(request)
          status(result)                                               shouldBe SEE_OTHER
          redirectLocation(result).value                               shouldBe routes.AdvertisingListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          getAdvertisingOnlineDetails(data)                              should have size 3
          getAdvertisingOnlineDetails(data)(0)                         shouldBe oneOnlineAdvertising.head
          getAdvertisingOnlineDetails(data)(1).websiteAddress          shouldBe "456.com"
          getAdvertisingOnlineDetails(data)(1).propertyReferenceNumber shouldBe "aaa456"
        }
      }
      "and the maximum number of advertising online details has been reached" should {
        "be handling GET /detail by replying 303 redirect to the 'Advertising Online List' page" in new ControllerFixture(
          fiveOnlineAdvertising
        ) {
          val result = controller.show(index = None)(fakeGetRequest)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe controllers.routes.TaskListController.show().url // TODO !!!
        }
      }
    }
    "regardless of what the user might have submitted" should {
      "be handling invalid POST /detail by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(None)(
          fakePostRequest.withFormUrlEncodedBody(
            "websiteAddress"          -> "",
            "propertyReferenceNumber" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("websiteAddress") shouldBe "error.websiteAddressForProperty.required"
      }
    }
  }

  trait ControllerFixture(advertisingOnlineList: List[AdvertisingOnline] = Nil)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new AdvertisingOnlineDetailsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[AdvertisingOnlineDetailsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            advertisingOnline = Some(advertisingOnlineList.nonEmpty),
            advertisingOnlineDetails = advertisingOnlineList
          )
        )
      ),
      repository
    )
