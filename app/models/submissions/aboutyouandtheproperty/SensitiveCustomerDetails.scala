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

package models.submissions.aboutyouandtheproperty


import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import crypto.MongoCrypto
import models.submissions.common.SensitiveContactDetails

case class SensitiveCustomerDetails(
                                     fullName: SensitiveString,
                                     contactDetails: SensitiveContactDetails
                                   ) extends Sensitive[CustomerDetails] {

  override def decryptedValue: CustomerDetails = CustomerDetails(
    fullName.decryptedValue,
    contactDetails.decryptedValue,
  )

}

object SensitiveCustomerDetails {

  import crypto.SensitiveFormats._

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveCustomerDetails] = Json.format[SensitiveCustomerDetails]

  def apply(customerDetails: CustomerDetails): SensitiveCustomerDetails = SensitiveCustomerDetails(
    SensitiveString(customerDetails.fullName),
    SensitiveContactDetails(customerDetails.contactDetails),
  )
}