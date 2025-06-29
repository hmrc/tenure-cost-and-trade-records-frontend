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

package models.submissions

import models.ForType
import models.Session
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.accommodation.AccommodationDetails
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.Address
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.RemoveConnectionDetails
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.libs.json.{Json, OFormat}

import java.time.Instant

case class ConnectedSubmission(
  referenceNumber: String,
  forType: ForType,
  address: Address,
  token: String,
  stillConnectedDetails: Option[StillConnectedDetails] = None,
  removeConnectionDetails: Option[RemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = None,
  accommodationDetails: Option[AccommodationDetails] = None,
  createdAt: Instant
)

object ConnectedSubmission:

  implicit val format: OFormat[ConnectedSubmission] = Json.format

  def apply(session: Session): ConnectedSubmission = ConnectedSubmission(
    session.referenceNumber,
    session.forType,
    session.address,
    session.token,
    session.stillConnectedDetails,
    session.removeConnectionDetails,
    session.aboutYouAndTheProperty,
    session.additionalInformation,
    session.aboutTheTradingHistory,
    session.aboutFranchisesOrLettings,
    session.aboutLeaseOrAgreementPartOne,
    session.aboutLeaseOrAgreementPartTwo,
    session.aboutLeaseOrAgreementPartThree,
    session.saveAsDraftPassword,
    session.lastCYAPageUrl,
    session.requestReferenceNumberDetails,
    session.accommodationDetails,
    Instant.now()
  )
