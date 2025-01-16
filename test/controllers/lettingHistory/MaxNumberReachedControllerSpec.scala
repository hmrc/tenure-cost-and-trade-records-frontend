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

import models.submissions.lettingHistory.LettingHistory
import navigation.LettingHistoryNavigator
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers._
import views.html.lettingHistory.maxNumberReached as MaxNumberReachedView

class MaxNumberReachedControllerSpec extends LettingHistoryControllerSpec:

  "the MaxNumberReached controller" when {
    "the user has not provided any answer yet"        should {
      "and the journey comes from the 'Resident List' page"    should {
        "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
          val result = controller.show(kind = "permanentResidents")(fakeGetRequest)

          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.heading              shouldBe "lettingHistory.maxNumberReached.permanentResidents.heading"
          page.backLink             shouldBe routes.ResidentListController.show.url
          page.checkbox("understood") should notBeChecked
        }
      }
      "and the journey comes from the 'Occupier List' page"    should {
        "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
          val result = controller.show(kind = "temporaryOccupiers")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.heading              shouldBe "lettingHistory.maxNumberReached.temporaryOccupiers.heading"
          page.backLink             shouldBe routes.OccupierListController.show.url
          page.checkbox("understood") should notBeChecked
        }
      }
      "and the journey comes from the 'Advertising List' page" should {
        "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
          val result = controller.show(kind = "onlineAdvertising")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.heading              shouldBe "lettingHistory.maxNumberReached.onlineAdvertising.heading"
          page.backLink             shouldBe routes.AdvertisingListController.show.url
          page.checkbox("understood") should notBeChecked
        }
      }
    }
    "the user has already provided an answer"         should {
      "and the journey comes from the 'Resident List' page"    should {
        "be handling GET by replying 200 with the HTML form having already checked radios" in new ControllerFixture(
          mayHaveMorePermanentResidents = Some(true)
        ) {
          val result = controller.show(kind = "permanentResidents")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.backLink             shouldBe routes.ResidentListController.show.url
          page.checkbox("understood") should beChecked
        }
      }
      "and the journey comes from the 'Occupier List' page"    should {
        "be handling GET by replying 200 with the HTML form having already checked radios" in new ControllerFixture(
          mayHaveMoreCompletedLettings = Some(true)
        ) {
          val result = controller.show(kind = "temporaryOccupiers")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.backLink             shouldBe routes.OccupierListController.show.url
          page.checkbox("understood") should beChecked
        }
      }
      "and the journey comes from the 'Advertising List' page" should {
        "be handling GET by replying 200 with the HTML form having already checked radios" in new ControllerFixture(
          mayHaveMoreAdvertisingOnline = Some(true)
        ) {
          val result = controller.show(kind = "onlineAdvertising")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.backLink             shouldBe routes.AdvertisingListController.show.url
          page.checkbox("understood") should beChecked
        }
      }
    }
    "regardless of what the user might have answered" should {
      "be handling GET by replying 200 with the HTML form having unchecked radios despite unknown kind" in new ControllerFixture {
        val result = controller.show(kind = "unknown")(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.backLink             shouldBe controllers.routes.TaskListController.show().withFragment("lettingHistory").toString
        page.checkbox("understood") should notBeChecked
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(kind = "whatever")(
          fakePostRequest
            .withFormUrlEncodedBody("understood" -> "") // understood is missing!
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("understood") shouldBe "error.boolean"
      }
      "and the journey comes from the 'Resident List' page"    should {
        "be handling POST kind=permanentResidents&understood=false by replying 400 and display error message" in new ControllerFixture {
          val result = controller.submit(kind = "permanentResidents")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe BAD_REQUEST
          val page   = contentAsJsoup(result)
          page.error("understood") shouldBe "lettingHistory.maxNumberReached.understanding.required"
        }
        "be handling POST kind=permanentResidents&understood=true by replying 303 redirect to the 'Has Completed Lettings' page" in new ControllerFixture {
          val result = controller.submit(kind = "permanentResidents")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "true")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.HasCompletedLettingsController.show.url
        }
      }
      "and the journey comes from the 'Occupier List' page"    should {
        "be handling POST kind=temporaryOccupiers&understand=false by replying 400  display error message" in new ControllerFixture {
          val result = controller.submit(kind = "temporaryOccupiers")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe BAD_REQUEST
          val page   = contentAsJsoup(result)
          page.error("understood") shouldBe "lettingHistory.maxNumberReached.understanding.required"
        }
        "be handling POST kind=temporaryOccupiers&understood=true by replying 303 redirect to the 'How Many Nights' page" in new ControllerFixture {
          val result = controller.submit(kind = "temporaryOccupiers")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "true")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.HowManyNightsController.show.url
        }
      }
      "and the journey comes from the 'Advertising List' page" should {
        "be handling POST kind=onlineAdvertising&understand=false by replying 400 and display error message" in new ControllerFixture {
          val result = controller.submit(kind = "onlineAdvertising")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe BAD_REQUEST
          val page   = contentAsJsoup(result)
          page.error("understood") shouldBe "lettingHistory.maxNumberReached.understanding.required"
        }
        "be handling POST kind=onlineAdvertising&understood=true by replying 303 redirect to the '???' page" in new ControllerFixture {
          pending
          val result = controller.submit(kind = "onlineAdvertising")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "true")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe "/path/to/???"
        }
      }
    }
  }

  trait ControllerFixture(
    mayHaveMorePermanentResidents: Option[Boolean] = None,
    mayHaveMoreCompletedLettings: Option[Boolean] = None,
    mayHaveMoreAdvertisingOnline: Option[Boolean] = None
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new MaxNumberReachedController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[MaxNumberReachedView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            mayHaveMorePermanentResidents = mayHaveMorePermanentResidents,
            mayHaveMoreCompletedLettings = mayHaveMoreCompletedLettings,
            mayHaveMoreOnlineAdvertising = mayHaveMoreAdvertisingOnline
          )
        )
      ),
      repository
    )
