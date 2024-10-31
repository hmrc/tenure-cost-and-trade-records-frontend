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

package models.submissions.lettingHistory

import models.submissions.common.{Address, AnswerNo, AnswerYes}
import models.{ForType, Session}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.OptionValues
import utils.CustomMatchers

class LettingHistorySpec extends AnyWordSpec with CustomMatchers with Matchers with OptionValues:

  "the LettingHistory model" when {
    "updating a fresh session" should {
      "set the isPermanent boolean flag" in new FreshSessionFixture {
        val updatedSession = LettingHistory.withPermanentResidents(hasPermanentResidents = AnswerNo)
        LettingHistory.hasPermanentResidents(updatedSession).value must beAnswerNo
      }
      "add the first resident detail" in new FreshSessionFixture {
        val updateSession      = LettingHistory.byAddingPermanentResident(
          ResidentDetail(
            name = "Mr. Peter Pan",
            address = "20, Fantasy Street, Birds' Island, BIR067"
          )
        )
        val permanentResidents = LettingHistory.permanentResidents(updateSession)
        permanentResidents must have size 1
        permanentResidents(0).name mustBe "Mr. Peter Pan"
        permanentResidents(0).address mustBe "20, Fantasy Street, Birds' Island, BIR067"
      }
    }
    "updating a stale session" should {
      "set the isPermanent boolean flag" in new StaleSessionFixture(
        LettingHistory(hasPermanentResidents = Some(AnswerYes))
      ) {
        val updatedSession = LettingHistory.withPermanentResidents(AnswerNo)
        LettingHistory.hasPermanentResidents(updatedSession).value must beAnswerNo
      }
      "add a new unknown resident detail" in new StaleSessionFixture(havingKnownResident) {
        val misterNewGuest     = ResidentDetail(
          name = "Mr. New Guest",
          address = "Sleeping on the sofa"
        )
        val updateSession      = LettingHistory.byAddingPermanentResident(misterNewGuest)
        val permanentResidents = LettingHistory.permanentResidents(updateSession)
        permanentResidents must have size 2
        permanentResidents(0) mustBe misterKnownTenant
        permanentResidents(1) mustBe misterNewGuest
      }
      "add an already known resident by just overwriting the same" in new StaleSessionFixture(havingKnownResident) {
        // Update the session by adding the very same resident name
        val misterKnownAtDifferentAddress = misterKnownTenant.copy(address = "different address")
        val updateSession                 = LettingHistory.byAddingPermanentResident(misterKnownAtDifferentAddress)
        // Assert that nothing changed (the size of the detail list is still 1)
        val permanentResidents            = LettingHistory.permanentResidents(updateSession)
        permanentResidents must have size 1
        permanentResidents(0).name mustBe misterKnownTenant.name
        permanentResidents(0).address mustBe misterKnownAtDifferentAddress.address
      }
    }
  }

  trait FreshSessionFixture {
    given Session = Session(
      referenceNumber = "referenceNumber",
      forType = ForType.FOR6048,
      address = Address(
        buildingNameNumber = "buildingNameNumber",
        street1 = Some("street1"),
        street2 = "street2",
        county = Some("county"),
        postcode = "postcode"
      ),
      token = "token",
      isWelsh = false,
      lettingHistory = None
    )
  }

  trait StaleSessionFixture(stale: LettingHistory) {
    given Session = Session(
      referenceNumber = "referenceNumber",
      forType = ForType.FOR6048,
      address = Address(
        buildingNameNumber = "buildingNameNumber",
        street1 = Some("street1"),
        street2 = "street2",
        county = Some("county"),
        postcode = "postcode"
      ),
      token = "token",
      isWelsh = false,
      lettingHistory = Some(stale)
    )
  }

  val misterKnownTenant = ResidentDetail(
    name = "Mr. Known Tenant",
    address = "21, Already Added Street"
  )

  val havingKnownResident = LettingHistory(
    hasPermanentResidents = Some(AnswerYes),
    permanentResidents = List(misterKnownTenant)
  )
