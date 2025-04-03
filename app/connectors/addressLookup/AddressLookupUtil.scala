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

package connectors.addressLookup

import models.submissions.aboutYourLeaseOrTenure.LandlordAddress

@deprecated("Remove this utility class as it's not longer useful")
object AddressLookupUtil {
  def getLandLordAddress(addressLookup: AddressLookupConfirmedAddress): LandlordAddress =
    addressLookup match {
      case AddressLookupConfirmedAddress(AddressLookupAddress(Some(lines), postcode, _), _, Some(_))
          if lines.nonEmpty =>
        // With ID present, at least two lines are guaranteed
        LandlordAddress(
          buildingNameNumber = lines.head,
          street1 = lines.lift(1),
          town = lines.lift(2).getOrElse(""),
          county = None,
          postcode = postcode.getOrElse("Empty Postcode")
        )

      case AddressLookupConfirmedAddress(AddressLookupAddress(Some(lines), postcode, _), _, None) =>
        lines.length match {
          case 1 =>
            LandlordAddress(
              buildingNameNumber = lines.head,
              street1 = None,
              town = "",
              county = None,
              postcode = postcode.getOrElse("Empty Postcode")
            )
          case 2 =>
            LandlordAddress(
              buildingNameNumber = lines.head,
              street1 = None,
              town = lines(1),
              county = None,
              postcode = postcode.getOrElse("Empty Postcode")
            )
          case 3 =>
            LandlordAddress(
              buildingNameNumber = lines.head,
              street1 = Some(lines(1)),
              town = lines(2),
              county = None,
              postcode = postcode.getOrElse("Empty Postcode")
            )
          case _ =>
            LandlordAddress(
              buildingNameNumber = lines.head,
              street1 = Some(lines(1)),
              town = lines(2),
              county = lines.lift(3),
              postcode = postcode.getOrElse("Empty Postcode")
            )
        }
      case _                                                                                      =>
        LandlordAddress(
          buildingNameNumber = "",
          street1 = None,
          town = "",
          county = None,
          postcode = "Empty Postcode"
        )
    }

}
