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
import models.submissions.lettingHistory.{Address, LettingHistory, LocalPeriod, OccupierDetail}
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.occupierDetail as OccupierDetailView

class OccupierDetailControllerSpec extends LettingHistoryControllerSpec:

  "the OccupierDetail controller" when {
    "the user session is fresh"                        should {
      "be handling GET by replying 200 with the form showing name and address fields" in new ControllerFixture {
        val result = controller.show(maybeIndex = None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                 shouldBe "lettingHistory.occupierDetail.heading"
        page.backLink                shouldBe routes.HasCompletedLettingsController.show.url
        page.input("name")             should beEmpty
        page.input("address.line1")    should beEmpty
        page.input("address.town")     should beEmpty
        page.input("address.county")   should beEmpty
        page.input("address.postcode") should beEmpty
      }
      "be handling good POST by replying 303 redirect to 'Rental Period' page" in new ControllerFixture {
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
          rentalPeriod = None
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET ?index=0 by replying 200 with the form pre-filled with name and address values" in new ControllerFixture(
          oneOccupier
        ) {
          val result = controller.show(maybeIndex = Some(0))(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.input("name")             should haveValue(oneOccupier.head.name)
          page.input("address.line1")    should haveValue(oneOccupier.head.address.line1)
          page.input("address.town")     should haveValue(oneOccupier.head.address.town)
          page.input("address.postcode") should haveValue(oneOccupier.head.address.postcode)
        }
        "be handling POST unknown by replying 303 redirect to 'Rental Period' page" in new ControllerFixture(
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
        "be handling POST known by replying 303 redirect to 'Rental Period' page" in new ControllerFixture(
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
          status(result)                          shouldBe SEE_OTHER
          redirectLocation(result).value          shouldBe routes.RentalPeriodController.show(index = Some(1)).url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          completedLettings(data)                   should have size 2 // the same as it was before sending the post request
          completedLettings(data)(0)              shouldBe oneOccupier.head
          completedLettings(data)(1).name         shouldBe "Mr. Two"
          completedLettings(data)(1).address      shouldBe Address(
            line1 = "22, Different Street", // instead of "Address Two"
            line2 = None,
            town = "Neverland",
            county = Some("Nowhere"),
            postcode = "BN12 4AX"
          )
          completedLettings(data)(1).rentalPeriod shouldBe twoOccupiers.last.rentalPeriod
        }
      }
      "and the maximum number of occupiers has been reached" should {
        "be handling GET by replying 303 redirect to the 'Occupiers List' page" in new ControllerFixture(
          fiveOccupiers
        ) {
          val result = controller.show(maybeIndex = None)(fakeGetRequest)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.OccupierListController.show.url
        }
      }
    }
    "regardless of what the user might have submitted" should {
      "be handling invalid POST by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "name"             -> "",
            "address.line1"    -> "",
            "address.line2"    -> "",
            "address.town"     -> "",
            "address.county"   -> "",
            "address.postcode" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("name")             shouldBe "lettingHistory.occupierDetail.name.required"
        page.error("address.line1")    shouldBe "error.buildingNameNumber.required"
        page.error("address.town")     shouldBe "error.townCity.required"
        page.error("address.postcode") shouldBe "error.postcodeAlternativeContact.required"
      }
    }
  }

  "OccupierDetail" should {

    "serialize and deserialize correctly" in {

      val occupierDetail = occupierDetails
      val json           = Json.toJson(occupierDetail)
      json.as[OccupierDetail] shouldBe occupierDetail
    }

  }

  trait ControllerFixture(completedLettings: List[OccupierDetail] = Nil)
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
