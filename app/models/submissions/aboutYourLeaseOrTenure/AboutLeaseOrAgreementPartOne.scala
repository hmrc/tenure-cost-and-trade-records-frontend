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

package models.submissions.aboutYourLeaseOrTenure

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

case class AboutLeaseOrAgreementPartOne(
  aboutTheLandlord: Option[AboutTheLandlord] = None,
  connectedToLandlord: Option[AnswersYesNo] = None,
  connectedToLandlordDetails: Option[String] = None,
  leaseOrAgreementYearsDetails: Option[LeaseOrAgreementYearsDetails] = None,
  currentRentPayableWithin12Months: Option[CurrentRentPayableWithin12Months] = None,
  propertyUseLeasebackAgreement: Option[AnswersYesNo] = None,
  annualRent: Option[BigDecimal] = None,
  currentRentFirstPaid: Option[CurrentRentFirstPaid] = None,
  currentLeaseOrAgreementBegin: Option[CurrentLeaseOrAgreementBegin] = None,
  includedInYourRentDetails: Option[IncludedInYourRentDetails] = None,
  doesTheRentPayable: Option[DoesTheRentPayable] = None,
  rentIncludeTradeServicesDetails: Option[AnswersYesNo] = None,
  rentIncludeTradeServicesInformation: Option[RentIncludeTradeServicesInformationDetails] = None,
  rentIncludeFixturesAndFittings: Option[AnswersYesNo] = None,
  rentIncludeFixturesAndFittingsAmount: Option[BigDecimal] = None,
  rentOpenMarketValue: Option[AnswersYesNo] = None,
  whatIsYourCurrentRentBasedOnDetails: Option[WhatIsYourCurrentRentBasedOnDetails] = None,
  rentIncreasedAnnuallyWithRPIDetails: Option[AnswersYesNo] = None,
  checkYourAnswersAboutYourLeaseOrTenure: Option[AnswersYesNo] = None,
  rentIncludesVat: Option[AnswersYesNo] = None
)

object AboutLeaseOrAgreementPartOne:

  implicit val format: OFormat[AboutLeaseOrAgreementPartOne] = Json.format

  def updateAboutLeaseOrAgreementPartOne(
    copy: AboutLeaseOrAgreementPartOne => AboutLeaseOrAgreementPartOne
  )(implicit sessionRequest: SessionRequest[?]): Session =
    val currentAboutLeaseOrAgreementPartOne = sessionRequest.sessionData.aboutLeaseOrAgreementPartOne

    val updatedAboutLeaseOrAgreementPartOne = currentAboutLeaseOrAgreementPartOne match {
      case Some(_) => sessionRequest.sessionData.aboutLeaseOrAgreementPartOne.map(copy)
      case _       => Some(copy(AboutLeaseOrAgreementPartOne()))
    }

    sessionRequest.sessionData.copy(aboutLeaseOrAgreementPartOne = updatedAboutLeaseOrAgreementPartOne)
