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

import crypto.MongoCrypto
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveAboutLeaseOrAgreementPartOne(
  aboutTheLandlord: Option[SensitiveAboutTheLandlord] = None,
  connectedToLandlord: Option[AnswersYesNo] = None,
  connectedToLandlordDetails: Option[String] = None,
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
) extends Sensitive[AboutLeaseOrAgreementPartOne]:
  override def decryptedValue: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    aboutTheLandlord.map(_.decryptedValue),
    connectedToLandlord,
    connectedToLandlordDetails,
    leaseOrAgreementYearsDetails,
    currentRentPayableWithin12Months,
    propertyUseLeasebackAgreement,
    annualRent,
    currentRentFirstPaid,
    currentLeaseOrAgreementBegin,
    includedInYourRentDetails,
    doesTheRentPayable,
    sharedResponsibilitiesDetails,
    rentIncludeTradeServicesDetails,
    rentIncludeTradeServicesInformation,
    rentIncludeFixturesAndFittingsDetails,
    rentIncludeFixtureAndFittingsDetails,
    rentOpenMarketValueDetails,
    whatIsYourCurrentRentBasedOnDetails,
    rentIncreasedAnnuallyWithRPIDetails,
    checkYourAnswersAboutYourLeaseOrTenure,
    rentIncludesVat
  )

object SensitiveAboutLeaseOrAgreementPartOne:

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAboutLeaseOrAgreementPartOne] = Json.format

  def apply(aboutLeaseOrAgreementPartOne: AboutLeaseOrAgreementPartOne): SensitiveAboutLeaseOrAgreementPartOne =
    SensitiveAboutLeaseOrAgreementPartOne(
      aboutLeaseOrAgreementPartOne.aboutTheLandlord.map(SensitiveAboutTheLandlord(_)),
      aboutLeaseOrAgreementPartOne.connectedToLandlord,
      aboutLeaseOrAgreementPartOne.connectedToLandlordDetails,
      aboutLeaseOrAgreementPartOne.leaseOrAgreementYearsDetails,
      aboutLeaseOrAgreementPartOne.currentRentPayableWithin12Months,
      aboutLeaseOrAgreementPartOne.propertyUseLeasebackAgreement,
      aboutLeaseOrAgreementPartOne.annualRent,
      aboutLeaseOrAgreementPartOne.currentRentFirstPaid,
      aboutLeaseOrAgreementPartOne.currentLeaseOrAgreementBegin,
      aboutLeaseOrAgreementPartOne.includedInYourRentDetails,
      aboutLeaseOrAgreementPartOne.doesTheRentPayable,
      aboutLeaseOrAgreementPartOne.sharedResponsibilitiesDetails,
      aboutLeaseOrAgreementPartOne.rentIncludeTradeServicesDetails,
      aboutLeaseOrAgreementPartOne.rentIncludeTradeServicesInformation,
      aboutLeaseOrAgreementPartOne.rentIncludeFixturesAndFittingsDetails,
      aboutLeaseOrAgreementPartOne.rentIncludeFixtureAndFittingsDetails,
      aboutLeaseOrAgreementPartOne.rentOpenMarketValueDetails,
      aboutLeaseOrAgreementPartOne.whatIsYourCurrentRentBasedOnDetails,
      aboutLeaseOrAgreementPartOne.rentIncreasedAnnuallyWithRPIDetails,
      aboutLeaseOrAgreementPartOne.checkYourAnswersAboutYourLeaseOrTenure,
      aboutLeaseOrAgreementPartOne.rentIncludesVat
    )
