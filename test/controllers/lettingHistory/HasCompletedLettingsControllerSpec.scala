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
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.hasCompletedLettings as HasCompletedLettingsView

class HasCompletedLettingsControllerSpec extends LettingHistoryControllerSpec:

  "the CompletedLettings controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading        shouldBe "lettingHistory.completedLettings.heading"
        page.backLink       shouldBe routes.HasPermanentResidentsController.show.url
        page.radios("answer") should haveNoneChecked
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.hasCompletedLettings.required"
      }
      "be handling POST answer='yes' by replying 303 redirect to 'Occupier Detail' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe routes.OccupierDetailController.show(index = None).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        hasCompletedLettings(data).value shouldBe true
      }
    }
    "the user has already provided an answer"  should {
      "regardless of the given number of occupiers"          should {
        "be handling GET by replying 200 with the HTML form having checked radios" in new ControllerFixture(
          permanentResidents = twoResidents,
          hasCompletedLettings = Some(true)
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.backLink          shouldBe routes.ResidentListController.show.url
          page.radios("answer")    should haveChecked(value = "yes")
          page.radios("answer") shouldNot haveChecked(value = "no")
        }
        "be handling POST answer='yes' by replying 303 redirect to the 'Occupier Detail' page" in new ControllerFixture(
          hasCompletedLettings = Some(true)
        ) {
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                   shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.OccupierDetailController.show(index = None).url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe true
        }
        "be handling POST answer='no' by replying 303 redirect to the 'Letting intention' page" in new ControllerFixture(
          hasCompletedLettings = Some(true)
        ) {
          // Answering 'no' will clear out all residents details
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "no"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.HowManyNightsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe false
          completedLettings(data)          shouldBe Nil
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling POST hasCompletedLettings='yes' and reply 303 redirect to the 'Occupiers List' page" in new ControllerFixture(
          hasCompletedLettings = Some(true)
        ) {
          pending
          val result = controller.submit(fakePostRequest.withFormUrlEncodedBody("answer" -> "yes"))
          status(result)                   shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.OccupierListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe true
          completedLettings(data)            should have size 5
        }
      }
    }
  }

  trait ControllerFixture(permanentResidents: List[ResidentDetail] = Nil, hasCompletedLettings: Option[Boolean] = None)
      extends MockRepositoryFixture
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
            hasCompletedLettings = hasCompletedLettings
          )
        )
      ),
      repository
    )
