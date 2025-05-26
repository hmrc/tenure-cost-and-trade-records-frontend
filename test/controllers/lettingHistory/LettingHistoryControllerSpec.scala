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
import models.submissions.lettingHistory.{AdvertisingDetail, LocalPeriod, OccupierAddress, OccupierDetail, ResidentDetail}
import org.mockito.ArgumentCaptor
import play.api.mvc.AnyContent
import play.api.mvc.request.RequestTarget
import play.api.test.FakeRequest
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.{JsoupHelpers, TestBaseSpec}

import java.time.LocalDate
import scala.concurrent.Future.successful

class LettingHistoryControllerSpec extends TestBaseSpec with JsoupHelpers:

  val residentDetails = ResidentDetail(name = "Mr. One", address = "Address One")
  val occupierDetails = OccupierDetail(
    name = "Mr. One",
    address = None,
    rentalPeriod = None
  )

  val oneResident = List(
    ResidentDetail(name = "Mr. One", address = "Address One")
  )

  val twoResidents = oneResident ++ List(
    ResidentDetail(name = "Mr. Two", address = "Address Two")
  )

  val fiveResidents = twoResidents ++ List(
    ResidentDetail(name = "Miss Three", address = "Address Three"),
    ResidentDetail(name = "Mr. Four", address = "Address Four"),
    ResidentDetail(name = "Mrs. Five", address = "Address Five")
  )

  val oneOccupier = List(
    OccupierDetail(
      name = "Mr. One",
      address = Some(OccupierAddress("Address One", None, "Neverland", None, "BN124AX")),
      rentalPeriod = None
    )
  )

  val twoOccupiers = oneOccupier ++ List(
    OccupierDetail(
      name = "Mr. Two",
      address = None,
      rentalPeriod = Some(
        LocalPeriod(
          fromDate = LocalDate.of(2023, 4, 2),
          toDate = LocalDate.of(2024, 3, 30)
        )
      )
    )
  )

  val fiveOccupiers = twoOccupiers ++ List(
    OccupierDetail(
      name = "Miss. Three",
      address = None,
      rentalPeriod = None
    ),
    OccupierDetail(
      name = "Mr. Four",
      address = None,
      rentalPeriod = None
    ),
    OccupierDetail(
      name = "Mrs. Five",
      address = Some(OccupierAddress("Address Five", None, "Neverland", None, "BN124AX")),
      rentalPeriod = None
    )
  )

  val oneAdvertising = List(
    AdvertisingDetail(websiteAddress = "123.com", propertyReferenceNumber = "aaa123")
  )

  val twoAdvertising = oneAdvertising ++ List(
    AdvertisingDetail(websiteAddress = "456.com", propertyReferenceNumber = "aaa456")
  )

  val fiveAdvertising = twoAdvertising ++ List(
    AdvertisingDetail(websiteAddress = "789.com", propertyReferenceNumber = "aaa789"),
    AdvertisingDetail(websiteAddress = "111.com", propertyReferenceNumber = "aaa111"),
    AdvertisingDetail(websiteAddress = "112.com", propertyReferenceNumber = "aaa112")
  )

  trait MockRepositoryFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(using any[HeaderCarrier]))
      .thenReturn(successful(()))

  trait SessionCapturingFixture:
    given argumentCaptorToSession: Conversion[ArgumentCaptor[Session], Session] with
      def apply(c: ArgumentCaptor[Session]) = c.getValue

  extension (r: FakeRequest[AnyContent])
    def withQueryParams(params: (String, String)*) =
      r.withTarget(RequestTarget(r.uri, r.path, queryString = Map(params.map((k, v) => k -> Seq(v))*)))
    def withFragment(fragment: String)             =
      r.withTarget(RequestTarget(r.uri, r.path + "#" + fragment, r.queryString))
