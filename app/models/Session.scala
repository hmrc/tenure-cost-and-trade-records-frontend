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

import models.audit.UserData
import models.pages.Summary
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.Address
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.downloadFORTypeForm.DownloadPDFDetails
import models.submissions.notconnected.RemoveConnectionDetails
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.libs.json._

import java.time.LocalDate

// New session properties must be also added to class `UserData` and method `toUserData`
case class Session(
  referenceNumber: String,
  forType: String,
  address: Address,
  token: String,
  stillConnectedDetails: Option[StillConnectedDetails] = None,
  removeConnectionDetails: Option[RemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = None,
  aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = None,
  downloadPDFDetails: Option[DownloadPDFDetails] = None
  // New session properties must be also added to class `UserData` and method `toUserData`
) {

  /**
    * Returns only referenceNumber digits without slash or any other special char to use in endpoint path.
    */
  def referenceNumberCleaned: String = referenceNumber.replaceAll("[^0-9]", "")

  def financialYearEndDates: Seq[(LocalDate, Int)] =
    aboutTheTradingHistory.fold(Seq.empty[(LocalDate, Int)])(_.turnoverSections.map(_.financialYearEnd).zipWithIndex)

  def financialYearEndDates6020: Seq[(LocalDate, Int)] =
    aboutTheTradingHistory.fold(Seq.empty[(LocalDate, Int)])(
      _.turnoverSections6020.getOrElse(Seq()).map(_.financialYearEnd).zipWithIndex
    )

  def financialYearEndDates6030: Seq[(LocalDate, Int)] =
    aboutTheTradingHistory.fold(Seq.empty[(LocalDate, Int)])(
      _.turnoverSections6030.map(_.financialYearEnd).zipWithIndex
    )

  def toUserData: UserData = UserData(
    referenceNumber,
    forType,
    address,
    stillConnectedDetails,
    removeConnectionDetails,
    aboutYouAndTheProperty,
    aboutYouAndThePropertyPartTwo,
    additionalInformation,
    aboutTheTradingHistory,
    aboutTheTradingHistoryPartOne,
    aboutFranchisesOrLettings,
    aboutLeaseOrAgreementPartOne,
    aboutLeaseOrAgreementPartTwo,
    aboutLeaseOrAgreementPartThree,
    requestReferenceNumberDetails,
    downloadPDFDetails
  )

  def toSummary: Summary = Summary(
    referenceNumber,
    Option(address)
  )

}

object Session {
  implicit val format: OFormat[Session] = Json.format[Session]

}
