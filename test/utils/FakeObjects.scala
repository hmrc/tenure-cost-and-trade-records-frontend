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

package utils

import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration, RentIncludeFixturesAndFittingsDetails, RentOpenMarketValueDetails}
import models.submissions.aboutfranchisesorlettings._
import models.submissions.abouttheproperty.PremisesLicenseGrantedNo
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, _}
import models.submissions.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.LettingSection
import models.submissions.abouttheproperty._
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutYourTradingHistory}
import models.submissions.aboutyou.{AboutYou, CustomerDetails}
import models.submissions.additionalinformation._
import models.{AnnualRent, Session, SubmissionDraft, UserLoginDetails}
import models.submissions.common.{Address, AnswerNo, AnswerYes, ContactDetails}
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import form.DateMappings._

import java.time.LocalDate

trait FakeObjects {
  val prefilledStillConnectedDetailsYes         = StillConnectedDetails(Some(AddressConnectionTypeYes))
  val prefilledAddress                          = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")
  val prefilledContactDetails                   = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledFakeName                         = "John Doe"
  val prefilledContactAddress                   = AlternativeContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    Some("GORING-BY-SEA, WORTHING"),
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledCateringAddress                  =
    CateringAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLettingAddress                   =
    LettingAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLandlordAddress                  =
    LandlordAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledFirstOccupy                      = MonthsYearDuration(2000, 2)
  val prefilledFinancialYear                    = DayMonthsDuration(2, 12)
  val prefilledDateInput                        = LocalDate.of(2022, 6, 1)
  val prefilledBigDecimal                       = BigDecimal(9999999)
  val prefilledAnnualRent                       = AnnualRent(prefilledBigDecimal)
  val prefilledCurrentRentPayableWithin12Months =
    CurrentRentPayableWithin12Months(CurrentRentWithin12MonthsYes, prefilledDateInput)

  val prefilledCateringOperationSectionYes    = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerYes)
  )
  val prefilledCateringOperationSectionNo     = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerNo)
  )
  val prefilledLettingSectionYes              = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerYes)
  )
  val prefilledLettingSectionNo               = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerNo)
  )
  val prefilledAboutTheLandlord               =
    AboutTheLandlord(
      prefilledFakeName,
      prefilledLandlordAddress
    )
  val prefilledConnectedToLandlordDetails     =
    ConnectedToLandlordInformationDetails(
      "This is some test information"
    )
  val prefilledLeaseOrAgreementYearsDetails   =
    LeaseOrAgreementYearsDetails(
      AnswerYes,
      AnswerYes,
      AnswerYes
    )
  val prefilledLeaseOrAgreementYearsDetailsNo =
    LeaseOrAgreementYearsDetails(AnswerNo, AnswerNo, AnswerNo)
  val prefilledUserLoginDetails               =
    UserLoginDetails("Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", "FOR6010", "99996010004", prefilledAddress)
  val prefilledUserLoginDetails6015           =
    UserLoginDetails("Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", "FOR6015", "99996015001", prefilledAddress)
  val prefilledBaseSession                    = Session(prefilledUserLoginDetails)
  val submissionDraft                         = SubmissionDraft("FOR6010", prefilledBaseSession, "password")
  val prefilledRemoveConnection               =
    RemoveConnectionDetails(
      Some(
        RemoveConnectionsDetails(
          "John Smith",
          ContactDetails("12345678909", "test@email.com"),
          Some("Additional Information is here")
        )
      )
    )

  val prefilledAboutYou                 = AboutYou(Some(CustomerDetails("Tobermory", ContactDetails("12345678909", "test@email.com"))))
  val prefilledAboutThePropertyNo       = AboutTheProperty(
    Some(PropertyDetails("OccupierName", CurrentPropertyHotel, None)),
    Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    Some(PremisesLicenseGrantedNo),
    None,
    Some(LicensableActivitiesNo),
    None,
    Some(PremisesLicensesConditionsNo),
    None,
    Some(EnforcementActionsNo),
    None,
    Some(TiedGoodsNo),
    None
  )
  val prefilledAdditionalInformation    = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details string"))
  )
  val prefilledAltContactInformation    = AltContactInformation(
    Some(
      AlternativeContactDetails(
        "Full Alternative Contact Name",
        prefilledContactDetails,
        prefilledContactAddress
      )
    )
  )
  val prefilledAboutTheTradingHistory   = AboutTheTradingHistory(
    aboutYourTradingHistory = Some(
      AboutYourTradingHistory(
        prefilledFirstOccupy,
        prefilledFinancialYear
      )
    )
  )
  val prefilledAboutFranchiseOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledLettingSectionYes)
  )

  val prefilledAboutFranchiseOrLettingsNo = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledLettingSectionNo)
  )

  val prefilledAboutFranchiseOrLettings6015 = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledLettingSectionYes)
  )

  val prefilledAboutFranchiseOrLettingsNo6015 = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledLettingSectionNo)
  )

  val prefilledAboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    None,
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartOneNo = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    None,
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetailsNo),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerNo)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerNo)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerNo))
  )
}
