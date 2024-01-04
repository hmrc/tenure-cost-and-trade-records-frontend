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
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, _}
import models.submissions.{ConnectedSubmission, NotConnectedSubmission, aboutfranchisesorlettings}
import models.submissions.aboutfranchisesorlettings.LettingSection
import models.submissions.aboutyouandtheproperty._
import models.submissions.aboutthetradinghistory._
import models.submissions.additionalinformation._
import models.{AnnualRent, ForTypes, Session, SubmissionDraft}
import models.submissions.common.{Address, AnswerNo, AnswerYes, ContactDetails, ContactDetailsAddress}
import models.submissions.connectiontoproperty._
import models.submissions.connectiontoproperty.StartDateOfVacantProperty
import models.submissions.downloadFORTypeForm.{DownloadPDF, DownloadPDFDetails, DownloadPDFReferenceNumber}
import models.submissions.notconnected.{PastConnectionTypeYes, RemoveConnectionDetails, RemoveConnectionsDetails}
import models.submissions.requestReferenceNumber.{CheckYourAnswersRequestReferenceNumber, RequestReferenceNumber, RequestReferenceNumberAddress, RequestReferenceNumberContactDetails, RequestReferenceNumberDetails}

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String   = "99996010004"
  val forType6010: String       = "FOR6010"
  val forType6011: String       = "FOR6011"
  val forType6015: String       = "FOR6015"
  val forType6016: String       = "FOR6016"
  val prefilledAddress: Address =
    Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX")
  val token: String             = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

  val prefilledContactDetails: ContactDetails                            = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledContactAddress: ContactDetailsAddress                     = ContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledAlternativeAddress: AlternativeAddress                    = AlternativeAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledNoRefContactDetails: RequestReferenceNumberContactDetails =
    RequestReferenceNumberContactDetails("test", prefilledContactDetails, Some("test"))

  val prefilledFakeName                                                 = "John Doe"
  val prefilledFakePhoneNo                                              = "12345678901"
  val prefilledFakeEmail                                                = "test@email.com"
  val prefilledCateringAddress: CateringAddress                         =
    CateringAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledLettingAddress: LettingAddress                           =
    LettingAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledLandlordAddress: LandlordAddress                         =
    LandlordAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledEditAddress: EditAddress                                 =
    EditAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledNoReferenceContactAddress: RequestReferenceNumberAddress =
    RequestReferenceNumberAddress(
      "004",
      Some("GORING ROAD"),
      "GORING-BY-SEA, WORTHING",
      Some("West sussex"),
      "BN12 4AX"
    )

  val prefilledDateInput: LocalDate               = LocalDate.of(2022, 6, 1)
  val prefilledMonthYearInput: MonthsYearDuration = MonthsYearDuration(6, 2000)

  val prefilledTradingNameOperatingFromProperty: TradingNameOperatingFromProperty = TradingNameOperatingFromProperty(
    "TRADING NAME"
  )

  val baseFilled6010Session: Session = Session(referenceNumber, forType6010, prefilledAddress, token)
  val baseFilled6011Session: Session = Session(referenceNumber, forType6011, prefilledAddress, token)
  val baseFilled6015Session: Session = Session(referenceNumber, forType6015, prefilledAddress, token)
  val baseFilled6016Session: Session = Session(referenceNumber, forType6016, prefilledAddress, token)

  // Are your still connected sessions
  val prefilledStillConnectedDetailsYes: StillConnectedDetails  = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee)
  )
  val prefilledStillConnectedDetailsEdit: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYesChangeAddress),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress)
  )

  val prefilledStillConnectedDetailsYesToAll: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(EditTheAddress(EditAddress("Street 1", Some("Street 2"), "Town", Some("County"), "BN12 4AX"))),
    Some(VacantProperties(VacantPropertiesDetailsYes)),
    Some(TradingNameOperatingFromProperty("ABC LTD")),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(StartDateOfVacantProperty(prefilledDateInput)),
    Some(AnswerYes),
    Some(ProvideContactDetails(YourContactDetails("fullname", prefilledContactDetails, Some("additional info")))),
    lettingPartOfPropertyDetailsIndex = 0,
    lettingPartOfPropertyDetails = IndexedSeq(
      LettingPartOfPropertyDetails(
        TenantDetails(
          "name",
          "billboard",
          CorrespondenceAddress("building", Some("street"), "town", Some("county"), "BN12 4AX")
        ),
        Some(LettingPartOfPropertyRentDetails(2000, prefilledDateInput)),
        List("Other"),
        addAnotherLettingToProperty = Some(AnswerYes)
      )
    ),
    checkYourAnswersConnectionToProperty = None,
    checkYourAnswersConnectionToVacantProperty = None
  )

  val prefilledStillConnectedDetailsNoToAll: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeNo),
    Some(ConnectionToThePropertyOccupierAgent),
    Some(EditTheAddress(EditAddress("Street 1", Some("Street 2"), "Town", Some("County"), "BN12 4AX"))),
    Some(VacantProperties(VacantPropertiesDetailsNo)),
    Some(TradingNameOperatingFromProperty("ABC LTD")),
    Some(AnswerNo),
    Some(AnswerNo),
    Some(AnswerNo),
    Some(StartDateOfVacantProperty((LocalDate.now()))),
    Some(AnswerNo),
    Some(prefilledProvideContactDetails)
  )

  val prefilledStillConnectedDetailsNo: StillConnectedDetails = StillConnectedDetails(Some(AddressConnectionTypeNo))
  val stillConnectedDetailsYesSession: Session                =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetailsYesToAllSession: Session           =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll))
  val stillConnectedDetailsEditSession: Session               =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsEdit))
  val stillConnectedDetailsNoSession: Session                 =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetailsNoToAllSession: Session            =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNoToAll))
  val stillConnectedDetails6011YesSession: Session            =
    baseFilled6011Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6011NoSession: Session             =
    baseFilled6011Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6015YesSession: Session            =
    baseFilled6015Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6015NoSession: Session             =
    baseFilled6015Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6016YesSession: Session            =
    baseFilled6016Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6016NoSession: Session             =
    baseFilled6016Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetailsConnectedDetailsNoSession: Session =
    baseFilled6010Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))

  // Not connected sessions
  val prefilledNotConnectedYes: RemoveConnectionDetails = RemoveConnectionDetails(
    Some(
      RemoveConnectionsDetails(
        prefilledFakeName,
        ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail),
        Some("Additional Info")
      )
    ),
    Some(PastConnectionTypeYes)
  )

  val notConnected6010NoSession: Session =
    stillConnectedDetailsConnectedDetailsNoSession.copy(removeConnectionDetails = Some(prefilledNotConnectedYes))

  // About you and the property sessions
  val prefilledAboutYouAndThePropertyYes: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(PropertyDetails(CurrentPropertyHotel, None)),
    Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    Some(AnswerYes),
    Some(PremisesLicenseGrantedInformationDetails("Premises licence granted details")),
    Some(AnswerYes),
    Some(LicensableActivitiesInformationDetails("Licensable activities details")),
    Some(AnswerYes),
    Some(PremisesLicenseConditionsDetails("Premises license conditions details")),
    Some(AnswerYes),
    Some(EnforcementActionHasBeenTakenInformationDetails("Enforcement action taken details")),
    Some(AnswerYes),
    Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie))
  )
  val prefilledAboutYouAndThePropertyNo: AboutYouAndTheProperty  = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(PropertyDetails(CurrentPropertyHotel, None)),
    Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    Some(AnswerNo),
    None,
    Some(AnswerNo),
    None,
    Some(AnswerNo),
    None,
    Some(AnswerNo),
    None,
    Some(AnswerNo),
    None
  )

  val prefilledProvideContactDetails: ProvideContactDetails = ProvideContactDetails(
    YourContactDetails(
      fullName = "Tobermory",
      contactDetails = ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail),
      additionalInformation = Some("Additional information")
    )
  )

  val aboutYouAndTheProperty6010YesSession: Session =
    stillConnectedDetailsYesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes))
  val aboutYouAndTheProperty6010NoSession: Session  =
    stillConnectedDetailsNoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))
  val aboutYouAndTheProperty6011YesSession: Session =
    stillConnectedDetails6011YesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes))
  val aboutYouAndTheProperty6011NoSession: Session  =
    stillConnectedDetails6011NoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))
  val aboutYouAndTheProperty6015YesSession: Session =
    stillConnectedDetails6015YesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes))
  val aboutYouAndTheProperty6015NoSession: Session  =
    stillConnectedDetails6015NoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))
  val aboutYouAndTheProperty6016YesSession: Session =
    stillConnectedDetails6016YesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes))
  val aboutYouAndTheProperty6016NoSession: Session  =
    stillConnectedDetails6016NoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))

  // Trading history
  val prefilledAboutYourTradingHistory: AboutTheTradingHistory = AboutTheTradingHistory(
    Some(OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), Some(DayMonthsDuration(27, 9)))),
    Seq(
      TurnoverSection(
        LocalDate.now(),
        123,
        Some(BigDecimal(234)),
        Some(BigDecimal(345)),
        Some(BigDecimal(456)),
        Some(BigDecimal(567)),
        Some(BigDecimal(678))
      ),
      TurnoverSection(
        LocalDate.now().minusYears(1),
        123,
        Some(BigDecimal(234)),
        Some(BigDecimal(345)),
        Some(BigDecimal(456)),
        Some(BigDecimal(567)),
        Some(BigDecimal(678))
      ),
      TurnoverSection(
        LocalDate.now().minusYears(2),
        123,
        Some(BigDecimal(234)),
        Some(BigDecimal(345)),
        Some(BigDecimal(456)),
        Some(BigDecimal(567)),
        Some(BigDecimal(678))
      )
    ),
    costOfSales = Seq(
      CostOfSales(
        LocalDate.now(),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      ),
      CostOfSales(
        LocalDate.now().minusYears(1),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      ),
      CostOfSales(
        LocalDate.now().minusYears(2),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      )
    ),
    totalPayrollCostSections = Seq(
      TotalPayrollCost(
        LocalDate.now(),
        BigDecimal(1),
        BigDecimal(2)
      ),
      TotalPayrollCost(
        LocalDate.now().minusYears(1),
        BigDecimal(1),
        BigDecimal(2)
      ),
      TotalPayrollCost(
        LocalDate.now().minusYears(2),
        BigDecimal(1),
        BigDecimal(2)
      )
    ),
    fixedOperatingExpensesSections = Seq(
      FixedOperatingExpenses(
        LocalDate.now(),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1)
      ),
      FixedOperatingExpenses(
        LocalDate.now().minusYears(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1)
      ),
      FixedOperatingExpenses(
        LocalDate.now().minusYears(2),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1),
        BigDecimal(1)
      )
    ),
    variableOperatingExpensesSections = VariableOperatingExpensesSections(
      Seq(
        VariableOperatingExpenses(
          LocalDate.now(),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1))
        ),
        VariableOperatingExpenses(
          LocalDate.now().minusYears(1),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1))
        ),
        VariableOperatingExpenses(
          LocalDate.now().minusYears(2),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1)),
          Some(BigDecimal(1))
        )
      ),
      Some("Other expenses details for all years")
    ),
    otherCosts = Some(
      OtherCosts(
        otherCosts = Seq(
          OtherCost(
            LocalDate.now(),
            Some(BigDecimal(1)),
            Some(BigDecimal(1))
          ),
          OtherCost(
            LocalDate.now().minusYears(1),
            Some(BigDecimal(1)),
            Some(BigDecimal(1))
          ),
          OtherCost(
            LocalDate.now().minusYears(2),
            Some(BigDecimal(1)),
            Some(BigDecimal(1))
          )
        ),
        Some("Other Costs Details")
      )
    )
  )

  val aboutYourTradingHistory6010YesSession: Session =
    aboutYouAndTheProperty6010YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  val aboutYourTradingHistory6015YesSession: Session =
    aboutYouAndTheProperty6015YesSession.copy(
      aboutTheTradingHistory = Some(
        prefilledAboutYourTradingHistory
          .copy(
            costOfSales = Seq(CostOfSales(LocalDate.now, None, None, None, None)),
            otherCosts = Some(OtherCosts(otherCosts = Seq(OtherCost(LocalDate.now, None, None))))
          )
      ),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  // Franchises or lettings
  val prefilledCateringOperationSectionYes: CateringOperationSection = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(RentReceivedFrom(BigDecimal(1500), true)),
    Some(CalculatingTheRent("test", prefilledDateInput)),
    Some(AnswerYes)
  )
  val prefilledCateringOperationSectionNo: CateringOperationSection  = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(RentReceivedFrom(BigDecimal(1500), true)),
    Some(CalculatingTheRent("test", prefilledDateInput)),
    Some(AnswerNo)
  )
  val prefilledLettingSectionYes: LettingSection                     = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerYes)
  )
  val prefilledLettingSectionNo: LettingSection                      = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerNo)
  )

  val prefilledAboutFranchiseOrLettings: AboutFranchisesOrLettings       = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledLettingSectionYes)
  )
  val prefilledAboutFranchiseOrLettingsNo: AboutFranchisesOrLettings     = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledLettingSectionNo)
  )
  val prefilledAboutFranchiseOrLettings6015: AboutFranchisesOrLettings   = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledLettingSectionYes)
  )
  val prefilledAboutFranchiseOrLettingsNo6015: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(AnswerNo),
    0,
    IndexedSeq(prefilledLettingSectionNo)
  )

  val sessionAboutFranchiseOrLetting6010YesSession: Session =
    aboutYouAndTheProperty6010YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings))
  val sessionAboutFranchiseOrLetting6010NoSession: Session  =
    aboutYouAndTheProperty6010NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo))
  val sessionAboutFranchiseOrLetting6015YesSession: Session =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6015))
  val sessionAboutFranchiseOrLetting6015NoSession: Session  =
    aboutYouAndTheProperty6015NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo6015))

  // Additional information sessions
  val prefilledAdditionalInformation: AdditionalInformation = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details")),
    Some(ContactDetailsQuestion(AnswerYes)),
    Some(
      AlternativeContactDetails("Full name", prefilledContactDetails, prefilledAlternativeAddress)
    ),
    Some(CheckYourAnswersAdditionalInformation("CYA"))
  )

  val additionalInformationSession: Session =
    stillConnectedDetailsYesSession.copy(additionalInformation = Some(prefilledAdditionalInformation))

  val prefilledFirstOccupy                      = MonthsYearDuration(2, 2000)
  val prefilledFinancialYear                    = Some(DayMonthsDuration(2, 12))
  val prefilledBigDecimal                       = BigDecimal(9999999)
  val prefilledAnnualRent                       = AnnualRent(prefilledBigDecimal)
  val prefilledCurrentRentPayableWithin12Months =
    CurrentRentPayableWithin12Months(CurrentRentWithin12MonthsYes, Some(prefilledDateInput))

  val prefilledAboutTheLandlord =
    AboutTheLandlord(
      prefilledFakeName,
      prefilledLandlordAddress
    )

  val prefilledEditTheAddress =
    EditTheAddress(
      prefilledEditAddress
    )

  val prefilledNoRefAddress =
    RequestReferenceNumber(
      "Business Name",
      prefilledNoReferenceContactAddress
    )

  val prefilledVacantPropertiesDetails =
    VacantProperties(
      VacantPropertiesDetailsYes
    )

  val prefilledConnectedToLandlordDetails     =
    ConnectedToLandlordInformationDetails(
      "This is some test information"
    )
  val prefilledLeaseOrAgreementYearsDetails   =
    LeaseOrAgreementYearsDetails(
      TenancyThreeYearsYes,
      RentThreeYearsYes,
      UnderReviewYes
    )
  val prefilledLeaseOrAgreementYearsDetailsNo =
    LeaseOrAgreementYearsDetails(TenancyThreeYearsNo, RentThreeYearsNo, UnderReviewNo)
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
  val connectedSubmission                     = ConnectedSubmission(stillConnectedDetailsYesToAllSession)

  val notConnectedSubmission = NotConnectedSubmission(
    "id",
    ForTypes.for6010,
    prefilledAddress,
    "John Smith",
    Some("test@test.com"),
    Some("12312312312"),
    Some("additional info"),
    Instant.now(),
    false
  )

  val prefilledAboutTheTradingHistory = AboutTheTradingHistory(
    occupationAndAccountingInformation = Some(
      OccupationalAndAccountingInformation(
        prefilledFirstOccupy,
        prefilledFinancialYear
      )
    )
  )

  val prefilledVacantProperties = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress)
  )

  val prefilledNotVacantPropertiesCYA = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress),
    Some(prefilledVacantPropertiesDetails),
    Some(prefilledTradingNameOperatingFromProperty),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(AnswerNo)
  )

  val prefilledAboutLeaseOrAgreement6010Route = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(AnswerYes),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)),
    whatIsYourCurrentRentBasedOnDetails =
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnIndexedToRPI, Some("Test")))
  )

  val prefilledAboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    None,
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(AnswerYes),
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
    Some(AnswerYes),
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

  val prefilledRequestReferenceNumber = RequestReferenceNumberDetails(
    Some(RequestReferenceNumber(prefilledFakeName, prefilledNoReferenceContactAddress)),
    Some(RequestReferenceNumberContactDetails(prefilledFakeName, prefilledContactDetails, Some("test"))),
    Some(CheckYourAnswersRequestReferenceNumber("CYA"))
  )

  val prefilledDownloadPDFRef: DownloadPDFDetails = DownloadPDFDetails(
    Some(DownloadPDFReferenceNumber(referenceNumber)),
    Some(DownloadPDF(forType6010))
  )
}
