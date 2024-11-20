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
import views.html.lettingHistory.howManyNights as HowManyNightsView

class HowManyNightsControllerSpec extends LettingHistoryControllerSpec:

  "the HowManyNights controller" when {
    "the user has not specified any number of nights yet"     should {
      "be handling GET requests by replying 200 with the form showing just the nights field" in new ControllerFixture(
        hasCompletedLettings = Some(false)
      ) {
        val result = controller.show()(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading       shouldBe "lettingHistory.intendedLettings.heading"
        page.backLink      shouldBe routes.CompletedLettingsController.show.url
        page.input("nights") should beEmpty
      }
      "be handling good POST by replying 303 redirect to the 'Yearly availability' page" in new ControllerFixture(
        isWelsh = false,
        hasStopped = Some(true)
      ) {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "nights" -> "140" // meets criteria (England)
        )
        val result  = controller.submit(request)
        status(result)                            shouldBe SEE_OTHER
        redirectLocation(result).value            shouldBe "/path/to/is-yearly-available"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.nights.value shouldBe 140
      }
    }
    "the user has already specified a given number of nights" should {
      "be handling GET by replying 200 with the form pre-filled with the nights" in new ControllerFixture(
        isWelsh = true,
        hasCompletedLettings = Some(true),
        nights = Some(100),
        hasStopped = Some(false)
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.backLink      shouldBe routes.OccupierListController.show.url
        page.input("nights") should haveValue("100")
      }
      "be handling good POST by replying 303 redirect to the 'Has stopped letting?' page" in new ControllerFixture(
        isWelsh = true,
        nights = Some(100),
        hasStopped = Some(false)
      ) {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "nights" -> "251" // still does NOT meet criteria (in Wales)
        )
        val result  = controller.submit(request)
        status(result)                            shouldBe SEE_OTHER
        redirectLocation(result).value            shouldBe "/path/to/letting-stopped"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.nights.value shouldBe 251
      }
    }
    "regardless of what users might have submitted"           should {
      "be handling invalid POST by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "nights" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("nights") shouldBe "error.number"
      }
    }
  }

  trait ControllerFixture(
    isWelsh: Boolean = false,
    hasCompletedLettings: Option[Boolean] = None,
    nights: Option[Int] = None,
    hasStopped: Option[Boolean] = None
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HowManyNightsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HowManyNightsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = hasCompletedLettings,
            intendedLettings = Some(
              IntendedLettings(
                nights = nights
              )
            )
          )
        ),
        isWelsh = isWelsh
      ),
      repository
    )
