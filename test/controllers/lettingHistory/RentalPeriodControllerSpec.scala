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
import models.submissions.common.AnswerYes
import models.submissions.lettingHistory.{Address, LettingHistory, LocalPeriod, OccupierDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.rentalPeriod as RentalPeriodView

import java.time.LocalDate

class RentalPeriodControllerSpec extends LettingHistoryControllerSpec with FiscalYearSupport:

  "the RentPeriod controller" when {
    "the user has not entered any rent period yet"           should {
      "be handling GET /rental-period?index=0 by replying 200 with the form showing date fields" in new StaleSessionFixture {
        val result  = controller.show(maybeIndex = Some(0))(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("""name="fromDate.day"""")
        content                     should include("""name="fromDate.month"""")
        content                     should include("""name="fromDate.year"""")
        content                     should include("""name="toDate.day"""")
        content                     should include("""name="toDate.month"""")
        content                     should include("""name="toDate.year"""")
        content                     should include(routes.OccupierDetailController.show(index = Some(0)).url)
      }
      "be handling POST /rental-period?index=0 by replying 303 redirect to 'Occupier List' page" in new StaleSessionFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "fromDate.day"   -> "1",
          "fromDate.month" -> "4",
          "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
          "toDate.day"     -> "31",
          "toDate.month"   -> "3",
          "toDate.year"    -> previousFiscalYearEnd.toString
        )
        val result  = controller.submit(index = Some(0))(request)
        status(result)                                     shouldBe SEE_OTHER
        redirectLocation(result).value                     shouldBe "/path/to/occupiers-list"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        completedLettings(data)                              should have size 1
        completedLettings(data).head.rental.value.fromDate shouldBe LocalDate.of(previousFiscalYearEnd - 1, 4, 1)
        completedLettings(data).head.rental.value.toDate   shouldBe LocalDate.of(previousFiscalYearEnd, 3, 31)
      }
    }
    "the user has already entered a rent period"             should {
      "be handling GET /rental-period?index=0 by replying 200 with the pre-filled form showing date fields" in new StaleSessionFixture(
        rental = Some(
          LocalPeriod(
            fromDate = LocalDate.of(previousFiscalYearEnd, 9, 10),
            toDate = LocalDate.of(previousFiscalYearEnd - 1, 2, 9)
          )
        )
      ) {
        val result  = controller.show(maybeIndex = Some(0))(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("""name="fromDate.day" type="text"   value="10"""")
        content                     should include("""name="fromDate.month" type="text"   value="9"""")
        content                     should include("""name="fromDate.year" type="text"   value="2024"""")
        content                     should include("""name="toDate.day" type="text"   value="9"""")
        content                     should include("""name="toDate.month" type="text"   value="2"""")
        content                     should include("""name="toDate.year" type="text"   value="2023"""")
        content                     should include(routes.OccupierDetailController.show(index = Some(0)).url)
      }
    }
    "regardless users having entered occupier's name or not" should {
      "be handling GET /rental-period missing index by replying 303 redirect to 'Occupiers List' page" in new StaleSessionFixture {
        val result = controller.show(maybeIndex = None)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/path/to/occupiers-list"
      }
      "be handling GET /rental-period unknown index by replying 303 redirect to 'Occupiers List' page" in new StaleSessionFixture {
        val result = controller.show(maybeIndex = Some(99))(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/path/to/occupiers-list"
      }
      "be handling invalid POST /detail by replying 400 with error messages" in new StaleSessionFixture {
        val result  = controller.submit(index = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "fromDate.day"   -> "",
            "fromDate.month" -> "",
            "fromDate.year"  -> "",
            "toDate.day"     -> "",
            "toDate.month"   -> "",
            "toDate.year"    -> ""
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.date.required")
      }
    }
  }

  trait StaleSessionFixture(rental: Option[LocalPeriod] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new RentalPeriodController(
      mcc = stubMessagesControllerComponents(),
      dateUtil = inject[DateUtilLocalised],
      navigator = inject[LettingHistoryNavigator],
      theView = inject[RentalPeriodView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = Some(AnswerYes),
            completedLettings = List(
              OccupierDetail(
                name = "Somebody",
                address = Address(
                  line1 = "22, Somewhere Street",
                  line2 = None,
                  town = "Neverland",
                  county = None,
                  postcode = "BN124AX"
                ),
                rental = rental
              )
            )
          )
        )
      ),
      repository
    )
