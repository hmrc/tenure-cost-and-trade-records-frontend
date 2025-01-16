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

case class SensitiveLettingHistory(
  hasPermanentResidents: Option[Boolean],
  permanentResidents: Option[List[SensitiveResidentDetail]],
  mayHaveMorePermanentResidents: Option[Boolean],
  hasCompletedLettings: Option[Boolean],
  completedLettings: Option[List[SensitiveOccupierDetail]],
  mayHaveMoreCompletedLettings: Option[Boolean],
  intendedLettings: Option[IntendedDetail],
  onlineAdvertising: Option[Boolean],
  advertisingOnlineDetails: List[AdvertisingDetail]
) extends Sensitive[LettingHistory]:

  override def decryptedValue: LettingHistory =
    LettingHistory(
      hasPermanentResidents,
      permanentResidents.fold(Nil)(_.map(_.decryptedValue)),
      mayHaveMorePermanentResidents,
      hasCompletedLettings,
      completedLettings.fold(Nil)(_.map(_.decryptedValue)),
      mayHaveMoreCompletedLettings,
      intendedLettings,
      onlineAdvertising,
      advertisingOnlineDetails
    )

object SensitiveLettingHistory:
  import crypto.MongoCrypto
  import play.api.libs.json.{Format, Json}
  implicit def format(using crypto: MongoCrypto): Format[SensitiveLettingHistory] = Json.format

  // encryption method
  def apply(lettingHistory: LettingHistory): SensitiveLettingHistory =
    SensitiveLettingHistory(
      hasPermanentResidents = lettingHistory.hasPermanentResidents,
      permanentResidents =
        if lettingHistory.hasPermanentResidents.isEmpty
        then None
        else Some(lettingHistory.permanentResidents.map(SensitiveResidentDetail(_))),
      mayHaveMorePermanentResidents = lettingHistory.mayHaveMorePermanentResidents,
      hasCompletedLettings = lettingHistory.hasCompletedLettings,
      completedLettings =
        if lettingHistory.hasCompletedLettings.isEmpty
        then None
        else Some(lettingHistory.completedLettings.map(SensitiveOccupierDetail(_))),
      mayHaveMoreCompletedLettings = lettingHistory.mayHaveMoreCompletedLettings,
      intendedLettings = lettingHistory.intendedLettings,
      onlineAdvertising = lettingHistory.hasOnlineAdvertising,
      advertisingOnlineDetails = lettingHistory.onlineAdvertising
    )
