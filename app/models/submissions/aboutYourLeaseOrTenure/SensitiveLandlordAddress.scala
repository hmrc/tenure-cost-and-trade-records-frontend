/*
 * Copyright 2023 HM Revenue & Customs
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

package models.submissions.aboutYourLeaseOrTenure

import crypto.MongoCrypto
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString

case class SensitiveLandlordAddress(
  buildingNameNumber: SensitiveString,
  street1: Option[SensitiveString],
  town: SensitiveString,
  county: Option[SensitiveString],
  postcode: SensitiveString
) extends Sensitive[LandlordAddress] {

  override def decryptedValue: LandlordAddress = LandlordAddress(
    buildingNameNumber.decryptedValue,
    street1.map(_.decryptedValue),
    town.decryptedValue,
    county.map(_.decryptedValue),
    postcode.decryptedValue
  )
}

object SensitiveLandlordAddress {
  import crypto.SensitiveFormats._
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveLandlordAddress] =
    Json.format[SensitiveLandlordAddress]

  def apply(landlordAddress: LandlordAddress): SensitiveLandlordAddress = SensitiveLandlordAddress(
    SensitiveString(landlordAddress.buildingNameNumber),
    landlordAddress.street1.map(SensitiveString(_)),
    SensitiveString(landlordAddress.town),
    landlordAddress.county.map(SensitiveString(_)),
    SensitiveString(landlordAddress.postcode)
  )
}
