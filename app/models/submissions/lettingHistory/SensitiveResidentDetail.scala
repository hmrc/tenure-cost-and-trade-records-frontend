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

import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString

case class SensitiveResidentDetail(
  name: SensitiveString,
  address: SensitiveString
) extends Sensitive[ResidentDetail]:

  override def decryptedValue: ResidentDetail =
    ResidentDetail(
      name.decryptedValue,
      address.decryptedValue
    )

object SensitiveResidentDetail:
  import crypto.MongoCrypto
  import crypto.SensitiveFormats.sensitiveStringFormat
  import play.api.libs.json.{Format, Json}
  implicit def format(using crypto: MongoCrypto): Format[SensitiveResidentDetail] = Json.format

  // encryption method
  def apply(residentDetail: ResidentDetail): SensitiveResidentDetail =
    SensitiveResidentDetail(
      SensitiveString(residentDetail.name),
      SensitiveString(residentDetail.address)
    )
