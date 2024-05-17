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

package models.audit

import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.Address
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import models.submissions.notconnected.RemoveConnectionDetails
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class UserData(
  referenceNumber: String,
  forType: String,
  address: Address,
  stillConnectedDetails: Option[StillConnectedDetails],
  removeConnectionDetails: Option[RemoveConnectionDetails],
  aboutYouAndTheProperty: Option[AboutYouAndTheProperty],
  aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo],
  additionalInformation: Option[AdditionalInformation],
  aboutTheTradingHistory: Option[AboutTheTradingHistory],
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings],
  aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne],
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo],
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree],
  requestReferenceNumber: Option[RequestReferenceNumberDetails],
  downloadPDFDetails: Option[DownloadPDFDetails]
)

object UserData {
  implicit val format: OFormat[UserData] = Json.format[UserData]
}
