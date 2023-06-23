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

package models.submissions.notconnected

import crypto.MongoCrypto
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveRemoveConnectionDetails(
            removeConnectionDetails: Option[SensitiveRemoveConnectionsDetails] = None,
            pastConnectionType: Option[PastConnectionType] = None
          ) extends Sensitive[RemoveConnectionDetails] {
  override def decryptedValue: RemoveConnectionDetails = RemoveConnectionDetails(
    removeConnectionDetails.map(_.decryptedValue),
    pastConnectionType
  )
}

object SensitiveRemoveConnectionDetails{

  import crypto.SensitiveFormats._
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveRemoveConnectionDetails] = Json.format[SensitiveRemoveConnectionDetails]

  def apply(removeConnectionDetails: RemoveConnectionDetails):SensitiveRemoveConnectionDetails = SensitiveRemoveConnectionDetails(
    removeConnectionDetails.removeConnectionDetails.map(SensitiveRemoveConnectionsDetails(_)),
    removeConnectionDetails.pastConnectionType
  )
}