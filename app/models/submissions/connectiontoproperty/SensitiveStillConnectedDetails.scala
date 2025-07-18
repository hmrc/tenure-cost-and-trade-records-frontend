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
import models.submissions.common.{AnswersYesNo, CheckYourAnswersAndConfirm, SensitiveAddress}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

import java.time.LocalDate

case class SensitiveStillConnectedDetails(
  addressConnectionType: Option[AddressConnectionType] = None,
  connectionToProperty: Option[ConnectionToProperty] = None,
  editAddress: Option[SensitiveAddress] = None,
  isPropertyVacant: Option[AnswersYesNo] = None,
  tradingNameOperatingFromProperty: Option[String] = None,
  tradingNameOwnTheProperty: Option[AnswersYesNo] = None,
  tradingNamePayingRent: Option[AnswersYesNo] = None,
  areYouThirdParty: Option[AnswersYesNo] = None,
  vacantPropertyStartDate: Option[LocalDate] = None,
  isAnyRentReceived: Option[AnswersYesNo] = None,
  provideContactDetails: Option[SensitiveYourContactDetails] = None,
  lettingPartOfPropertyDetailsIndex: Int = 0,
  maxOfLettings: Option[Boolean] = None,
  lettingPartOfPropertyDetails: IndexedSeq[SensitiveLettingPartOfPropertyDetails] = IndexedSeq.empty,
  checkYourAnswersConnectionToProperty: Option[CheckYourAnswersAndConfirm] = None
) extends Sensitive[StillConnectedDetails]:

  override def decryptedValue: StillConnectedDetails = StillConnectedDetails(
    addressConnectionType,
    connectionToProperty,
    editAddress.map(_.decryptedValue),
    isPropertyVacant,
    tradingNameOperatingFromProperty,
    tradingNameOwnTheProperty,
    tradingNamePayingRent,
    areYouThirdParty,
    vacantPropertyStartDate,
    isAnyRentReceived,
    provideContactDetails.map(_.decryptedValue),
    lettingPartOfPropertyDetailsIndex,
    maxOfLettings,
    lettingPartOfPropertyDetails.map(_.decryptedValue),
    checkYourAnswersConnectionToProperty
  )

object SensitiveStillConnectedDetails:

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveStillConnectedDetails] = Json.format

  def apply(stillConnectedDetails: StillConnectedDetails): SensitiveStillConnectedDetails =
    SensitiveStillConnectedDetails(
      stillConnectedDetails.addressConnectionType,
      stillConnectedDetails.connectionToProperty,
      stillConnectedDetails.editAddress.map(SensitiveAddress(_)),
      stillConnectedDetails.isPropertyVacant,
      stillConnectedDetails.tradingNameOperatingFromProperty,
      stillConnectedDetails.tradingNameOwnTheProperty,
      stillConnectedDetails.tradingNamePayingRent,
      stillConnectedDetails.areYouThirdParty,
      stillConnectedDetails.vacantPropertyStartDate,
      stillConnectedDetails.isAnyRentReceived,
      stillConnectedDetails.provideContactDetails.map(SensitiveYourContactDetails(_)),
      stillConnectedDetails.lettingPartOfPropertyDetailsIndex,
      stillConnectedDetails.maxOfLettings,
      stillConnectedDetails.lettingPartOfPropertyDetails.map(SensitiveLettingPartOfPropertyDetails(_)),
      stillConnectedDetails.checkYourAnswersConnectionToProperty
    )
