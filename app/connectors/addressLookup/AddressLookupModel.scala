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

import models.submissions.common.Address
import play.api.libs.json.{Format, Json}

case class AddressLookupConfirmedAddress(
  address: AddressLookupAddress,
  auditRef: String,
  id: Option[String]
) {

  val buildingNameNumber = address.lines.fold("")(_.headOption.getOrElse(""))
  val street1            = address.lines.flatMap(lines => if lines.size > 2 then Some(lines(1)) else None)
  val town               = address.lines.fold("")(_.lastOption.getOrElse(""))
  val county             = None
  val postcode           = address.postcode.getOrElse("")

  def asAddress = Address(
    this.buildingNameNumber,
    this.street1,
    this.town,
    this.county,
    this.postcode
  )
}

object AddressLookupConfirmedAddress:
  given Format[AddressLookupConfirmedAddress] = Json.format

// ------------

case class AddressLookupAddress(
  lines: Option[Seq[String]],
  postcode: Option[String],
  country: Option[AddressLookupCountry]
)

object AddressLookupAddress:
  given Format[AddressLookupAddress] = Json.format

// ------------

case class AddressLookupCountry(
  name: Option[String],
  code: Option[String]
)

object AddressLookupCountry:
  given Format[AddressLookupCountry] = Json.format
