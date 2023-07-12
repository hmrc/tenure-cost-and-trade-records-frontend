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

package models.submissions.additionalinformation

import crypto.MongoCrypto
import models.submissions.common.{SensitiveContactDetails, SensitiveContactDetailsAddress}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import crypto.SensitiveFormats._

case class SensitiveAlternativeContactDetails(
  alternativeContactFullName: Option[SensitiveString],
  alternativeContactDetails: Option[SensitiveContactDetails],
  alternativeContactAddress: Option[SensitiveAlternativeAddress]
) extends Sensitive[AlternativeContactDetails] {

  override def decryptedValue: AlternativeContactDetails = AlternativeContactDetails(
    alternativeContactFullName.map(_.decryptedValue),
    alternativeContactDetails.map(_.decryptedValue),
    alternativeContactAddress.map(_.decryptedValue)
  )
}

object SensitiveAlternativeContactDetails {
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAlternativeContactDetails] =
    Json.format[SensitiveAlternativeContactDetails]

  def apply(alternativeContactDetails: AlternativeContactDetails): SensitiveAlternativeContactDetails =
    SensitiveAlternativeContactDetails(
      alternativeContactDetails.alternativeContactFullName.map(SensitiveString(_)),
      alternativeContactDetails.alternativeContactDetails.map(SensitiveContactDetails(_)),
      alternativeContactDetails.alternativeContactAddress.map(SensitiveAlternativeAddress(_))
    )
}
