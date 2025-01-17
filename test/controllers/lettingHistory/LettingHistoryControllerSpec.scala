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
import models.submissions.lettingHistory.{Address, AdvertisingOnline, LocalPeriod, OccupierDetail, ResidentDetail}
import org.mockito.ArgumentCaptor
import play.api.libs.json.Writes
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.{JsoupHelpers, TestBaseSpec}

import java.time.LocalDate
import scala.concurrent.Future.successful

class LettingHistoryControllerSpec extends TestBaseSpec with JsoupHelpers:

  val residentDetails = ResidentDetail(name = "Mr. One", address = "Address One")
  val occupierDetails = OccupierDetail(
    name = "Mr. One",
    address = Address("Address One", None, "Neverland", None, "BN124AX"),
    rental = None
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
      address = Address("Address One", None, "Neverland", None, "BN124AX"),
      rental = None
    )
  )

  val twoOccupiers = oneOccupier ++ List(
    OccupierDetail(
      name = "Mr. Two",
      address = Address("Address Two", None, "Neverland", None, "BN124AX"),
      rental = Some(
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
      address = Address("Address Three", None, "Neverland", None, "BN124AX"),
      rental = None
    ),
    OccupierDetail(
      name = "Mr. Four",
      address = Address("Address Four", None, "Neverland", None, "BN124AX"),
      rental = None
    ),
    OccupierDetail(
      name = "Mrs. Five",
      address = Address("Address Five", None, "Neverland", None, "BN124AX"),
      rental = None
    )
  )

  val oneOnlineAdvertising: List[AdvertisingOnline] = List(
    AdvertisingOnline(websiteAddress = "123.com", propertyReferenceNumber = "aaa123")
  )

  val twoOnlineAdvertising: List[AdvertisingOnline] = oneOnlineAdvertising ++ List(
    AdvertisingOnline(websiteAddress = "456.com", propertyReferenceNumber = "aaa456")
  )

  val fiveOnlineAdvertising: List[AdvertisingOnline] = twoOnlineAdvertising ++ List(
    AdvertisingOnline(websiteAddress = "789.com", propertyReferenceNumber = "aaa789"),
    AdvertisingOnline(websiteAddress = "111.com", propertyReferenceNumber = "aaa111"),
    AdvertisingOnline(websiteAddress = "112.com", propertyReferenceNumber = "aaa112")
  )

  trait MockRepositoryFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

  trait SessionCapturingFixture:
    given argumentCaptorToSession: Conversion[ArgumentCaptor[Session], Session] with
      def apply(c: ArgumentCaptor[Session]) = c.getValue
