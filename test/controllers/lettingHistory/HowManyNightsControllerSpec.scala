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
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import models.submissions.lettingHistory.LettingHistory.{hasCompletedLettings, intendedLettings}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.howManyNights as HowManyNightsView

class HowManyNightsControllerSpec extends LettingHistoryControllerSpec:

  "the IntendedLettingNights controller" when {
    "the user has not specified any number of nights yet"     should {
      "be handling GET requests by replying 200 with the form showing just the nights field" in new ControllerFixture {
        val result  = controller.show()(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.CompletedLettingsController.show.url}" class="govuk-back-link"""")
        content                     should include("""lettingHistory.intendedLettings.heading""")
        content                     should include("""name="nights"""")
      }
      "be handling good POST by replying 303 redirect to the 'Yearly availability' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "nights" -> "140"
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
        nights = Some(252)
      ) {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("""name="nights" type="text"  spellcheck="false"
                                                     |      value="252"""".stripMargin)
      }
      "be handling good POST by replying 303 redirect to the 'Has stopped letting?' page" in new ControllerFixture(
        isWelsh = true,
        nights = Some(252)
      ) {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "nights" -> "140"
        )
        val result  = controller.submit(request)
        status(result)                            shouldBe SEE_OTHER
        redirectLocation(result).value            shouldBe "/path/to/letting-stopped"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.nights.value shouldBe 140
      }
    }
    "the user session is either fresh or stale"               should {
      "be handling invalid POST by replying 400 with error messages" in new ControllerFixture {
        val result  = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "nights" -> ""
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.number")
      }
    }
  }

  trait ControllerFixture(
    nights: Option[Int] = None,
    isWelsh: Boolean = false
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HowManyNightsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HowManyNightsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = Some(false),
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
