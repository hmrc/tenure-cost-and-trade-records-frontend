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
import models.submissions.common.Address as CommonAddress
import models.submissions.lettingHistory.LettingHistory.*
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.LocalDate

class CompletedLettingsSpec extends AnyWordSpec with Matchers with OptionValues:

  "the CompletedLettings trait" when {
    "copying the session withHasCompletedLettings"            should {
      "set a boolean value although lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = withHasCompletedLettings(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasCompletedLettings(session.data).value mustBe true
      }
      "set a boolean value although lettingHistory.hasCompletedLettings was None" in new SessionWithSomeLettingHistory {
        val session = withHasCompletedLettings(true)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe true
        // completedLettings(session.data) must be(empty)
      }
      "confirm the boolean value which was already set" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val session = withHasCompletedLettings(true)
        session.changed mustBe false
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) mustNot be(empty)
      }
      "negate the boolean value which was already set" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val session = withHasCompletedLettings(false)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe false
        completedLettings(session.data) mustBe empty
      }
      "double negate the boolean value which was already set" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val session1 = withHasCompletedLettings(false)
        val session2 = withHasCompletedLettings(true)(using session1.data)
        session2.changed mustBe true
        hasCompletedLettings(session2.data).value mustBe true
      }
    }
    "copying the session byAddingOrUpdatingTemporaryOccupier" should {
      "set a non-empty completedLettings list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val (index, session) = byAddingOrUpdatingOccupier(johnBrown)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) must have size 1
        completedLettings(session.data).head mustBe johnBrown
      }
      "set the very first list value when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val (index, session) = byAddingOrUpdatingOccupier(johnBrown)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) must have size 1
        completedLettings(session.data).head mustBe johnBrown
      }
      "confirm resident address which was already set" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val (index, session) = byAddingOrUpdatingOccupier(johnBrown, maybeIndex = Some(0))
        session.changed mustBe false
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) must have size 1
        completedLettings(session.data).head mustBe johnBrown
      }
      "change resident address which was already set" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val updatedJohnBrown = OccupierDetail(johnBrown.name, aliceWhite.address, rentalPeriod = None)
        val (index, session) = byAddingOrUpdatingOccupier(updatedJohnBrown, maybeIndex = Some(0))
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) must have size 1
        completedLettings(session.data).head.name mustBe johnBrown.name
        completedLettings(session.data).head.address mustBe aliceWhite.address
      }
      "append a second resident to the existing list" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val (index, session) = byAddingOrUpdatingOccupier(aliceWhite)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe true
        completedLettings(session.data) must have size 2
        completedLettings(session.data).head mustBe johnBrown
        completedLettings(session.data).last mustBe aliceWhite
      }
    }
    "copying the session byUpdatingOccupierRentalPeriod"      should {
      "create some empty lettingHistory if it was None" in new SessionWithNoLettingHistory {
        val session = byUpdatingOccupierRentalPeriod(0, year2024)
        session.changed mustBe true
        hasCompletedLettings(session.data) mustBe None
        completedLettings(session.data) mustBe empty
      }
      "keep the empty completedLettings" in new SessionWithSomeLettingHistory(
        completedLettings = List.empty
      ) {
        val session = byUpdatingOccupierRentalPeriod(0, year2024)
        session.changed mustBe false
        hasCompletedLettings(session.data) mustBe None
        completedLettings(session.data) mustBe empty
      }
      "patch the existing occupier if the given rental period is different" in new SessionWithSomeLettingHistory(
        completedLettings = List(johnBrown)
      ) {
        val session = byUpdatingOccupierRentalPeriod(0, year2024)
        session.changed mustBe true
        completedLettings(session.data).head.name mustBe johnBrown.name
        completedLettings(session.data).head.address mustBe johnBrown.address
        completedLettings(session.data).head.rentalPeriod.value mustBe year2024
      }
      "keep the existing occupier if the given rental period is the same" in new SessionWithSomeLettingHistory(
        completedLettings = List(
          johnBrown.copy(rentalPeriod = Some(year2024))
        )
      ) {
        val session = byUpdatingOccupierRentalPeriod(0, year2024)
        session.changed mustBe false
        completedLettings(session.data).head.name mustBe johnBrown.name
        completedLettings(session.data).head.address mustBe johnBrown.address
        completedLettings(session.data).head.rentalPeriod.value mustBe year2024
      }
    }
    "copying the session byRemovingCompletedLettingAt"        should {
      "set an empty completedLettings list although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session = byRemovingCompletedLettingAt(2)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        hasCompletedLettings(session.data).value mustBe false
        completedLettings(session.data) mustBe empty
      }
      "remove from empty completedLettings list when lettingHistory is not None" in new SessionWithSomeLettingHistory {
        val session = byRemovingCompletedLettingAt(0)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe false
        completedLettings(session.data) mustBe empty
      }
      "remove existent resident from completedLettings list" in new SessionWithSomeLettingHistory(completedLettings =
        List(johnBrown)
      ) {
        val session = byRemovingCompletedLettingAt(0)
        session.changed mustBe true
        hasCompletedLettings(session.data).value mustBe false
        completedLettings(session.data) mustBe empty
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

  val year2024 = LocalPeriod(
    fromDate = LocalDate.of(2024, 1, 1),
    toDate = LocalDate.of(2024, 12, 31)
  )

  val johnBrown = OccupierDetail(
    name = "John Brown",
    address = Some(
      OccupierAddress(
        buildingNameNumber = "21, Somewhere Place",
        street1 = Some("Basement"),
        town = "NeverTown",
        county = Some("Birds' Island"),
        postcode = "BN124AX"
      )
    ),
    rentalPeriod = None
  )

  val aliceWhite = OccupierDetail(
    name = "Alice White",
    address = None,
    rentalPeriod = None
  )

  trait SessionWithNoLettingHistory:
    given Session = session // having lettingHistory = None

  trait SessionWithSomeLettingHistory(completedLettings: List[OccupierDetail] = Nil):
    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          hasCompletedLettings = if completedLettings.isEmpty then None else Some(true),
          completedLettings = completedLettings
        )
      )
    )
