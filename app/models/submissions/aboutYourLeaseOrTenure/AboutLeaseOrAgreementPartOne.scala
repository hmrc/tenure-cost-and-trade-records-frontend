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

//There are now 22 attributes in this case class, so no more can be added
case class AboutLeaseOrAgreementPartOne(
  aboutTheLandlord: Option[AboutTheLandlord] = None,
  connectedToLandlord: Option[AnswersYesNo] = None,
  connectedToLandlordDetails: Option[ConnectedToLandlordInformationDetails] = None,
  leaseOrAgreementYearsDetails: Option[LeaseOrAgreementYearsDetails] = None,
  currentRentPayableWithin12Months: Option[CurrentRentPayableWithin12Months] = None,
  propertyUseLeasebackAgreement: Option[PropertyUseLeasebackArrangement] = None,
  annualRent: Option[BigDecimal] = None,
  currentRentFirstPaid: Option[CurrentRentFirstPaid] = None,
  currentLeaseOrAgreementBegin: Option[CurrentLeaseOrAgreementBegin] = None,
  includedInYourRentDetails: Option[IncludedInYourRentDetails] = None,
  doesTheRentPayable: Option[DoesTheRentPayable] = None,
  sharedResponsibilitiesDetails: Option[SharedResponsibilitiesDetails] = None,
  rentIncludeTradeServicesDetails: Option[RentIncludeTradeServicesDetails] = None,
  rentIncludeTradeServicesInformation: Option[RentIncludeTradeServicesInformationDetails] = None,
  rentIncludeFixturesAndFittingsDetails: Option[RentIncludeFixturesAndFittingsDetails] = None,
  rentIncludeFixtureAndFittingsDetails: Option[RentIncludeFixturesOrFittingsInformationDetails] = None,
  rentOpenMarketValueDetails: Option[RentOpenMarketValueDetails] = None,
  whatIsYourCurrentRentBasedOnDetails: Option[WhatIsYourCurrentRentBasedOnDetails] = None,
  rentIncreasedAnnuallyWithRPIDetails: Option[RentIncreasedAnnuallyWithRPIDetails] = None,
  checkYourAnswersAboutYourLeaseOrTenure: Option[AnswersYesNo] = None,
  rentIncludesVat: Option[RentIncludesVatDetails] = None
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
