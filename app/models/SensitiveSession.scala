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

package models

import crypto.MongoCrypto
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartFour, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo, SensitiveAboutLeaseOrAgreementPartOne}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, SensitiveAboutYouAndTheProperty}
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.SensitiveAddress
import models.submissions.connectiontoproperty.SensitiveStillConnectedDetails
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import models.submissions.lettingHistory.SensitiveLettingHistory
import models.submissions.notconnected.SensitiveRemoveConnectionDetails
import models.submissions.requestReferenceNumber.SensitiveRequestReferenceNumber
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveSession(
  referenceNumber: String,
  forType: ForType,
  address: SensitiveAddress,
  token: String,
  isWelsh: Boolean,
  stillConnectedDetails: Option[SensitiveStillConnectedDetails] = None,
  removeConnectionDetails: Option[SensitiveRemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[SensitiveAboutYouAndTheProperty] = None,
  aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[SensitiveAboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[SensitiveRequestReferenceNumber],
  downloadPDFDetails: Option[DownloadPDFDetails] = None,
  lettingHistory: Option[SensitiveLettingHistory] = None
  // Also add more properties to both this.decryptedValue and SensitiveSession.apply methods (see below)
) extends Sensitive[Session] {

  override def decryptedValue: Session = Session(
    referenceNumber,
    forType,
    address.decryptedValue,
    token,
    isWelsh,
    stillConnectedDetails.map(_.decryptedValue),
    removeConnectionDetails.map(_.decryptedValue),
    aboutYouAndTheProperty.map(_.decryptedValue),
    aboutYouAndThePropertyPartTwo,
    additionalInformation,
    aboutTheTradingHistory,
    aboutTheTradingHistoryPartOne,
    aboutFranchisesOrLettings,
    aboutLeaseOrAgreementPartOne.map(_.decryptedValue),
    aboutLeaseOrAgreementPartTwo,
    aboutLeaseOrAgreementPartThree,
    aboutLeaseOrAgreementPartFour,
    saveAsDraftPassword,
    lastCYAPageUrl,
    requestReferenceNumberDetails.map(_.decryptedValue),
    downloadPDFDetails,
    lettingHistory.map(_.decryptedValue)
    // Add more properties here ...
  )
}

object SensitiveSession {

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveSession] = Json.format

  def apply(session: Session): SensitiveSession = SensitiveSession(
    session.referenceNumber,
    session.forType,
    SensitiveAddress(session.address),
    session.token,
    session.isWelsh,
    session.stillConnectedDetails.map(SensitiveStillConnectedDetails(_)),
    session.removeConnectionDetails.map(SensitiveRemoveConnectionDetails(_)),
    session.aboutYouAndTheProperty.map(SensitiveAboutYouAndTheProperty(_)),
    session.aboutYouAndThePropertyPartTwo,
    session.additionalInformation,
    session.aboutTheTradingHistory,
    session.aboutTheTradingHistoryPartOne,
    session.aboutFranchisesOrLettings,
    session.aboutLeaseOrAgreementPartOne.map(SensitiveAboutLeaseOrAgreementPartOne(_)),
    session.aboutLeaseOrAgreementPartTwo,
    session.aboutLeaseOrAgreementPartThree,
    session.aboutLeaseOrAgreementPartFour,
    session.saveAsDraftPassword,
    session.lastCYAPageUrl,
    session.requestReferenceNumberDetails.map(SensitiveRequestReferenceNumber(_)),
    session.downloadPDFDetails,
    session.lettingHistory.map(SensitiveLettingHistory(_))
  )
}
