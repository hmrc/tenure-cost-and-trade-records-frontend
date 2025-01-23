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

package models.submissions.lettingHistory

import models.ForType.FOR6048
import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.common.Address as CommonAddress
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class OnlineAdvertisingSpec extends AnyWordSpec with Matchers with OptionValues:

  "the OnlineAdvertising trait" when {
    "copying the session withHasOnlineAdvertising"            should {
      "set a boolean value although lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = withHasOnlineAdvertising(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasOnlineAdvertising(session.data).value mustBe true
      }
      "set a boolean value although lettingHistory.hasOnlineAdvertising was None" in new SessionWithSomeLettingHistory {
        val session = withHasOnlineAdvertising(true)
        session.changed mustBe true
        hasOnlineAdvertising(session.data).value mustBe true
        // onlineAdvertising(session.data) must be(empty)
      }
      "confirm the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session = withHasOnlineAdvertising(true)
        session.changed mustBe false
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) mustNot be(empty)
      }
      "negate the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session = withHasOnlineAdvertising(false)
        session.changed mustBe true
        hasOnlineAdvertising(session.data).value mustBe false
        onlineAdvertising(session.data) mustBe empty
      }
      "double negate the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session1 = withHasOnlineAdvertising(false)
        val session2 = withHasOnlineAdvertising(true)(using session1.data)
        session2.changed mustBe true
        hasOnlineAdvertising(session2.data).value mustBe true
      }
    }
    "copying the session byAddingOrUpdatingOnlineAdvertising" should {
      "set a non-empty onlineAdvertising list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = byAddingOrUpdatingOnlineAdvertising(index = None, niceApartment)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) must have size 1
        onlineAdvertising(session.data).head mustBe niceApartment
      }
      "set the very first list value when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session = byAddingOrUpdatingOnlineAdvertising(index = None, niceApartment)
        session.changed mustBe true
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) must have size 1
        onlineAdvertising(session.data).head mustBe niceApartment
      }
      "confirm online advert which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        pending
        val session = byAddingOrUpdatingOnlineAdvertising(index = Some(0), niceApartment)
        session.changed mustBe false
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) must have size 1
        onlineAdvertising(session.data).head mustBe niceApartment
      }
      "change online advert which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session = byAddingOrUpdatingOnlineAdvertising(index = Some(0), uglyApartment)
        session.changed mustBe true
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) must have size 1
        onlineAdvertising(session.data).head mustBe uglyApartment
      }
      "append a online advert to the existing list" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session = byAddingOrUpdatingOnlineAdvertising(index = None, uglyApartment)
        session.changed mustBe true
        hasOnlineAdvertising(session.data).value mustBe true
        onlineAdvertising(session.data) must have size 2
        onlineAdvertising(session.data).head mustBe niceApartment
        onlineAdvertising(session.data).last mustBe uglyApartment
      }
    }
    "copying the session byAddingOrUpdatingOnlineAdvertising" should {}
  }

  val session = Session(
    referenceNumber = "99996010004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  val niceApartment = AdvertisingDetail(
    websiteAddress = "http://www.myproperty.com/properties/12569",
    propertyReferenceNumber = "12569"
  )

  val uglyApartment = AdvertisingDetail(
    websiteAddress = "http://www.booking.com/properties/99999",
    propertyReferenceNumber = "99999"
  )

  trait SessionWithNoLettingHistory:
    given Session = session // having lettingHistory = None

  trait SessionWithSomeLettingHistory(onlineAdvertising: List[AdvertisingDetail] = Nil):
    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          hasOnlineAdvertising = if onlineAdvertising.isEmpty then None else Some(true),
          onlineAdvertising = onlineAdvertising
        )
      )
    )
