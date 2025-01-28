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

case class SensitiveOccupierDetail(
  name: SensitiveString,
  address: SensitiveAddress,
  rental: Option[LocalPeriod]
) extends Sensitive[OccupierDetail]:

  override def decryptedValue: OccupierDetail =
    OccupierDetail(
      name.decryptedValue,
      address.decryptedValue,
      rental
    )

object SensitiveOccupierDetail:
  import crypto.MongoCrypto
  import crypto.SensitiveFormats.sensitiveStringFormat
  import play.api.libs.json.{Format, Json}
  implicit def format(using crypto: MongoCrypto): Format[SensitiveOccupierDetail] = Json.format

  // encryption method
  def apply(occupierDetail: OccupierDetail): SensitiveOccupierDetail =
    SensitiveOccupierDetail(
      SensitiveString(occupierDetail.name),
      SensitiveAddress(occupierDetail.address),
      occupierDetail.rentalPeriod
    )
