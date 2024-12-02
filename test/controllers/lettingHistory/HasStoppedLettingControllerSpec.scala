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
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.hasStoppedLetting as HasStoppedLettingView

class HasStoppedLettingControllerSpec extends LettingHistoryControllerSpec:

  "the HasStoppedLetting controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest.withSession("from" -> "permanentResidentsPage"))
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading        shouldBe "lettingHistory.hasStoppedLetting.heading"
        page.backLink       shouldBe routes.HowManyNightsController.show.url
        page.radios("answer") should haveNoneChecked
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing answer!
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.hasStoppedLetting.required"
      }
      "be handling POST answer='yes' by replying 303 redirect to the 'Last Rent' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                shouldBe routes.WhenWasLastLetController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.hasStopped.value shouldBe true
      }
    }
    "the user has already provided an answer"  should {
      "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
        hasStopped = Some(true)
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.radios("answer")    should haveChecked(value = "yes")
        page.radios("answer") shouldNot haveChecked(value = "no")
      }
      "be handling POST answer='no' by replying 303 redirect to the 'Commercial Lettings' page" in new ControllerFixture(
        hasStopped = Some(true)
      ) {
        // Answering 'no' will clear out all residents details
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "no"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                shouldBe "/path/to/is-yearly-available"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.hasStopped.value shouldBe false
      }
    }
  }

  trait ControllerFixture(hasStopped: Option[Boolean] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HasStoppedLettingController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HasStoppedLettingView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedLettings(
                hasStopped = hasStopped
              )
            )
          )
        )
      ),
      repository
    )
