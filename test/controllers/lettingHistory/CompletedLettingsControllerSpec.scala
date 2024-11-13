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
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.completedLettings as CompletedLettingsView

class CompletedLettingsControllerSpec extends LettingHistoryControllerSpec:

  "the CompletedLettings controller" when {
    "the user session is fresh" should {
      "be handling GET by replying 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.PermanentResidentsController.show.url}" class="govuk-back-link"""")
        content                     should include("lettingHistory.completedLettings.heading")
        content                     should not include "checked"
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("lettingHistory.hasCompletedLettings.required")
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
    "the user session is stale" should {
      "regardless of the given number of occupiers" should {
        "be handling GET by replying 200 with the HTML form having checked radios" in new ControllerFixture(
          permanentResidents = twoResidents,
          hasCompletedLettings = Some(true)
        ) {
          val result  = controller.show(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include(s"""${routes.ResidentListController.show.url}" class="govuk-back-link"""")
          content                     should include("checked")
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
          redirectLocation(result).value   shouldBe "/path/to/intended-nights"
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          hasCompletedLettings(data).value shouldBe false
          completedLettings(data)          shouldBe Nil
        }
      }
    }
  }

  trait ControllerFixture(permanentResidents: List[ResidentDetail] = Nil, hasCompletedLettings: Option[Boolean] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new CompletedLettingsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[CompletedLettingsView],
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
