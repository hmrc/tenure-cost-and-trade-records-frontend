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
import models.submissions.lettingHistory.{Address, LettingHistory, OccupierDetail}
import models.submissions.lettingHistory.LettingHistory._
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.occupierDetail as OccupierDetailView

class OccupierDetailControllerSpec extends LettingHistoryControllerSpec:

  "the OccupierDetail controller" when {
    "the user session is fresh"                 should {
      "be handling GET by replying 200 with the form showing name and address fields" in new FreshSessionFixture {
        val result  = controller.show(maybeIndex = None)(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.CompletedLettingsController.show.url}" class="govuk-back-link"""")
        content                     should include("""lettingHistory.occupierDetail.heading""")
        content                     should include("""name="name"""")
        content                     should include("""name="address.line1"""")
        content                     should include("""name="address.town"""")
        content                     should include("""name="address.county"""")
        content                     should include("""name="address.postcode"""")
      }
      "be handling good POST by replying 303 redirect to 'Rental Period' page" in new FreshSessionFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "name"             -> "Mr. Unknown",
          "address.line1"    -> "11, Fantasy Street",
          "address.line2"    -> "",
          "address.town"     -> "Neverland",
          "address.county"   -> "",
          "address.postcode" -> "BN124AX"
        )
        val result  = controller.submit(request)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.RentalPeriodController.show(index = Some(0)).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        completedLettings(data)          should have size 1
        completedLettings(data)(0)     shouldBe OccupierDetail(
          name = "Mr. Unknown",
          address = Address(
            line1 = "11, Fantasy Street",
            line2 = None,
            town = "Neverland",
            county = None,
            postcode = "BN12 4AX"
          ),
          rental = None
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET ?index=0 by replying 200 with the form pre-filled with name and address values" in new StaleSessionFixture(
          oneOccupier
        ) {
          val result  = controller.show(maybeIndex = Some(0))(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include("Mr. One")
          content                     should include("Address One")
        }
        "be handling POST unknown by replying 303 redirect to 'Rental Period' page" in new StaleSessionFixture(
          oneOccupier
        ) {
          // Post an unknown resident detail and expect it to become the third resident
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"             -> "Mr. Unknown",
            "address.line1"    -> "11, Fantasy Street",
            "address.line2"    -> "",
            "address.town"     -> "Neverland",
            "address.county"   -> "",
            "address.postcode" -> "BN124AX"
          )
          val result  = controller.submit(request)
          status(result)                     shouldBe SEE_OTHER
          redirectLocation(result).value     shouldBe routes.RentalPeriodController.show(index = Some(1)).url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          completedLettings(data)              should have size 2 // instead of 1
          completedLettings(data)(0)         shouldBe oneOccupier.head
          completedLettings(data)(1).name    shouldBe "Mr. Unknown"
          completedLettings(data)(1).address shouldBe Address(
            line1 = "11, Fantasy Street",
            line2 = None,
            town = "Neverland",
            county = None,
            postcode = "BN12 4AX"
          )
        }
        "be handling POST known by replying 303 redirect to 'Rental Period' page" in new StaleSessionFixture(
          twoOccupiers
        ) {
          // Post the second resident detail again and expect it to be changed
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"             -> twoOccupiers.last.name,
            "address.line1"    -> "22, Different Street",
            "address.line2"    -> "",
            "address.postcode" -> "BN124AX",
            "address.town"     -> "Neverland",
            "address.county"   -> "Nowhere",
            "address.postcode" -> "BN124AX"
          )
          val result  = controller.submit(request)
          status(result)                     shouldBe SEE_OTHER
          redirectLocation(result).value     shouldBe routes.RentalPeriodController.show(index = Some(1)).url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          completedLettings(data)              should have size 2 // the same as it was before sending the post request
          completedLettings(data)(0)         shouldBe oneOccupier.head
          completedLettings(data)(1).name    shouldBe "Mr. Two"
          completedLettings(data)(1).address shouldBe Address(
            line1 = "22, Different Street", // instead of "Address Two"
            line2 = None,
            town = "Neverland",
            county = Some("Nowhere"),
            postcode = "BN12 4AX"
          )
          completedLettings(data)(1).rental  shouldBe twoOccupiers.last.rental
        }
      }
      "and the maximum number of occupiers has been reached" should {
        "be handling GET by replying 303 redirect to the 'Occupiers List' page" in new StaleSessionFixture(
          fiveOccupiers
        ) {
          val result = controller.show(maybeIndex = None)(fakeGetRequest)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.OccupierListController.show.url
        }
      }
    }
    "the user session is either fresh or stale" should {
      "be handling invalid POST by replying 400 with error messages" in new FreshSessionFixture {
        val result  = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "name"             -> "",
            "address.line1"    -> "",
            "address.line2"    -> "",
            "address.town"     -> "",
            "address.county"   -> "",
            "address.postcode" -> ""
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("lettingHistory.occupierDetail.name.required")
        content          should include("error.buildingNameNumber.required")
        content          should include("error.townCity.required")
        content          should include("error.postcodeAlternativeContact.required")
      }
    }
  }

  // It provides the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new OccupierDetailController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[OccupierDetailView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(completedLettings: List[OccupierDetail])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new OccupierDetailController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[OccupierDetailView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = Some(completedLettings.nonEmpty),
            completedLettings = completedLettings
          )
        )
      ),
      repository
    )
