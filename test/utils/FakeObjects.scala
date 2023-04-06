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

import models.submissions.Form6010._
import models.submissions.aboutfranchisesorlettings._
import models.submissions.aboutyouandtheproperty.PremisesLicenseGrantedNo
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, _}
import models.submissions.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.LettingSection
import models.submissions.aboutyouandtheproperty._
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutYourTradingHistory}
import models.submissions.additionalinformation._
import models.{AnnualRent, Session, SubmissionDraft}
import models.submissions.common.{Address, AnswerNo, AnswerYes, ContactDetails, ContactDetailsAddress}
import models.submissions.connectiontoproperty._
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}

import java.time.LocalDate

trait FakeObjects {
  val referenceNumber: String   = "99996010004"
  val forType6010: String       = "FOR6010"
  val prefilledAddress: Address = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")
  val token: String             = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

  val prefilledContactDetails: ContactDetails        = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledContactAddress: ContactDetailsAddress = ContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )

  val prefilledFakeName        = "John Doe"
  val prefilledCateringAddress =
    CateringAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLettingAddress  =
    LettingAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")
  val prefilledLandlordAddress =
    LandlordAddress("004", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("West sussex"), "BN12 4AX")

  val baseFilled6010Session = Session(referenceNumber, forType6010, prefilledAddress, token)

  // Are your still connected sessions
  val prefilledStillConnectedDetailsYes: StillConnectedDetails  = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee)
  )
  val prefilledStillConnectedDetailsEdit: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYesChangeAddress),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledAddress)
  )
  val prefilledStillConnectedDetailsNo: StillConnectedDetails   = StillConnectedDetails(Some(AddressConnectionTypeNo))

  val stillConnectedDetailsYesSession: Session  =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetailsEditSession: Session =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsEdit))
  val stillConnectedDetailsNoSession: Session   =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))

  //  Additional information
  val prefilledAdditionalInformation: AdditionalInformation = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details")),
    Some(AlternativeContactDetails("Full name", prefilledContactDetails, prefilledContactAddress)),
    Some(CheckYourAnswersAdditionalInformation("CYA"))
  )

  val additionalInformationSession: Session =
    stillConnectedDetailsYesSession.copy(additionalInformation = Some(prefilledAdditionalInformation))

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
  val prefilledBaseSession                    =
    Session(
      "99996010004",
      "FOR6010",
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      saveAsDraftPassword = Some("pass")
    )
  val submissionDraft                         = SubmissionDraft("FOR6010", prefilledBaseSession, "/send-trade-and-cost-information/about-you")
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

  val prefilledAboutYouAndThePropertyNo = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails("12345678909", "test@email.com"))),
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

  val prefilledAboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartTwoNo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerNo)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerNo)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerNo)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerNo))
  )
}
