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

import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString

case class SensitiveAddress(
  line1: SensitiveString,
  line2: Option[SensitiveString],
  town: SensitiveString,
  county: Option[SensitiveString],
  postcode: SensitiveString
) extends Sensitive[Address]:

  override def decryptedValue: Address = Address(
    line1.decryptedValue,
    line2.map(_.decryptedValue),
    town.decryptedValue,
    county.map(_.decryptedValue),
    postcode.decryptedValue
  )

object SensitiveAddress:
  import crypto.MongoCrypto
  import crypto.SensitiveFormats.sensitiveStringFormat
  import play.api.libs.json.{Format, Json}

  implicit def format(using crypto: MongoCrypto): Format[SensitiveAddress] = Json.format

  // encryption method
  def apply(address: Address): SensitiveAddress =
    SensitiveAddress(
      SensitiveString(address.line1),
      address.line2.map(SensitiveString(_)),
      SensitiveString(address.town),
      address.county.map(SensitiveString(_)),
      SensitiveString(address.postcode)
    )
