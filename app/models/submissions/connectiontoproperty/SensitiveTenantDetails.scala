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

package models.submissions.connectiontoproperty

import crypto.MongoCrypto
import models.submissions.common.SensitiveAddress
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveTenantDetails(
  name: String,
  descriptionOfLetting: String,
  correspondenceAddress: Option[SensitiveAddress]
) extends Sensitive[TenantDetails] {

  override def decryptedValue: TenantDetails = TenantDetails(
    name,
    descriptionOfLetting,
    correspondenceAddress.map(_.decryptedValue)
  )

}

object SensitiveTenantDetails {
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveTenantDetails] = Json.format

  def apply(tenantDetails: TenantDetails): SensitiveTenantDetails = SensitiveTenantDetails(
    tenantDetails.name,
    tenantDetails.descriptionOfLetting,
    tenantDetails.correspondenceAddress.map(SensitiveAddress(_))
  )
}
