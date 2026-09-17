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

class PermanentResidentsSpec extends BaseSpec:

  private val session: Session = Session(
    referenceNumber = "99996048004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  private val johnBrown: ResidentDetail = ResidentDetail(
    name = "John Brown",
    address = "10, Somewhere Street"
  )

  private val aliceWhite: ResidentDetail = ResidentDetail(
    name = "Alice White",
    address = "99, Anywhere Square"
  )

  "the PermanentResidents trait" when {
    "copying the session withHasPermanentResidents" should {
      "set a boolean value although lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withHasPermanentResidents(true)
        session.changed                         shouldBe true
        session.data.lettingHistory            shouldNot be(None)
        hasPermanentResidents(session.data).get shouldBe true
      }

      "set a boolean value although lettingHistory.hasPermanentResident was None" in new SessionWithSomeLettingHistory {
        val session: SessionWrapper = withHasPermanentResidents(true)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe true
      }

      "confirm the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session: SessionWrapper = withHasPermanentResidents(true)
        session.changed                         shouldBe false
        hasPermanentResidents(session.data).get shouldBe true
        permanentResidents(session.data)       shouldNot be(empty)
      }

      "negate the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session: SessionWrapper = withHasPermanentResidents(false)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe false
        permanentResidents(session.data)        shouldBe empty
      }

      "double negate the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session1: SessionWrapper = withHasPermanentResidents(false)
        val session2: SessionWrapper = withHasPermanentResidents(true)(using session1.data)
        session2.changed                         shouldBe true
        hasPermanentResidents(session2.data).get shouldBe true
      }
    }

    "copying the session byAddingOrUpdatingPermanentResident" should {
      "set a non-empty permanentResidents list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = byAddingOrUpdatingPermanentResident(johnBrown)
        session.changed                         shouldBe true
        session.data.lettingHistory            shouldNot be(None)
        hasPermanentResidents(session.data).get shouldBe true
        permanentResidents(session.data)          should have size 1
        permanentResidents(session.data).head   shouldBe johnBrown
      }

      "set the very first list value when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session: SessionWrapper = byAddingOrUpdatingPermanentResident(johnBrown)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe true
        permanentResidents(session.data)          should have size 1
        permanentResidents(session.data).head   shouldBe johnBrown
      }

      "confirm resident address which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session: SessionWrapper = byAddingOrUpdatingPermanentResident(johnBrown, maybeIndex = Some(0))
        session.changed                         shouldBe false
        hasPermanentResidents(session.data).get shouldBe true
        permanentResidents(session.data)          should have size 1
        permanentResidents(session.data).head   shouldBe johnBrown
      }

      "change resident address which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val newAddress              = "20, NewAddress Avenue"
        val session: SessionWrapper = byAddingOrUpdatingPermanentResident(
          ResidentDetail(
            name = johnBrown.name,
            address = newAddress
          ),
          maybeIndex = Some(0)
        )
        session.changed                               shouldBe true
        hasPermanentResidents(session.data).get       shouldBe true
        permanentResidents(session.data)                should have size 1
        permanentResidents(session.data).head.name    shouldBe johnBrown.name
        permanentResidents(session.data).head.address shouldBe newAddress
      }

      "append a second resident to the existing list" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session: SessionWrapper = byAddingOrUpdatingPermanentResident(aliceWhite)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe true
        permanentResidents(session.data)          should have size 2
        permanentResidents(session.data).head   shouldBe johnBrown
        permanentResidents(session.data).last   shouldBe aliceWhite
      }
    }

    "copying the session byRemovingPermanentResidentAt" should {
      "set an empty permanentResidents list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = byRemovingPermanentResidentAt(2)
        session.changed                         shouldBe true
        session.data.lettingHistory            shouldNot be(None)
        hasPermanentResidents(session.data).get shouldBe false
        permanentResidents(session.data)        shouldBe empty
      }

      "remove from empty permanentResidents list when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session: SessionWrapper = byRemovingPermanentResidentAt(0)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe false
        permanentResidents(session.data)        shouldBe empty
      }

      "remove existent resident from permanentResidents list" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session: SessionWrapper = byRemovingPermanentResidentAt(0)
        session.changed                         shouldBe true
        hasPermanentResidents(session.data).get shouldBe false
        permanentResidents(session.data)        shouldBe empty
      }
    }
  }

  trait SessionWithNoLettingHistory:
    given Session = session

  trait SessionWithSomeLettingHistory(permanentResidents: List[ResidentDetail] = Nil):

    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          hasPermanentResidents = if permanentResidents.isEmpty then None else Some(true),
          permanentResidents = permanentResidents
        )
      )
    )
