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

package controllers.aboutYourLeaseOrTenure

import models.submissions.aboutYourLeaseOrTenure.LandlordAddress
import connectors.addressLookup.*
import connectors.addressLookup.AddressLookupUtil.getLandLordAddress
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AddressLookupUtilSpec extends AnyWordSpec with Matchers {

  "getLandLordAddress" should {

    "correctly transform with id present and at least two lines" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("1 Main Street", "Metropolis", "Gotham City")), Some("12345"), None),
        "auditRef",
        Some("id")
      )

      val expected = LandlordAddress(
        "1 Main Street",
        Some("Metropolis"),
        "Gotham City",
        None,
        "12345"
      )

      getLandLordAddress(lookup) shouldBe expected
    }

    "correctly transform without id and four lines" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("Building 1", "Street 2", "Town 3", "County 4")), Some("Postcode"), None),
        "auditRef",
        None
      )

      val expected = LandlordAddress(
        "Building 1",
        Some("Street 2"),
        "Town 3",
        Some("County 4"),
        "Postcode"
      )

      getLandLordAddress(lookup) shouldBe expected
    }

    "correctly transform without id and three lines" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("Building 1", "Street 2", "Town 3")), Some("Postcode"), None),
        "auditRef",
        None
      )

      val expected = LandlordAddress(
        "Building 1",
        Some("Street 2"),
        "Town 3",
        None,
        "Postcode"
      )

      getLandLordAddress(lookup) shouldBe expected
    }

    "correctly transform without id and two lines" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("Building 1", "Town 2")), Some("Postcode"), None),
        "auditRef",
        None
      )

      val expected = LandlordAddress(
        "Building 1",
        None,
        "Town 2",
        None,
        "Postcode"
      )

      getLandLordAddress(lookup) shouldBe expected
    }

    "correctly transform without id and one line" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("Building 1")), None, None),
        "auditRef",
        None
      )

      val expected = LandlordAddress(
        "Building 1",
        None,
        "",
        None,
        "Empty Postcode"
      )

      getLandLordAddress(lookup) shouldBe expected
    }
  }
}
