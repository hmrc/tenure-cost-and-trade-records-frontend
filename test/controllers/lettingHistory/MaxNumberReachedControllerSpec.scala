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
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory._
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.maxNumberReached as MaxNumberReachedView

class MaxNumberReachedControllerSpec extends LettingHistoryControllerSpec:

  "the MaxNumberReached controller" when {
    "the user has not provided any answer yet"        should {
      "and the journey comes from the 'Resident List' page" should {
        "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
          val result = controller.show(kind = "permanentResidents")(fakeGetRequest)

          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.heading            shouldBe "lettingHistory.maxNumberReached.permanentResidents.heading"
          page.backLink           shouldBe routes.ResidentListController.show.url
          page.radios("understood") should haveNoneChecked
        }
        "be handling GET by replying 200 with the HTML form having unchecked radios despite unknown kind" in new ControllerFixture {
          val result = controller.show(kind = "unknown")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.backLink           shouldBe controllers.routes.TaskListController.show().withFragment("lettingHistory").toString
          page.radios("understood") should haveNoneChecked
        }
      }
      "and the journey comes from the 'Occupier List' page" should {
        "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
          val result = controller.show(kind = "temporaryOccupiers")(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.heading            shouldBe "lettingHistory.maxNumberReached.temporaryOccupiers.heading"
          page.backLink           shouldBe routes.OccupierListController.show.url
          page.radios("understood") should haveNoneChecked
        }
      }
    }
    "the user has already provided an answer"         should {
      "and the journey comes from the 'Resident List' page" should {
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
        "be handling POST understood=false by replying 303 redirect to 'How many nights' page" in new ControllerFixture {
          val result = controller.submit(kind = "permanentResidents")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value            shouldBe routes.CompletedLettingsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          mayHaveMorePermanentResidents(data).value shouldBe false
        }
      }
      "and the journey comes from the 'Occupier List' page" should {
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
        "be handling POST kind=temporaryOccupiers&understand=false by replying 303 redirect to the 'How many nights' page" in new ControllerFixture {
          val result = controller.submit(kind = "temporaryOccupiers")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value           shouldBe routes.HowManyNightsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          mayHaveMoreCompletedLettings(data).value shouldBe false
        }
        "be handling POST kind=unknown by replying 303 redirect to the 'Task List' page" in new ControllerFixture {
          val result = controller.submit(kind = "unknown")(
            fakePostRequest
              .withFormUrlEncodedBody("understood" -> "false")
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value     shouldBe controllers.routes.TaskListController
            .show()
            .withFragment("lettingHistory")
            .toString
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          mayHaveMoreCompletedLettings(data) shouldBe None
        }
      }
    }
    "regardless of what the user might have answered" should {
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(kind = "permanentResidents")(
          fakePostRequest
            .withFormUrlEncodedBody("understood" -> "") // understood is missing!
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("understood") shouldBe "error.boolean"
      }
    }
  }

  trait ControllerFixture(
    mayHaveMorePermanentResidents: Option[Boolean] = None,
    mayHaveMoreCompletedLettings: Option[Boolean] = None
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
            mayHaveMoreCompletedLettings = mayHaveMoreCompletedLettings
          )
        )
      ),
      repository
    )
