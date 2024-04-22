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
import models.submissions.requestReferenceNumber._

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String   = "99996010004"
  val forType6010: String       = "FOR6010"
  val forType6011: String       = "FOR6011"
  val forType6015: String       = "FOR6015"
  val forType6016: String       = "FOR6016"
  val forType6030: String       = "FOR6030"
  val forType6020: String       = "FOR6020"
  val prefilledAddress: Address =
    Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX")
  val token: String             = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

  val prefilledContactDetails: ContactDetails         = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledContactAddress: ContactDetailsAddress  = ContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledAlternativeAddress: AlternativeAddress = AlternativeAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )

  val prefilledAlternativeCONTACTAddress: AlternativeContactDetails = AlternativeContactDetails(
    AlternativeAddress(
      "004",
      Some("GORING ROAD"),
      "WORTHING",
      Some("West sussex"),
      "BN12 4AX"
    )
  )

  val prefilledNoRefContactDetails: RequestReferenceNumberContactDetails =
    RequestReferenceNumberContactDetails("test", prefilledContactDetails, Some("test"))

  val prefilledFakeName                                                 = "John Doe"
  val prefilledFakePhoneNo                                              = "12345678901"
  val prefilledFakeEmail                                                = "test@email.com"
  val prefilledFakeTradingName                                          = "TRADING NAME"
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
  val baseFilled6030Session: Session = Session(referenceNumber, forType6030, prefilledAddress, token)
  val baseFilled6020Session: Session = Session(referenceNumber, forType6020, prefilledAddress, token)

  // Request reference number
  val prefilledRequestRefNumCYA   = RequestReferenceNumberDetails(
    Some(RequestReferenceNumber(prefilledFakeTradingName, prefilledNoReferenceContactAddress)),
    Some(
      RequestReferenceNumberContactDetails(
        prefilledFakeName,
        ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail),
        Some("Additional Information")
      )
    ),
    Some(CheckYourAnswersRequestReferenceNumber("CYARequestRefNum"))
  )
  val prefilledRequestRefNumBlank = RequestReferenceNumberDetails()

  // Are your still connected sessions
  val prefilledStillConnectedDetailsYes: StillConnectedDetails                       = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee)
  )
  val prefilledStillConnectedDetailsEdit: StillConnectedDetails                      = StillConnectedDetails(
    Some(AddressConnectionTypeYesChangeAddress),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress),
    Some(VacantProperties(VacantPropertiesDetailsNo))
  )
  val prefilledStillConnectedDetailsNoneOwnProperty: StillConnectedDetails           = StillConnectedDetails(
    Some(AddressConnectionTypeYesChangeAddress),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress),
    None,
    Some(TradingNameOperatingFromProperty("ABC LTD"))
  )
  val prefilledStillConnectedDetailsYesRentReceivedNoLettings: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeNo),
    Some(ConnectionToThePropertyOccupierAgent),
    Some(EditTheAddress(EditAddress("Street 1", Some("Street 2"), "Town", Some("County"), "BN12 4AX"))),
    Some(VacantProperties(VacantPropertiesDetailsNo)),
    Some(TradingNameOperatingFromProperty("ABC LTD")),
    Some(AnswerNo),
    Some(AnswerNo),
    Some(AnswerNo),
    Some(StartDateOfVacantProperty((LocalDate.now()))),
    Some(AnswerYes)
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

  val stillConnectedDetails6030NoSession: Session  =
    baseFilled6030Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6030YesSession: Session =
    baseFilled6030Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))

  val stillConnectedDetails6020NoSession: Session  =
    baseFilled6020Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6020YesSession: Session =
    baseFilled6020Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))

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
  val prefilledAboutYouAndThePropertyBlank: AboutYouAndTheProperty = AboutYouAndTheProperty()

  val prefilledAboutYouAndThePropertyYes: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(ContactDetailsQuestion(AnswerYes)),
    Some(
      AlternativeContactDetails(prefilledAlternativeAddress)
    ),
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
    Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie)),
    checkYourAnswersAboutTheProperty = Some(CheckYourAnswersAboutYourProperty("Yes")),
    charityQuestion = Some(AnswerYes),
    tradingActivity = Some(TradingActivity(AnswerYes, Some("Trading activity details")))
  )

  val prefilledAboutYouAndThePropertyYesString: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(ContactDetailsQuestion(AnswerYes)),
    Some(
      AlternativeContactDetails(prefilledAlternativeAddress)
    ),
    None,
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
    Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie)),
    None,
    Some(PropertyDetailsString("This property is a museum"))
  )

  val prefilledAboutYouAndThePropertyNoString: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(ContactDetailsQuestion(AnswerNo)),
    None,
    None,
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
    None,
    None
  )

  val prefilledAboutYouAndThePropertyNo: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Tobermory", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(ContactDetailsQuestion(AnswerNo)),
    None,
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
    None,
    checkYourAnswersAboutTheProperty = Some(CheckYourAnswersAboutYourProperty("no")),
    charityQuestion = Some(AnswerNo)
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
  val aboutYouAndTheProperty6020YesSession: Session =
    stillConnectedDetails6020YesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes))
  val aboutYouAndTheProperty6020NoSession: Session  =
    stillConnectedDetails6020NoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))
  val aboutYouAndTheProperty6030YesSession: Session =
    stillConnectedDetails6030YesSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYesString))
  val aboutYouAndTheProperty6030NoSession: Session  =
    stillConnectedDetails6030NoSession.copy(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNoString))

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
        Some(BigDecimal(1)),
        Some(BigDecimal(2))
      ),
      TotalPayrollCost(
        LocalDate.now().minusYears(1),
        Some(BigDecimal(1)),
        Some(BigDecimal(2))
      ),
      TotalPayrollCost(
        LocalDate.now().minusYears(2),
        Some(BigDecimal(1)),
        Some(BigDecimal(2))
      )
    ),
    fixedOperatingExpensesSections = Seq(
      FixedOperatingExpenses(
        LocalDate.now(),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      ),
      FixedOperatingExpenses(
        LocalDate.now().minusYears(1),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      ),
      FixedOperatingExpenses(
        LocalDate.now().minusYears(2),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1)),
        Some(BigDecimal(1))
      )
    ),
    variableOperatingExpenses = Some(
      VariableOperatingExpensesSections(
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
      )
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

  val prefilledAboutYourTradingHistory6020: AboutTheTradingHistory = AboutTheTradingHistory(
    Some(OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), Some(DayMonthsDuration(27, 9)))),
    turnoverSections6020 = Some(
      Seq(
        TurnoverSection6020(LocalDate.now(), Some(BigDecimal(100)), Some(100)),
        TurnoverSection6020(LocalDate.now().minusYears(1), Some(BigDecimal(200)), Some(200)),
        TurnoverSection6020(LocalDate.now().minusYears(2), Some(BigDecimal(300)), Some(300))
      )
    ),
    electricVehicleChargingPoints = Some(ElectricVehicleChargingPoints(AnswerYes, Some(123)))
  )

  val prefilledAboutYourTradingHistory6030: AboutTheTradingHistory = AboutTheTradingHistory(
    Some(OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), Some(DayMonthsDuration(27, 9)))),
    Seq.empty,
    None,
    Seq(
      TurnoverSection6030(LocalDate.now(), 52, Some(BigDecimal(100)), Some(100)),
      TurnoverSection6030(LocalDate.now().minusYears(1), 52, Some(BigDecimal(200)), Some(200)),
      TurnoverSection6030(LocalDate.now().minusYears(2), 52, Some(BigDecimal(300)), Some(300))
    ),
    Seq.empty,
    Seq.empty,
    None,
    Seq.empty,
    None,
    None,
    Seq.empty,
    Some(UnusualCircumstances("unusual circumstances"))
  )

  val aboutYourTradingHistory6010YesSession: Session =
    aboutYouAndTheProperty6010YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  val aboutYourTradingHistory6030YesSession: Session =
    aboutYouAndTheProperty6030YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6030),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  val aboutYourTradingHistory6015YesSession: Session =
    aboutYouAndTheProperty6015YesSession.copy(
      aboutTheTradingHistory = Some(
        prefilledAboutYourTradingHistory
          .copy(
            costOfSales = Seq(CostOfSales(LocalDate.now, None, None, None, None)),
            otherCosts = Some(OtherCosts(otherCosts = Seq(OtherCost(LocalDate.now, None, None)))),
            totalPayrollCostSections = Seq(TotalPayrollCost(LocalDate.now, None, None))
          )
      ),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  val aboutYourTradingHistory6020YesSession: Session =
    aboutYouAndTheProperty6020YesSession.copy(
      aboutTheTradingHistory = Some(
        prefilledAboutYourTradingHistory6020
          .copy(
            totalFuelSold = Some(Seq(TotalFuelSold(LocalDate.now(), None)))
          )
      ),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll)
    )

  val aboutYourTradingHistoryWithBunkerFuelCardsDetailsSession: Session =
    aboutYourTradingHistory6020YesSession.copy(
      aboutTheTradingHistory = prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails
    )

  val aboutYourTradingHistoryWithLowMarginFuelCardsDetailsSession: Session =
    aboutYourTradingHistory6020YesSession.copy(
      aboutTheTradingHistory = prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails
    )

  // Franchises or lettings
  val prefilledCateringOperationSectionYes: CateringOperationSection = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(RentReceivedFrom(BigDecimal(1500), true)),
    Some(CalculatingTheRent("test", prefilledDateInput)),
    Some(AnswerYes),
    itemsInRent = List("Other")
  )

  val prefilledCateringOperationBusinessSectionYes: CateringOperationBusinessSection = CateringOperationBusinessSection(
    CateringOperationBusinessDetails("Operator Name", "Type of Business", "Describe business"),
    Some(FeeReceived(Seq(FeeReceivedPerYear(LocalDate.now, 52, Some(1000))), Some("Fee calculation details"))),
    Some(AnswerYes)
  )

  val prefilledCateringOperationSectionIncompleteCatering: CateringOperationSection = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(RentReceivedFrom(BigDecimal(1500), true)),
    Some(CalculatingTheRent("test", prefilledDateInput))
  )

  val prefilledCateringOperationBusinessSectionIncompleteCatering: CateringOperationBusinessSection =
    CateringOperationBusinessSection(
      CateringOperationBusinessDetails("Operator Name", "Type of Business", "Describe business")
    )

  val prefilledCateringOperationSectionIncompleteCateringRentDetails: CateringOperationSection =
    CateringOperationSection(
      CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress)
    )

  val prefilledCateringBusinessOperationSectionIncompleteCateringRentDetails: CateringOperationBusinessSection =
    CateringOperationBusinessSection(
      CateringOperationBusinessDetails("Operator Name", "Type of Business", "Describe business")
    )

  val prefilledCateringOperationSectionNo: CateringOperationSection = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(RentReceivedFrom(BigDecimal(1500), true)),
    Some(CalculatingTheRent("test", prefilledDateInput)),
    Some(AnswerNo),
    itemsInRent = List("Other")
  )

  val prefilledCateringOperationBusinessSectionNo: CateringOperationBusinessSection = CateringOperationBusinessSection(
    CateringOperationBusinessDetails("Operator Name", "Type of Business", "Describe business"),
    None,
    Some(AnswerNo)
  )

  val prefilledLettingSectionYes: LettingSection               = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(LettingOtherPartOfPropertyRent6015Details(BigDecimal(1500), prefilledDateInput, true)),
    Some(AnswerYes),
    itemsInRent = List("Other")
  )
  val prefilledLettingSectionIncompleteLetting: LettingSection = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(LettingOtherPartOfPropertyRent6015Details(BigDecimal(1500), prefilledDateInput, true))
  )
  val prefilledLettingSectionIncomplete: LettingSection        = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(LettingOtherPartOfPropertyRent6015Details(BigDecimal(1500), prefilledDateInput, true))
  )
  val prefilledLettingSectionNo: LettingSection                = LettingSection(
    aboutfranchisesorlettings.LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(LettingOtherPartOfPropertyRent6015Details(BigDecimal(1500), prefilledDateInput, true)),
    Some(AnswerNo),
    itemsInRent = List("Other")
  )

  val prefilledAboutFranchiseOrLettings6016: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionYes)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionYes),
    None,
    Some(false),
    Some(AnswerYes)
  )

  val prefilledAboutFranchiseOrLettings: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionYes)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionYes),
    None,
    Some(false),
    Some(AnswerYes)
  )

  val prefilledAboutFranchiseOrLettingsIncompleteLetting: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionIncompleteCateringRentDetails),
    Some(IndexedSeq(prefilledCateringBusinessOperationSectionIncompleteCateringRentDetails)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionIncomplete)
  )

  val prefilledAboutFranchiseOrLettingsNo: AboutFranchisesOrLettings                           = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionNo)),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledLettingSectionNo)
  )
  val prefilledAboutFranchiseOrLettings6015: AboutFranchisesOrLettings                         = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionYes)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionYes)
  )
  val prefilledIncompleteAboutFranchiseOrLettings6015: AboutFranchisesOrLettings               = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionIncompleteCatering),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionIncompleteCatering)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionIncompleteLetting)
  )
  val prefilledIncompleteCateringDetailAboutFranchiseOrLettings6015: AboutFranchisesOrLettings =
    AboutFranchisesOrLettings(
      Some(AnswerYes),
      Some(AnswerYes),
      0,
      None,
      IndexedSeq(prefilledCateringOperationSectionIncompleteCateringRentDetails),
      Some(IndexedSeq(prefilledCateringBusinessOperationSectionIncompleteCateringRentDetails)),
      Some(AnswerYes),
      0,
      None,
      IndexedSeq(prefilledLettingSectionIncompleteLetting)
    )

  val prefilledAboutFranchiseOrLettingsNo6015: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionNo)),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledLettingSectionNo)
  )

  val sessionAboutFranchiseOrLetting6010YesSession: Session                =
    aboutYouAndTheProperty6010YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings))
  val sessionAboutFranchiseOrLetting6010Incomplete: Session                =
    aboutYouAndTheProperty6010YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledAboutFranchiseOrLettingsIncompleteLetting)
    )
  val sessionAboutFranchiseOrLetting6010NoSession: Session                 =
    aboutYouAndTheProperty6010NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo))
  val sessionAboutFranchiseOrLetting6015YesSession: Session                =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6015))
  val sessionAboutFranchiseOrLetting6015SIncompleteCatering: Session       =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015SIncompleteCateringDetail: Session =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteCateringDetailAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015SIncompleteLetting: Session        =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015NoSession: Session                 =
    aboutYouAndTheProperty6015NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo6015))

  val prefilledAboutFranchiseOrLettings6030: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionYes),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionYes)),
    Some(AnswerYes),
    0,
    None,
    IndexedSeq(prefilledLettingSectionYes),
    cateringOrFranchiseFee = Some(AnswerYes)
  )

  val sessionAboutFranchiseOrLetting6030YesSession: Session =
    aboutYouAndTheProperty6030YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6030))

  // Additional information sessions
  val prefilledAdditionalInformation: AdditionalInformation = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details")),
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
  val prefilledPropertyUseLeasebackArrangement  =
    PropertyUseLeasebackArrangement(AnswerYes)

  val prefilledAboutTheLandlord =
    AboutTheLandlord(
      prefilledFakeName,
      Some(prefilledLandlordAddress)
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
    ),
    turnoverSections6020 = Some(Seq(TurnoverSection6020(LocalDate.now))),
    turnoverSections6030 = Seq(TurnoverSection6030(LocalDate.now, 52, None, None))
  )

  val prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails    = Some(
    prefilledAboutTheTradingHistory.copy(bunkerFuelCardsDetails =
      Some(
        IndexedSeq(BunkerFuelCardsDetails(bunkerFuelCardDetails = BunkerFuelCardDetails("Card 1", 2)))
      )
    )
  )
  val prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails = Some(
    prefilledAboutTheTradingHistory.copy(lowMarginFuelCardsDetails =
      Some(
        IndexedSeq(LowMarginFuelCardsDetails(lowMarginFuelCardDetail = LowMarginFuelCardDetail("Low Margin Card", 2)))
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

  val prefilledAboutLeaseOrAgreement6010Route: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)),
    whatIsYourCurrentRentBasedOnDetails =
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnIndexedToRPI, Some("Test")))
  )

  val prefilledAboutLeaseOrAgreement6030Route: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)),
    whatIsYourCurrentRentBasedOnDetails =
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnIndexedToRPI, Some("Test")))
  )

  val prefilledAboutLeaseOrAgreementPartOne: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    currentRentFirstPaid = Some(CurrentRentFirstPaid(prefilledDateInput)),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartOneNoStartDate: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    None,
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    None,
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    currentRentFirstPaid = Some(CurrentRentFirstPaid(prefilledDateInput)),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartOneNo: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerNo),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetailsNo),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerNo)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerNo)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerNo))
  )

  val prefilledAboutLeaseOrAgreementPartTwo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersInformationDetails =
      Some(RentPayableVaryOnQuantityOfBeersInformationDetails("Test Content")),
    howIsCurrentRentFixed = Some(HowIsCurrentRentFixed(CurrentRentFixedInterimRent, prefilledDateInput)),
    intervalsOfRentReview = Some(IntervalsOfRentReview(Some("test"), Some(prefilledDateInput))),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    payACapitalSumInformationDetails = Some(PayACapitalSumInformationDetails(Some(123.12), Some(prefilledDateInput))),
    tenancyLeaseAgreementExpire = Some(TenancyLeaseAgreementExpire(prefilledDateInput))
  )

  val prefilledAboutLeaseOrAgreementPartTwoNoDate: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    howIsCurrentRentFixed = Some(HowIsCurrentRentFixed(CurrentRentFixedInterimRent, prefilledDateInput)),
    intervalsOfRentReview = Some(IntervalsOfRentReview(Some("test"), Some(prefilledDateInput))),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    payACapitalSumInformationDetails = Some(PayACapitalSumInformationDetails(Some(123.12), None)),
    tenancyLeaseAgreementExpire = Some(TenancyLeaseAgreementExpire(prefilledDateInput))
  )

  val prefilledAboutLeaseOrAgreementPartTwo6030: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    payACapitalSumInformationDetails = Some(PayACapitalSumInformationDetails(Some(123.12), Some(prefilledDateInput)))
  )

  val prefilledAboutLeaseOrAgreementPayPartTwo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartTwoNo: AboutLeaseOrAgreementPartTwo   = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerNo)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerNo)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerNo)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerNo))
  )
  val prefilledAboutLeaseOrAgreementPartThree: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServicesIndex = 1,
    servicesPaidIndex = 1,
    tradeServices = IndexedSeq(TradeServices(TradeServicesDetails("service-1"))),
    servicesPaid = IndexedSeq(ServicesPaid(ServicePaidSeparately("service-paid-1"))),
    carParking = Some(CarParking(Some(AnswerYes), Some(CarParkingSpaces(1, 2, 3)), Some(AnswerNo))),
    rentedEquipmentDetails = Some("Rented equipment details"),
    paymentForTradeServices = Some(PaymentForTradeServices(AnswerYes)),
    typeOfTenure = Some(TypeOfTenure(List("license"), Some("Type of tenure details"))),
    propertyUpdates = Some(PropertyUpdates(AnswerYes)),
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerYes))
  )

  val prefilledRequestReferenceNumber: RequestReferenceNumberDetails = RequestReferenceNumberDetails(
    Some(RequestReferenceNumber(prefilledFakeName, prefilledNoReferenceContactAddress)),
    Some(RequestReferenceNumberContactDetails(prefilledFakeName, prefilledContactDetails, Some("test"))),
    Some(CheckYourAnswersRequestReferenceNumber("CYA"))
  )

  val prefilledDownloadPDFRef: DownloadPDFDetails = DownloadPDFDetails(
    Some(DownloadPDFReferenceNumber(referenceNumber)),
    Some(DownloadPDF(forType6010))
  )
}
