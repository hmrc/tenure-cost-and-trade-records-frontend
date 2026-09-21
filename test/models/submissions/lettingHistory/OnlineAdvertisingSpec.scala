/*
 * Copyright 2026 HM Revenue & Customs
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
import models.submissions.common.Address as CommonAddress
import models.submissions.lettingHistory.LettingHistory.*
import uk.gov.hmrc.vo.unit.test.BaseSpec

class OnlineAdvertisingSpec extends BaseSpec:

  private val session: Session = Session(
    referenceNumber = "99996048004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  private val niceApartment: AdvertisingDetail = AdvertisingDetail(
    websiteAddress = "http://www.myproperty.com/properties/12569",
    propertyReferenceNumber = "12569"
  )

  private val uglyApartment: AdvertisingDetail = AdvertisingDetail(
    websiteAddress = "http://www.booking.com/properties/99999",
    propertyReferenceNumber = "99999"
  )

  "the OnlineAdvertising trait" when {
    "copying the session withHasOnlineAdvertising" should {
      "set a boolean value although lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withHasOnlineAdvertising(true)
        session.changed                        shouldBe true
        session.data.lettingHistory           shouldNot be(None)
        hasOnlineAdvertising(session.data).get shouldBe true
      }

      "set a boolean value although lettingHistory.hasOnlineAdvertising was None" in new SessionWithSomeLettingHistory {
        val session: SessionWrapper = withHasOnlineAdvertising(true)
        session.changed                        shouldBe true
        hasOnlineAdvertising(session.data).get shouldBe true
      }

      "confirm the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session: SessionWrapper = withHasOnlineAdvertising(true)
        session.changed                        shouldBe false
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)       shouldNot be(empty)
      }

      "negate the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session: SessionWrapper = withHasOnlineAdvertising(false)
        session.changed                        shouldBe true
        hasOnlineAdvertising(session.data).get shouldBe false
        onlineAdvertising(session.data)        shouldBe empty
      }

      "double negate the boolean value which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session1: SessionWrapper = withHasOnlineAdvertising(false)
        val session2: SessionWrapper = withHasOnlineAdvertising(true)(using session1.data)
        session2.changed                        shouldBe true
        hasOnlineAdvertising(session2.data).get shouldBe true
      }
    }

    "copying the session byAddingOrUpdatingOnlineAdvertising" should {
      "set a non-empty onlineAdvertising list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = byAddingOrUpdatingOnlineAdvertising(index = None, niceApartment)
        session.changed                        shouldBe true
        session.data.lettingHistory           shouldNot be(None)
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)          should have size 1
        onlineAdvertising(session.data).head   shouldBe niceApartment
      }

      "set the very first list value when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session: SessionWrapper = byAddingOrUpdatingOnlineAdvertising(index = None, niceApartment)
        session.changed                        shouldBe true
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)          should have size 1
        onlineAdvertising(session.data).head   shouldBe niceApartment
      }

      "confirm online advert which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        pending
        val session: SessionWrapper = byAddingOrUpdatingOnlineAdvertising(index = Some(0), niceApartment)
        session.changed                        shouldBe false
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)          should have size 1
        onlineAdvertising(session.data).head   shouldBe niceApartment
      }

      "change online advert which was already set" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session: SessionWrapper = byAddingOrUpdatingOnlineAdvertising(index = Some(0), uglyApartment)
        session.changed                        shouldBe true
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)          should have size 1
        onlineAdvertising(session.data).head   shouldBe uglyApartment
      }

      "append a online advert to the existing list" in new SessionWithSomeLettingHistory(onlineAdvertising =
        List(niceApartment)
      ) {
        val session: SessionWrapper = byAddingOrUpdatingOnlineAdvertising(index = None, uglyApartment)
        session.changed                        shouldBe true
        hasOnlineAdvertising(session.data).get shouldBe true
        onlineAdvertising(session.data)          should have size 2
        onlineAdvertising(session.data).head   shouldBe niceApartment
        onlineAdvertising(session.data).last   shouldBe uglyApartment
      }
    }
  }

  trait SessionWithNoLettingHistory:
    given Session = session

  trait SessionWithSomeLettingHistory(onlineAdvertising: List[AdvertisingDetail] = Nil):

    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          hasOnlineAdvertising = if onlineAdvertising.isEmpty then None else Some(true),
          onlineAdvertising = onlineAdvertising
        )
      )
    )
