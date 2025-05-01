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
import models.submissions.lettingHistory.{IntendedDetail, LettingHistory}
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.libs.json.Writes
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.isYearlyAvailable as IsYearlyAvailableView

class IsYearlyAvailableControllerSpec extends LettingHistoryControllerSpec:

  "the IsYearlyAvailable controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.intendedLettings.isYearlyAvailable.eitherMeetsCriteriaOrHasNotStopped.heading"
        page.backLink          shouldBe routes.HowManyNightsController.show.url
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveNoneChecked
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing answer!
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.intendedLettings.isYearlyAvailable.required"
      }
      "be handling POST answer='yes' by replying 303 redirect to the 'Do you advert online' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                       shouldBe routes.HasOnlineAdvertisingController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.isYearlyAvailable.value shouldBe true
      }
    }
    "the user has already answered"            should {
      "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
        hasStopped = Some(true),
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.intendedLettings.isYearlyAvailable.hasStoppedLetting.heading"
        page.backLink          shouldBe routes.WhenWasLastLetController.show.url
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveChecked("yes")
        page.radios("answer") shouldNot haveChecked("no")
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture(
        hasStopped = Some(false),
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing answer!
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.intendedLettings.isYearlyAvailable.required"
      }
      "be handling POST answer='no' by replying 303 redirect to the 'Give lenght of trading session' page" in new ControllerFixture(
        hasStopped = Some(false),
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "no"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value                       shouldBe routes.TradingSeasonController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.isYearlyAvailable.value shouldBe false
      }
    }
  }

  trait ControllerFixture(isYearlyAvailable: Option[Boolean] = None, hasStopped: Option[Boolean] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new IsYearlyAvailableController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[IsYearlyAvailableView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedDetail(
                hasStopped = hasStopped,
                isYearlyAvailable = isYearlyAvailable
              )
            )
          )
        )
      ),
      repository
    )
