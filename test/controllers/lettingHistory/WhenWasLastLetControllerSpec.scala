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
import models.submissions.lettingHistory.LettingHistory._
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.whenWasLastLet as WhenWasLastLetView

import java.time.LocalDate

class WhenWasLastLetControllerSpec extends LettingHistoryControllerSpec:

  "the LastRental controller" when {
    "the user has not entered any date yet"         should {
      "be handling GET / by replying 200 with the form showing the date field" in new ControllerFixture {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.HasStoppedLettingController.show.url}" class="govuk-back-link"""")
        content                     should include("lettingHistory.whenWasLastLet.heading")
        content                     should include("""name="date.day"""")
        content                     should include("""name="date.month"""")
        content                     should include("""name="date.year"""")
      }
      "be handling POST / by replying 303 redirect to the 'Yearly Available' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "date.day"   -> "1",
          "date.month" -> "4",
          "date.year"  -> "2024"
        )
        val result  = controller.submit(request)
        status(result)                                    shouldBe SEE_OTHER
        redirectLocation(result).value                    shouldBe "/path/to/is-yearly-available"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.whenWasLastLet.value shouldBe LocalDate.of(2024, 4, 1)
      }
    }
    "the user has already entered a date"           should {
      "be handling GET / by replying 200 with the pre-filled form showing the date value" in new ControllerFixture(
        whenWasLastLet = Some(LocalDate.of(2024, 12, 25))
      ) {
        val result  = controller.show(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.HasStoppedLettingController.show.url}" class="govuk-back-link"""")
        content                     should include("lettingHistory.whenWasLastLet.heading")
        content                     should include("""name="date.day" type="text"   value="25""")
        content                     should include("""name="date.month" type="text"   value="12"""")
        content                     should include("""name="date.year" type="text"   value="2024"""")
      }
    }
    "regardless the user had entered a date or not" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new ControllerFixture {
        val result  = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "date.day"   -> "",
            "date.month" -> "",
            "date.year"  -> ""
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.date.required")
      }
    }
  }

  trait ControllerFixture(whenWasLastLet: Option[LocalDate] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new WhenWasLastLetController(
      mcc = stubMessagesControllerComponents(),
      dateUtil = inject[DateUtilLocalised],
      navigator = inject[LettingHistoryNavigator],
      theView = inject[WhenWasLastLetView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedLettings(
                whenWasLastLet = whenWasLastLet
              )
            )
          )
        )
      ),
      repository
    )
