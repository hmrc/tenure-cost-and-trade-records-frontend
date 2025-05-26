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

import connectors.MockAddressLookup
import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, LocalPeriod, OccupierAddress, OccupierDetail}
import navigation.LettingHistoryNavigator
import play.api.libs.json.Json
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.occupierDetail as OccupierDetailView
import scala.concurrent.Future.successful

class OccupierDetailControllerSpec extends LettingHistoryControllerSpec:

  "the OccupierDetail controller" when {
    "the user session is fresh"                        should {
      "be handling GET by replying 200 with the form showing empty name" in new ControllerFixture {
        val result = controller.show(maybeIndex = None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading     shouldBe "lettingHistory.occupierDetail.heading"
        page.backLink    shouldBe routes.HasCompletedLettingsController.show.url
        page.input("name") should beEmpty
      }
      "save new record and reply 303 and redirect to address lookup page" in new ControllerFixture {
        val name    = "Mr. First"
        val request = fakePostRequest.withFormUrlEncodedBody(
          "name" -> name
        )
        val result  = controller.submit()(request)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"

        val sessionCaptor = captor[Session]
        verify(repository, once).saveOrUpdate(sessionCaptor.capture())(using any)
        val session       = sessionCaptor.getValue

        completedLettings(session)      should have size 1
        completedLettings(session)(0) shouldBe OccupierDetail(
          name = name,
          address = None,
          rentalPeriod = None
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET ?index=0 by replying 200 with the form pre-filled with name" in new ControllerFixture(
          oneOccupier
        ) {
          val result = controller.show(maybeIndex = Some(0))(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF8
          val page = contentAsJsoup(result)
          page.input("name") should haveValue(oneOccupier.head.name)
        }
        "update existing record and reply 303 and redirect to address lookup page" in new ControllerFixture(
          oneOccupier
        ) {
          // Post an additional resident detail and expect it to become the second resident
          val name    = "Mr. Additional"
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name" -> name
          )
          val result  = controller.submit()(request)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe "/on-ramp"

          val sessionCaptor = captor[Session]
          verify(repository, once).saveOrUpdate(sessionCaptor.capture())(using any[HeaderCarrier])
          val session       = sessionCaptor.getValue

          completedLettings(session)           should have size 2 // instead of 1
          completedLettings(session)(0)      shouldBe oneOccupier.head
          completedLettings(session)(1).name shouldBe name
        }
        "be handling POST known by replying 303 redirect to 'Rental Period' page" in new ControllerFixture(
          twoOccupiers
        ) {
          pending
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
          val result  = controller.submit(maybeIndex = Some(1))(request)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.RentalPeriodController.show(index = Some(1)).url

          val sessionCaptor = captor[Session]
          verify(repository, once).saveOrUpdate(sessionCaptor.capture())(using any[HeaderCarrier])
          val session       = sessionCaptor.getValue

          completedLettings(session)                   should have size 2 // the same as it was before sending the post request
          completedLettings(session)(0)              shouldBe oneOccupier.head
          completedLettings(session)(1).name         shouldBe "Mr. Two"
          completedLettings(session)(1).address      shouldBe OccupierAddress(
            buildingNameNumber = "22, Different Street", // instead of "Address Two"
            street1 = None,
            town = "Neverland",
            county = Some("Nowhere"),
            postcode = "BN12 4AX"
          )
          completedLettings(session)(1).rentalPeriod shouldBe twoOccupiers.last.rentalPeriod
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
        val result = controller.submit()(
          fakePostRequest.withFormUrlEncodedBody(
            "name" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("name") shouldBe "lettingHistory.occupierDetail.name.required"
      }
    }
    "retrieving the confirmed address"                 should {
      "save record and reply 303 redirect to the next page" in new ControllerFixture(
        twoOccupiers
      ) {
        val idx    = 1
        val result = controller.addressLookupCallback(idx, "confirmedAddress")(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.RentalPeriodController.show(Some(idx)).url

        val idCaptor = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(idCaptor)(using any[HeaderCarrier])
        idCaptor.getValue shouldBe "confirmedAddress"

        val sessionCaptor = captor[Session]
        verify(repository, once).saveOrUpdate(sessionCaptor)(using any)
        val session       = sessionCaptor.getValue

        inside(session.lettingHistory.value.completedLettings(idx)) { case record: OccupierDetail =>
          record.address.value shouldBe OccupierAddress(
            buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
            street1 = Some(addressLookupConfirmedAddress.address.lines.get(idx)),
            town = addressLookupConfirmedAddress.address.lines.get.last,
            county = None,
            postcode = addressLookupConfirmedAddress.address.postcode.get
          )
        }
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

  trait ControllerFixture(completedLettings: List[OccupierDetail] = Nil) extends MockAddressLookup:

    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any)).thenReturn(successful(()))

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
      addressLookupConnector,
      repository
    )
