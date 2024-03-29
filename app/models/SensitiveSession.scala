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

package models

import crypto.MongoCrypto
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo, SensitiveAboutLeaseOrAgreementPartOne}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutyouandtheproperty.SensitiveAboutYouAndTheProperty
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.SensitiveAddress
import models.submissions.connectiontoproperty.SensitiveStillConnectedDetails
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import models.submissions.notconnected.SensitiveRemoveConnectionDetails
import models.submissions.requestReferenceNumber.SensitiveRequestReferenceNumber
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveSession(
  referenceNumber: String,
  forType: String,
  address: SensitiveAddress,
  token: String,
  stillConnectedDetails: Option[SensitiveStillConnectedDetails] = None,
  removeConnectionDetails: Option[SensitiveRemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[SensitiveAboutYouAndTheProperty] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[SensitiveAboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[SensitiveRequestReferenceNumber],
  downloadPDFDetails: Option[DownloadPDFDetails] = None
) extends Sensitive[Session] {

  override def decryptedValue: Session = Session(
    referenceNumber,
    forType,
    address.decryptedValue,
    token,
    stillConnectedDetails.map(_.decryptedValue),
    removeConnectionDetails.map(_.decryptedValue),
    aboutYouAndTheProperty.map(_.decryptedValue),
    additionalInformation,
    aboutTheTradingHistory,
    aboutFranchisesOrLettings,
    aboutLeaseOrAgreementPartOne.map(_.decryptedValue),
    aboutLeaseOrAgreementPartTwo,
    aboutLeaseOrAgreementPartThree,
    saveAsDraftPassword,
    lastCYAPageUrl,
    requestReferenceNumberDetails.map(_.decryptedValue),
    downloadPDFDetails
  )
}

object SensitiveSession {

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveSession] = Json.format[SensitiveSession]

  def apply(session: Session): SensitiveSession = SensitiveSession(
    session.referenceNumber,
    session.forType,
    SensitiveAddress(session.address),
    session.token,
    session.stillConnectedDetails.map(SensitiveStillConnectedDetails(_)),
    session.removeConnectionDetails.map(SensitiveRemoveConnectionDetails(_)),
    session.aboutYouAndTheProperty.map(SensitiveAboutYouAndTheProperty(_)),
    session.additionalInformation,
    session.aboutTheTradingHistory,
    session.aboutFranchisesOrLettings,
    session.aboutLeaseOrAgreementPartOne.map(SensitiveAboutLeaseOrAgreementPartOne(_)),
    session.aboutLeaseOrAgreementPartTwo,
    session.aboutLeaseOrAgreementPartThree,
    session.saveAsDraftPassword,
    session.lastCYAPageUrl,
    session.requestReferenceNumberDetails.map(SensitiveRequestReferenceNumber(_)),
    session.downloadPDFDetails
  )
}
