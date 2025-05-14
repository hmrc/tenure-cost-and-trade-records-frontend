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

import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, OccupierDetail, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.hasCompletedLettings as HasCompletedLettingsView

class HasCompletedLettingsControllerSpec extends LettingHistoryControllerSpec:

  "the HasCompletedLettings controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.hasCompletedLettings.heading"
        page.backLink          shouldBe routes.HasPermanentResidentsController.show.url
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveNoneChecked
      }
      "be handling POST answer='yes' by replying 303 redirect to 'CompletedLettingsDetail' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe routes.OccupierDetailController.show(index = None).url
        verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
        hasCompletedLettings(data).value shouldBe true
      }
    }
    "the user has already provided an answer"  should {
      "regardless of the given number of occupiers"          should {
        "be handling GET by replying 200 with the HTML form having checked radios" in new ControllerFixture(
          permanentResidents = twoResidents,
          completedLettings = twoOccupiers
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF8
          val page = contentAsJsoup(result)
          page.backLink          shouldBe routes.ResidentListController.show.url
          page.radios("answer") shouldNot be(empty)
          page.radios("answer")    should haveChecked(value = "yes")
          page.radios("answer") shouldNot haveChecked(value = "no")
        }
        "be handling POST answer='yes' by replying 303 redirect to the 'CompletedLettingDetail' page" in new ControllerFixture(
          completedLettings = twoOccupiers
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.OccupierListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe true
          completedLettings(data)          shouldBe twoOccupiers
        }
        "be handling POST answer='no' by replying 303 redirect to the 'LettingIntention' page" in new ControllerFixture(
          completedLettings = fiveOccupiers,
          mayHaveMoreCompletedLettings = Some(true)
        ) {
          // Answering 'no' will clear out all completed lettings
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "no"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value                                   shouldBe routes.HowManyNightsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasCompletedLettings(data).value                                 shouldBe false
          completedLettings(data)                                          shouldBe Nil
          mayHaveMoreEntitiesOf(kind = "completedLettings", data.getValue) shouldBe None
        }
      }
      "and the maximum number of occupiers has been reached" should {
        "be handling POST hasCompletedLettings='yes' and reply 303 redirect to the 'CompletedLettingList' page" in new ControllerFixture(
          completedLettings = fiveOccupiers
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.OccupierListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe true
          completedLettings(data)            should have size 5
        }
      }
    }
    "regardless of the user providing answers" should {
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest
            .withFormUrlEncodedBody(
              "answer" -> "" // missing
            )
            .withQueryParams(
              "from" -> "TL"
            )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.backLink        shouldBe controllers.routes.TaskListController.show().withFragment("letting-history").toString
        page.error("answer") shouldBe "lettingHistory.hasCompletedLettings.required"
      }
    }
  }

  trait ControllerFixture(
    permanentResidents: List[ResidentDetail] = Nil,
    completedLettings: List[OccupierDetail] = Nil,
    mayHaveMoreCompletedLettings: Option[Boolean] = None
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HasCompletedLettingsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HasCompletedLettingsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(permanentResidents.nonEmpty),
            permanentResidents = permanentResidents,
            hasCompletedLettings = Some(completedLettings.nonEmpty),
            completedLettings = completedLettings,
            mayHaveMoreCompletedLettings = mayHaveMoreCompletedLettings
          )
        )
      ),
      repository
    )
