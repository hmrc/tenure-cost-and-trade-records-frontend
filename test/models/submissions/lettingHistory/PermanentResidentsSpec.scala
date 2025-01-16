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

class PermanentResidentsSpec extends AnyWordSpec with Matchers with OptionValues:

  "the PermanentResidents trait" when {
    "copying the session withHasPermanentResidents"           should {
      "set a boolean value although lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = withHasPermanentResidents(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasPermanentResidents(session.data).value mustBe true
      }
      "set a boolean value although lettingHistory.hasPermanentResident was None" in new SessionWithSomeLettingHistory {
        val session = withHasPermanentResidents(true)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe true
        // permanentResidents(session.data) must be(empty)
      }
      "confirm the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session = withHasPermanentResidents(true)
        session.changed mustBe false
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) mustNot be(empty)
      }
      "negate the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session = withHasPermanentResidents(false)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe false
        permanentResidents(session.data) mustBe empty
      }
      "double negate the boolean value which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session1 = withHasPermanentResidents(false)
        val session2 = withHasPermanentResidents(true)(using session1.data)
        session2.changed mustBe true
        hasPermanentResidents(session2.data).value mustBe true
      }
    }
    "copying the session byAddingOrUpdatingPermanentResident" should {
      "set a non-empty permanentResidents list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = byAddingOrUpdatingPermanentResident(johnBrown)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) must have size 1
        permanentResidents(session.data).head mustBe johnBrown
      }
      "set the very first list value when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session = byAddingOrUpdatingPermanentResident(johnBrown)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) must have size 1
        permanentResidents(session.data).head mustBe johnBrown
      }
      "confirm resident address which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session = byAddingOrUpdatingPermanentResident(johnBrown)
        session.changed mustBe false
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) must have size 1
        permanentResidents(session.data).head mustBe johnBrown
      }
      "change resident address which was already set" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val newAddress = "20, NewAddress Avenue"
        val session    = byAddingOrUpdatingPermanentResident(
          ResidentDetail(
            name = johnBrown.name,
            address = newAddress
          )
        )
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) must have size 1
        permanentResidents(session.data).head.name mustBe johnBrown.name
        permanentResidents(session.data).head.address mustBe newAddress
      }
      "append a second resident to the existing list" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session = byAddingOrUpdatingPermanentResident(aliceWhite)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe true
        permanentResidents(session.data) must have size 2
        permanentResidents(session.data).head mustBe johnBrown
        permanentResidents(session.data).last mustBe aliceWhite
      }
    }
    "copying the session byRemovingPermanentResidentAt"       should {
      "set an empty permanentResidents list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = byRemovingPermanentResidentAt(2)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasPermanentResidents(session.data).value mustBe false
        permanentResidents(session.data) mustBe empty
      }
      "remove from empty permanentResidents list when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session = byRemovingPermanentResidentAt(0)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe false
        permanentResidents(session.data) mustBe empty
      }
      "remove existent resident from permanentResidents list" in new SessionWithSomeLettingHistory(permanentResidents =
        List(johnBrown)
      ) {
        val session = byRemovingPermanentResidentAt(0)
        session.changed mustBe true
        hasPermanentResidents(session.data).value mustBe false
        permanentResidents(session.data) mustBe empty
      }
    }
  }

  val session = Session(
    referenceNumber = "99996010004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  val johnBrown = ResidentDetail(
    name = "John Brown",
    address = "10, Somewhere Street"
  )

  val aliceWhite = ResidentDetail(
    name = "Alice White",
    address = "99, Anywhere Square"
  )

  trait SessionWithNoLettingHistory:
    given Session = session // having lettingHistory = None

  trait SessionWithSomeLettingHistory(permanentResidents: List[ResidentDetail] = Nil):
    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          hasPermanentResidents = if permanentResidents.isEmpty then None else Some(true),
          permanentResidents = permanentResidents
        )
      )
    )
