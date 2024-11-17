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

import models.ForType.*
import models.submissions.Form6010.*
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutthetradinghistory.*
import models.submissions.aboutthetradinghistory.Caravans.CaravansPitchFeeServices.*
import models.submissions.aboutyouandtheproperty.*
import models.submissions.additionalinformation.*
import models.submissions.common.*
import models.submissions.connectiontoproperty.*
import models.submissions.downloadFORTypeForm.{DownloadPDF, DownloadPDFDetails, DownloadPDFReferenceNumber}
import models.submissions.notconnected.*
import models.submissions.requestReferenceNumber.*
import models.submissions.{ConnectedSubmission, NotConnectedSubmission, aboutfranchisesorlettings}
import models.{AnnualRent, ForType, Session, SubmissionDraft}

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String = "99996010004"

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
  val today: LocalDate                            = LocalDate.now
  val prefilledMonthYearInput: MonthsYearDuration = MonthsYearDuration(6, 2000)

  val hundred: BigDecimal = BigDecimal(100)

  val prefilledTradingNameOperatingFromProperty: TradingNameOperatingFromProperty = TradingNameOperatingFromProperty(
    "TRADING NAME"
  )

  val baseFilled6010Session: Session      = Session(referenceNumber, FOR6010, prefilledAddress, token, isWelsh = false)
  val baseFilled6011Session: Session      = Session(referenceNumber, FOR6011, prefilledAddress, token, isWelsh = false)
  val baseFilled6015Session: Session      = Session(referenceNumber, FOR6015, prefilledAddress, token, isWelsh = false)
  val baseFilled6016Session: Session      = Session(referenceNumber, FOR6016, prefilledAddress, token, isWelsh = false)
  val baseFilled6030Session: Session      = Session(referenceNumber, FOR6030, prefilledAddress, token, isWelsh = false)
  val baseFilled6020Session: Session      = Session(referenceNumber, FOR6020, prefilledAddress, token, isWelsh = false)
  val baseFilled6076Session: Session      = Session(referenceNumber, FOR6076, prefilledAddress, token, isWelsh = false)
  val baseFilled6045Session: Session      = Session(referenceNumber, FOR6045, prefilledAddress, token, isWelsh = false)
  val baseFilled6046Session: Session      = Session(referenceNumber, FOR6046, prefilledAddress, token, isWelsh = false)
  val baseFilled6048Session: Session      = Session(referenceNumber, FOR6048, prefilledAddress, token, isWelsh = false)
  val baseFilled6048WelshSession: Session = Session(referenceNumber, FOR6048, prefilledAddress, token, isWelsh = true)

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
  val prefilledStillConnectedDetailsYes: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee)
  )

  val prefilledStillConnectedVacantYes: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    vacantProperties = Some(VacantProperties(VacantPropertiesDetailsYes))
  )

  val prefilledStillConnectedVacantNo: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    vacantProperties = Some(VacantProperties(VacantPropertiesDetailsNo))
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
    Some(StartDateOfVacantProperty(LocalDate.now())),
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
    Some(StartDateOfVacantProperty(LocalDate.now())),
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
  val stillConnectedDetails6076YesSession: Session            =
    baseFilled6076Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6076NoSession: Session             =
    baseFilled6076Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6045YesSession: Session            =
    baseFilled6045Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6045NoSession: Session             =
    baseFilled6045Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))

  val stillConnectedDetails6048YesSession: Session =
    baseFilled6048Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6048NoSession: Session  =
    baseFilled6048Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))

  // Not connected sessions
  val prefilledNotConnectedYes: RemoveConnectionDetails  = RemoveConnectionDetails(
    Some(
      RemoveConnectionsDetails(
        prefilledFakeName,
        ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail),
        Some("Additional Info")
      )
    ),
    Some(PastConnectionTypeYes)
  )
  val prefilledNotConnectedNone: RemoveConnectionDetails = RemoveConnectionDetails(
    Some(RemoveConnectionsDetails(prefilledFakeName, ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail), None)),
    None
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
    tradingActivity = Some(TradingActivity(AnswerYes, Some("Trading activity details"))),
    renewablesPlant = Some(RenewablesPlant(Intermittent)),
    threeYearsConstructed = Some(AnswerYes),
    costsBreakdown = Some("breakdown")
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

  val prefilledAboutYouAndThePropertyPartTwo: AboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(
    plantAndTechnology = Some("plant and technology"),
    generatorCapacity = Some("generator capacity"),
    batteriesCapacity = Some("batteries capacity"),
    PropertyCurrentlyUsed(List("fleetCaravanPark", "chaletPark", "other"), Some("another use details"))
  )

  val prefilledAboutYouAndThePropertyPartTwo6045: AboutYouAndThePropertyPartTwo =
    prefilledAboutYouAndThePropertyPartTwo.copy(propertyCurrentlyUsed = prefilledPropertyCurrentlyInUsed)

  val prefilledAboutYouAndThePropertyPartTwo6048: AboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(
    commercialLetDate = Option(MonthsYearDuration(9, 2022)),
    commercialLetAvailability = Option(5),
    commercialLetAvailabilityWelsh = Option(
      Seq(
        LettingAvailability(LocalDate.of(2024, 3, 31), 10),
        LettingAvailability(LocalDate.of(2023, 3, 31), 20),
        LettingAvailability(LocalDate.of(2022, 3, 31), 15)
      )
    ),
    financialEndYearDates = Option(
      Seq(
        LocalDate.of(2024, 3, 31),
        LocalDate.of(2023, 3, 31),
        LocalDate.of(2022, 3, 31)
      )
    )
  )

  val prefilledPropertyCurrentlyInUsed =
    PropertyCurrentlyUsed(List("fleetCaravanPark", "chaletPark", "other"), Some("another use details"))

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
  val aboutYouAndTheProperty6076Session: Session    =
    stillConnectedDetails6076YesSession.copy(aboutYouAndThePropertyPartTwo =
      Some(prefilledAboutYouAndThePropertyPartTwo)
    )
  val aboutYouAndTheProperty6045YesSession: Session =
    stillConnectedDetails6045YesSession.copy(aboutYouAndThePropertyPartTwo =
      Some(prefilledAboutYouAndThePropertyPartTwo6045)
    )

  val aboutYouAndTheProperty6048YesSession: Session =
    stillConnectedDetails6048YesSession

  val aboutYouAndTheProperty6045NoSession: Session             =
    stillConnectedDetails6045NoSession.copy(aboutYouAndThePropertyPartTwo =
      Some(prefilledAboutYouAndThePropertyPartTwo6045)
    )
  // Trading history
  val prefilledAboutYourTradingHistory: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9)),
    Seq(
      TurnoverSection(
        today,
        123,
        234,
        345,
        456,
        567,
        678
      ),
      TurnoverSection(
        today.minusYears(1),
        123,
        234,
        345,
        456,
        567,
        678
      ),
      TurnoverSection(
        today.minusYears(2),
        123,
        234,
        345,
        456,
        567,
        678
      )
    ),
    costOfSales = Seq(
      CostOfSales(today, 1, 1, 1, 1),
      CostOfSales(today.minusYears(1), 1, 1, 1, 1),
      CostOfSales(today.minusYears(2), 1, 1, 1, 1)
    ),
    totalPayrollCostSections = Seq(
      TotalPayrollCost(today, 1, 2),
      TotalPayrollCost(today.minusYears(1), 1, 2),
      TotalPayrollCost(today.minusYears(2), 1, 2)
    ),
    fixedOperatingExpensesSections = Seq(
      FixedOperatingExpenses(today, 1, 1, 1, 1, 1),
      FixedOperatingExpenses(today.minusYears(1), 1, 1, 1, 1, 1),
      FixedOperatingExpenses(today.minusYears(2), 1, 1, 1, 1, 1)
    ),
    variableOperatingExpenses = VariableOperatingExpensesSections(
      Seq(
        VariableOperatingExpenses(today, 1, 1, 1, 1, 1, 1, 1, 1),
        VariableOperatingExpenses(today.minusYears(1), 1, 1, 1, 1, 1, 1, 1, 1),
        VariableOperatingExpenses(today.minusYears(2), 1, 1, 1, 1, 1, 1, 1, 1)
      ),
      "Other expenses details for all years"
    ),
    otherCosts = OtherCosts(
      otherCosts = Seq(
        OtherCost(today, 1, 1),
        OtherCost(today.minusYears(1), 1, 1),
        OtherCost(today.minusYears(2), 1, 1)
      ),
      "Other Costs Details"
    ),
    incomeExpenditureSummary = IncomeExpenditureSummary("confirmed"),
    incomeExpenditureSummaryData = Seq(IncomeExpenditureSummaryData(today.toString, 1, 2, 3, 4, 5, 6, 7, 8, 9)),
    unusualCircumstances = UnusualCircumstances("Unusual circumstances comment")
  )

  val prefilledAboutYourTradingHistory6020: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9)),
    turnoverSections6020 = Seq(
      TurnoverSection6020(today, 100, 100),
      TurnoverSection6020(today.minusYears(1), 200, 200),
      TurnoverSection6020(today.minusYears(2), 300, 300)
    ),
    electricVehicleChargingPoints = ElectricVehicleChargingPoints(AnswerYes, 123),
    totalFuelSold = Seq(TotalFuelSold(today, None)),
    bunkeredFuelQuestion = BunkeredFuelQuestion(AnswerYes),
    bunkeredFuelSold = Seq(BunkeredFuelSold(today, 1), BunkeredFuelSold(today, 2), BunkeredFuelSold(today, 3)),
    bunkerFuelCardsDetails = IndexedSeq(BunkerFuelCardsDetails(BunkerFuelCardDetails("Card 1", 2))),
    customerCreditAccounts =
      Seq(CustomerCreditAccounts(today, 5), CustomerCreditAccounts(today, 15), CustomerCreditAccounts(today, 25)),
    doYouAcceptLowMarginFuelCard = AnswerYes,
    percentageFromFuelCards =
      Seq(PercentageFromFuelCards(today, 5), PercentageFromFuelCards(today, 15), PercentageFromFuelCards(today, 25)),
    lowMarginFuelCardsDetails = IndexedSeq(LowMarginFuelCardsDetails(LowMarginFuelCardDetail("Low Margin Card", 2)))
  )

  val prefilledAboutYourTradingHistoryNoBunkered: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9)),
    turnoverSections6020 = Seq(
      TurnoverSection6020(today, 100, 100),
      TurnoverSection6020(today.minusYears(1), 200, 200),
      TurnoverSection6020(today.minusYears(2), 300, 300)
    ),
    electricVehicleChargingPoints = ElectricVehicleChargingPoints(AnswerYes, 123),
    totalFuelSold = Seq(TotalFuelSold(today, None)),
    bunkeredFuelQuestion = BunkeredFuelQuestion(AnswerNo),
    bunkerFuelCardsDetails = IndexedSeq(BunkerFuelCardsDetails(BunkerFuelCardDetails("Card 1", 2))),
    customerCreditAccounts =
      Seq(CustomerCreditAccounts(today, 5), CustomerCreditAccounts(today, 15), CustomerCreditAccounts(today, 25)),
    doYouAcceptLowMarginFuelCard = AnswerYes,
    percentageFromFuelCards =
      Seq(PercentageFromFuelCards(today, 5), PercentageFromFuelCards(today, 15), PercentageFromFuelCards(today, 25)),
    lowMarginFuelCardsDetails = IndexedSeq(LowMarginFuelCardsDetails(LowMarginFuelCardDetail("Low Margin Card", 2)))
  )

  val prefilledAboutYourTradingHistory6030: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9)),
    Seq.empty,
    None,
    Seq(
      TurnoverSection6030(today, 52, 100, 100),
      TurnoverSection6030(today.minusYears(1), 52, 200, 200),
      TurnoverSection6030(today.minusYears(2), 52, 300, 300)
    ),
    Seq.empty,
    Seq.empty,
    None,
    Seq.empty,
    None,
    None,
    Seq.empty,
    UnusualCircumstances("unusual circumstances")
  )

  val prefilledAboutYourTradingHistory6045: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(1, 2021), DayMonthsDuration(22, 4))
  )

  val prefilledAboutYourTradingHistory6048: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(1, 2018), DayMonthsDuration(22, 4))
  )

  val prefilledAboutYourTradingHistory6076: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9))
  )

  val prefilledAboutYourTradingHistory60761: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    whatYouWillNeed = "test"
  )

  val staffCostsTestData = StaffCosts(
    wagesAndSalaries = Some(BigDecimal(50000.00)),
    nationalInsurance = Some(BigDecimal(5000.00)),
    pensionContributions = Some(BigDecimal(3000.00)),
    remunerations = Some(BigDecimal(7000.00))
  )

  val grossReceiptsForBaseLoad = GrossReceiptsForBaseLoad(
    renewableHeatIncentiveBioMethane = Some(BigDecimal(50000.00)),
    renewableHeatIncentiveBioMass = Some(BigDecimal(40000.00)),
    byProductSales = Some(BigDecimal(30000.00)),
    hotWaterHeatOrSteamSales = Some(BigDecimal(20000.00)),
    gateIncomeFromWaste = Some(BigDecimal(10000.00))
  )

  val premisesCosts = PremisesCosts(
    energyAndUtilities = Some(BigDecimal(10000.00)),
    buildingRepairAndMaintenance = Some(BigDecimal(20000.00)),
    repairsAndRenewalsOfFixtures = Some(BigDecimal(30000.00)),
    rent = Some(BigDecimal(40000.00)),
    businessRates = Some(BigDecimal(50000.00)),
    buildingInsurance = Some(BigDecimal(60000.00))
  )

  val prefilledTurnoverSections6045: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = true,
    turnoverSections6045 = Seq(
      TurnoverSection6045(
        today,
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(),
        singleCaravansOwnedByOperator = CaravansTrading6045(52, 3000, 30),
        singleCaravansSublet = CaravansTrading6045(52, 1000, 10),
        twinUnitCaravansOwnedByOperator = CaravansTrading6045(26, 2000, 20),
        twinUnitCaravansSublet = CaravansTrading6045(),
        pitchesForCaravans = Some(
          TentingPitchesTradingData(
            tradingPeriod = 52,
            grossReceipts = Some(BigDecimal(5000)),
            numberOfPitches = Some(20)
          )
        ),
        pitchesForGlamping = Some(
          TentingPitchesTradingData(
            tradingPeriod = 52,
            grossReceipts = Some(BigDecimal(4000)),
            numberOfPitches = Some(15)
          )
        ),
        rallyAreas = Some(
          RallyAreasTradingData(
            tradingPeriod = 52,
            grossReceipts = Some(BigDecimal(2000)),
            areaInHectares = Some(BigDecimal(1.5))
          )
        ),
        additionalShops = Some(
          AdditionalShops(
            tradingPeriod = 52,
            grossReceipts = Some(BigDecimal(3000)),
            costOfPurchase = Some(BigDecimal(1500))
          )
        ),
        additionalCatering = Some(
          AdditionalCatering(
            tradingPeriod = 52,
            grossReceipts = Some(BigDecimal(2500)),
            costOfPurchase = Some(BigDecimal(1200))
          )
        ),
        additionalBarsClubs = Some(
          AdditionalBarsClubs(
            tradingPeriod = 52,
            grossReceiptsBars = Some(BigDecimal(3500)),
            barPurchases = Some(BigDecimal(1800)),
            grossClubMembership = Some(BigDecimal(2000)),
            grossClubSeparate = Some(BigDecimal(1000)),
            costOfEntertainment = Some(BigDecimal(500))
          )
        ),
        additionalAmusements = Some(
          AdditionalAmusements(
            tradingPeriod = 52,
            receipts = Some(BigDecimal(2500))
          )
        ),
        additionalMisc = Some(
          AdditionalMisc(52, Some(100.00), Some(100.00), Some(10), Some(100.00), Some(100.00))
        )
      ),
      TurnoverSection6045(
        today.minusYears(1),
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(51, 2000)
      ),
      TurnoverSection6045(
        today.minusYears(2),
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(50, 3000)
      )
    ),
    caravans = Caravans( // 6045/46
      anyStaticLeisureCaravansOnSite = AnswerYes,
      openAllYear = AnswerNo,
      weeksPerYear = 26,
      singleCaravansAge = CaravansAge(
        fleetHire = CaravansPerAgeCategory(10, 20, 30, 40),
        privateSublet = CaravansPerAgeCategory(5, 6, 7, 8)
      ),
      twinUnitCaravansAge = CaravansAge(
        fleetHire = CaravansPerAgeCategory(100, 200, 300, 400),
        privateSublet = CaravansPerAgeCategory(1, 2, 3, 4)
      ),
      totalSiteCapacity = CaravansTotalSiteCapacity(1, 2, 3, 4, 5, 6),
      caravansPerService = CaravansPerService(10, 20, 30, 40),
      annualPitchFee = CaravansAnnualPitchFee(
        9000,
        Seq(WaterAndDrainage, Electricity, Other),
        waterAndDrainage = Some(1000),
        electricity = Some(3000),
        otherPitchFeeDetails = Some("food - 1000, cleaning - 500")
      )
    ),
    touringAndTentingPitches = Some(
      TouringAndTentingPitches(
        tentingPitchesOnSite = Some(AnswerYes),
        tentingPitchesAllYear = Some(
          TentingPitchesAllYear(
            tentingPitchesAllYear = AnswerNo,
            weekOfPitchesUse = Some(26)
          )
        ),
        tentingPitchesTotal = Some(50),
        tentingPitchesCertificated = Some(AnswerYes),
        checkYourAnswersTentingPitches = Some(AnswerNo)
      )
    ),
    additionalActivities = Some(
      AdditionalActivities(
        additionalActivitiesOnSite = Some(AnswerYes),
        additionalActivitiesAllYear = Some(
          AdditionalActivitiesAllYear(
            additionalActivitiesAllYear = AnswerNo,
            weeksOpen = Some(30)
          )
        )
      )
    ),
    additionalMiscDetails = Some(
      AdditionalMiscDetails(Some("details"), Some("details"))
    )
  )

  val prefilledTurnoverSections6048: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = true,
    isFinancialYearsCorrect = true,
    turnoverSections6048 = Seq(
      TurnoverSection6048(
        today,
        income = Income6048(1, 2, 3),
        fixedCosts = FixedCosts6048(1, 3, 3),
        accountingCosts = AccountingCosts6048(1, 1, 2, 2, 2),
        administrativeCosts = AdministrativeCosts6048(1, 1, 1, 3, 3),
        operationalCosts = OperationalCosts6048(1, 1, 1, 1, 1, 1)
      ),
      TurnoverSection6048(
        today.minusYears(1),
        income = Income6048(10, 20, 30),
        fixedCosts = FixedCosts6048(10, 30, 30),
        accountingCosts = AccountingCosts6048(10, 10, 20, 20, 20),
        administrativeCosts = AdministrativeCosts6048(10, 10, 10, 30, 30),
        operationalCosts = OperationalCosts6048(10, 10, 10, 10, 10, 10)
      ),
      TurnoverSection6048(
        today.minusYears(2),
        income = Income6048(100, 200, 300),
        fixedCosts = FixedCosts6048(100, 300, 300),
        accountingCosts = AccountingCosts6048(100, 100, 200, 200, 200),
        administrativeCosts = AdministrativeCosts6048(100, 100, 100, 300, 300),
        operationalCosts = OperationalCosts6048(100, 100, 100, 100, 100, 100)
      )
    )
  )

  val aboutYourTradingHistory6045YesSession: Session =
    aboutYouAndTheProperty6045YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
      aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
    )

  val aboutYourTradingHistory6048YesSession: Session =
    aboutYouAndTheProperty6048YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6048),
      aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6048)
    )

  val aboutYourTradingHistory6045CYAOtherHolidayAccommodationSession: Session =
    aboutYourTradingHistory6045YesSession.copy(aboutTheTradingHistoryPartOne =
      prefilledAboutTheTradingHistoryPartOneCYA6045
    )

  val prefilledAboutTheTradingHistoryPartOneCYA6045: AboutTheTradingHistoryPartOne =
    prefilledTurnoverSections6045.copy(otherHolidayAccommodation =
      Some(OtherHolidayAccommodation(Some(AnswerNo), None))
    )

  val prefilledAboutTheTradingHistoryPartOneCYA6048: AboutTheTradingHistoryPartOne =
    prefilledTurnoverSections6048

  val prefilledAboutTheTradingHistoryPartOneCYA6045All                           = prefilledAboutTheTradingHistoryPartOneCYA6045.copy(
    otherHolidayAccommodation = Some(
      OtherHolidayAccommodation(
        Some(AnswerYes),
        OtherHolidayAccommodationDetails(AnswerYes),
        TotalSiteCapacity(20, 5, 15),
        None
      )
    )
  )
  val aboutYourTradingHistory6045CYAOtherHolidayAccommodationSessionYes: Session =
    aboutYourTradingHistory6045YesSession.copy(aboutTheTradingHistoryPartOne =
      prefilledAboutTheTradingHistoryPartOneCYA6045All
    )

  val prefilledAboutTheTradingHistoryPartOneTentYes: AboutTheTradingHistoryPartOne =
    prefilledTurnoverSections6045.copy(
      otherHolidayAccommodation = Some(OtherHolidayAccommodation(Some(AnswerNo), None)),
      touringAndTentingPitches = Some(TouringAndTentingPitches(Some(AnswerYes)))
    )
  val prefilledAboutTheTradingHistoryPartOneTentNo: AboutTheTradingHistoryPartOne  =
    prefilledTurnoverSections6045.copy(
      otherHolidayAccommodation = Some(OtherHolidayAccommodation(Some(AnswerNo), None)),
      touringAndTentingPitches = Some(TouringAndTentingPitches(Some(AnswerNo)))
    )
  val prefilledTurnoverSections6076: AboutTheTradingHistoryPartOne                 = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = true,
    turnoverSections6076 = Seq(
      TurnoverSection6076(
        today,
        52,
        "5000 kWh",
        1000,
        CostOfSales6076Sum(1, 2, 3, 4, 5),
        CostOfSales6076IntermittentSum(1, 2, 3, 4),
        OperationalExpenses(1, 2, 3, 4, 5, 6),
        headOfficeExpenses = 100,
        staffCosts = staffCostsTestData,
        grossReceiptsForBaseLoad = grossReceiptsForBaseLoad,
        premisesCosts = premisesCosts,
        grossReceiptsExcludingVAT = GrossReceiptsExcludingVAT(1, 10)
      ),
      TurnoverSection6076(
        today.minusYears(1),
        52,
        "5 MWh",
        2000,
        CostOfSales6076Sum(1, 2, 3, 4, 5),
        CostOfSales6076IntermittentSum(1, 2, 3, 4),
        OperationalExpenses(1, 2, 3, 4, 5, 6),
        headOfficeExpenses = 200,
        staffCosts = staffCostsTestData,
        grossReceiptsForBaseLoad = grossReceiptsForBaseLoad,
        premisesCosts = premisesCosts,
        grossReceiptsExcludingVAT = GrossReceiptsExcludingVAT(2, 20)
      ),
      TurnoverSection6076(
        today.minusYears(2),
        52,
        "5 MWh",
        3000,
        CostOfSales6076Sum(1, 2, 3, 4, 5),
        CostOfSales6076IntermittentSum(1, 2, 3, 4),
        OperationalExpenses(1, 2, 3, 4, 5, 6),
        headOfficeExpenses = 300,
        staffCosts = staffCostsTestData,
        grossReceiptsForBaseLoad = grossReceiptsForBaseLoad,
        premisesCosts = premisesCosts,
        grossReceiptsExcludingVAT = GrossReceiptsExcludingVAT(3, 30)
      )
    ),
    otherIncomeDetails = "OtherIncome details",
    otherOperationalExpensesDetails = "Other expenses",
    furtherInformationOrRemarks = "Further information or remarks"
  )

  val aboutYourTradingHistory6076YesSession: Session =
    aboutYouAndTheProperty6076Session.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6076),
      aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6076)
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

  val aboutYourTradingHistory6016YesSession: Session =
    aboutYouAndTheProperty6016YesSession.copy(
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
  val rentDetails                                              = Some(RentDetails(100, prefilledDateInput))
  val atmLetting                                               = ATMLetting(Some("HSBC"), Some(prefilledLettingAddress), rentDetails)
  val telcoLetting                                             = TelecomMastLetting(Some("Vodafone"), Some("roof"), Some(prefilledLettingAddress), rentDetails)
  val advert                                                   = AdvertisingRightLetting(Some("Billboard"), Some("JCDx"), Some(prefilledLettingAddress), rentDetails)
  val otherLetting                                             = OtherLetting(Some("Charging point"), Some("Tesla"), Some(prefilledLettingAddress), rentDetails)

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

  val prefilledAboutFranchiseOrLettingsNo6016: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerNo),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledCateringOperationSectionNo),
    Some(IndexedSeq(prefilledCateringOperationBusinessSectionNo)),
    Some(AnswerNo),
    0,
    None,
    IndexedSeq(prefilledLettingSectionNo),
    None,
    Some(false),
    Some(AnswerNo)
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

  val prefilledAboutFranchiseOrLettings6045: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    rentalIncome = Some(
      IndexedSeq(
        ConcessionIncomeRecord(
          businessDetails = Some(
            CateringOperationBusinessDetails(
              operatorName = "Operator",
              typeOfBusiness = "Bar",
              howBusinessPropertyIsUsed = "Leased"
            )
          ),
          feeReceived = Some(FeeReceived(Seq(FeeReceivedPerYear(LocalDate.now, 2023))))
        ),
        LettingIncomeRecord(
          operatorDetails = Some(
            LettingOtherPartOfPropertyInformationDetails(
              operatorName = "Letting Operator",
              typeOfBusiness = "Property Letting",
              lettingAddress = LettingAddress(
                buildingNameNumber = "123",
                street1 = Some("Main Street"),
                town = "Bristol",
                county = Some("Bristol"),
                postcode = "AN12 3YZ"
              )
            )
          ),
          rent = Some(
            LettingOtherPartOfPropertyRentDetails(
              annualRent = 15000.00,
              dateInput = LocalDate.of(2021, 1, 1)
            )
          )
        )
      )
    )
  )

  val lettingIncomeRecord                                                                      = LettingIncomeRecord(
    operatorDetails = Some(
      LettingOtherPartOfPropertyInformationDetails(
        operatorName = "Letting Operator",
        typeOfBusiness = "Property Letting",
        lettingAddress = LettingAddress(
          buildingNameNumber = "123",
          street1 = Some("Main Street"),
          town = "Bristol",
          county = Some("Bristol"),
          postcode = "AN12 3YZ"
        )
      )
    ),
    rent = Some(
      LettingOtherPartOfPropertyRentDetails(
        annualRent = 15000.00,
        dateInput = LocalDate.of(2021, 1, 1)
      )
    )
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

  val prefilledAboutFranchiseOrLettingsWith6020LettingsAll: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
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
    Some(AnswerYes),
    Some(IndexedSeq(atmLetting, telcoLetting, advert, otherLetting))
  )

  val prefilledAboutFranchiseOrLettingsWith6020MaxLettings: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
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
    Some(AnswerYes),
    Some(
      IndexedSeq(
        atmLetting,
        telcoLetting,
        advert,
        otherLetting,
        otherLetting,
        otherLetting,
        otherLetting,
        otherLetting,
        otherLetting,
        otherLetting
      )
    )
  )
  val sessionAboutFranchiseOrLetting6010YesSession: Session                           =
    aboutYouAndTheProperty6010YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings))
  val sessionAboutFranchiseOrLetting6010Incomplete: Session                           =
    aboutYouAndTheProperty6010YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledAboutFranchiseOrLettingsIncompleteLetting)
    )
  val sessionAboutFranchiseOrLetting6010NoSession: Session                            =
    aboutYouAndTheProperty6010NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo))

  val sessionAboutFranchiseOrLetting6045: Session =
    stillConnectedDetails6045NoSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6045))

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

  val sessionAboutFranchiseOrLetting6020Session: Session =
    aboutYouAndTheProperty6020YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledAboutFranchiseOrLettingsWith6020LettingsAll)
    )

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
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      saveAsDraftPassword = Some("pass")
    )
  val submissionDraft                         = SubmissionDraft(FOR6010, prefilledBaseSession, "/send-trade-and-cost-information/about-you")
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
    FOR6010,
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
    turnoverSections6020 = Some(Seq(TurnoverSection6020(today))),
    turnoverSections6030 = Seq(TurnoverSection6030(today, 52, None, None)),
    unusualCircumstances = Some(UnusualCircumstances("Unusual circumstances comment"))
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

  val prefilledAboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne()

  val prefilledOtherHolidayAccommodationYes: Option[OtherHolidayAccommodation] = Some(
    OtherHolidayAccommodation(Some(AnswerYes))
  )
  val prefilledOtherHolidayAccommodationNo: Option[OtherHolidayAccommodation]  = Some(
    OtherHolidayAccommodation(Some(AnswerNo))
  )

  val prefilledCYAOtherHolidayAccommodationNo: Option[OtherHolidayAccommodation] = Some(
    OtherHolidayAccommodation(Some(AnswerNo), None)
  )

  val prefilledAboutTheTradingHistoryPartOneYes =
    prefilledAboutTheTradingHistoryPartOne.copy(otherHolidayAccommodation = prefilledOtherHolidayAccommodationYes)
  val prefilledAboutTheTradingHistoryPartOneNo  =
    prefilledAboutTheTradingHistoryPartOne.copy(otherHolidayAccommodation = prefilledOtherHolidayAccommodationNo)

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
    Some(AnswerNo),
    checkYourAnswersConnectionToProperty = Some(CheckYourAnswersConnectionToProperty("No"))
  )

  val prefilledNotVacantPropertiesNoCYA = StillConnectedDetails(
    Some(AddressConnectionTypeNo),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(prefilledEditTheAddress),
    Some(prefilledVacantPropertiesDetails),
    Some(prefilledTradingNameOperatingFromProperty),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(AnswerNo)
  )

  val prefilledNotVacantPropertiesEditCYA = StillConnectedDetails(
    Some(AddressConnectionTypeYesChangeAddress),
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

  val prefilledAboutLeaseOrAgreement6045: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    doesTheRentPayable = Some(DoesTheRentPayable(List.empty, "Does rent payable details")),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreement6045TextArea: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes))
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
    currentLeaseOrAgreementBegin = Some(CurrentLeaseOrAgreementBegin(MonthsYearDuration(4, 2024), "Granted for")),
    includedInYourRentDetails = Some(IncludedInYourRentDetails(List("vat"), BigDecimal(100))),
    doesTheRentPayable = Some(DoesTheRentPayable(List.empty, "Does rent payable details")),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeTradeServicesInformation =
      Some(RentIncludeTradeServicesInformationDetails(BigDecimal(100), "Describe the services")),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentIncludeFixtureAndFittingsDetails = Some(RentIncludeFixturesOrFittingsInformationDetails(BigDecimal(100))),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)),
    whatIsYourCurrentRentBasedOnDetails =
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnPercentageOpenMarket, Some("Open market"))),
    rentIncreasedAnnuallyWithRPIDetails = Some(RentIncreasedAnnuallyWithRPIDetails(AnswerYes)),
    checkYourAnswersAboutYourLeaseOrTenure = Some(CheckYourAnswersAboutYourLeaseOrTenure("Yes")),
    rentIncludesVat = Some(RentIncludesVatDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartOneNoOpenMarket: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(prefilledAboutTheLandlord),
    Some(AnswerYes),
    Some(prefilledConnectedToLandlordDetails),
    Some(prefilledLeaseOrAgreementYearsDetails),
    Some(prefilledCurrentRentPayableWithin12Months),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(prefilledAnnualRent),
    currentRentFirstPaid = Some(CurrentRentFirstPaid(prefilledDateInput)),
    currentLeaseOrAgreementBegin = Some(CurrentLeaseOrAgreementBegin(MonthsYearDuration(4, 2024), "Granted for")),
    includedInYourRentDetails = Some(IncludedInYourRentDetails(List.empty)),
    doesTheRentPayable = Some(DoesTheRentPayable(List.empty, "Does rent payable details")),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerNo)),
    whatIsYourCurrentRentBasedOnDetails =
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnOther, Some("Other"))),
    checkYourAnswersAboutYourLeaseOrTenure = Some(CheckYourAnswersAboutYourLeaseOrTenure("Yes"))
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
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerNo)),
    rentIncreasedAnnuallyWithRPIDetails = Some(RentIncreasedAnnuallyWithRPIDetails(AnswerNo))
  )

  val prefilledAboutLeaseOrAgreementPartOneNoConnection6045: AboutLeaseOrAgreementPartOne =
    AboutLeaseOrAgreementPartOne(
      Some(prefilledAboutTheLandlord),
      Some(AnswerNo)
    )

  val prefilledAboutLeaseOrAgreementPartTwo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryAccordingToGrossOrNetInformationDetails =
      Some(RentPayableVaryAccordingToGrossOrNetInformationDetails("Test Content")),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersInformationDetails =
      Some(RentPayableVaryOnQuantityOfBeersInformationDetails("Test Content")),
    howIsCurrentRentFixed = Some(HowIsCurrentRentFixed(CurrentRentFixedInterimRent, prefilledDateInput)),
    methodToFixCurrentRentDetails = Some(MethodToFixCurrentRentDetails(MethodToFixCurrentRentsAgreement)),
    intervalsOfRentReview = Some(IntervalsOfRentReview(Some("test"), Some(prefilledDateInput))),
    canRentBeReducedOnReviewDetails = Some(CanRentBeReducedOnReviewDetails(AnswerYes)),
    incentivesPaymentsConditionsDetails = Some(IncentivesPaymentsConditionsDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    tenantsAdditionsDisregardedDetails = Some(TenantsAdditionsDisregardedDetails("Test Content")),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    legalOrPlanningRestrictionsDetails = Some(LegalOrPlanningRestrictionsDetails("Legal planning restrictions")),
    capitalSumDescription = Some(CapitalSumDescription("capital Sum Description")),
    payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerYes)),
    payACapitalSumInformationDetails = Some(PayACapitalSumInformationDetails(Some(123.12), Some(prefilledDateInput))),
    paymentWhenLeaseIsGrantedDetails = Some(PaymentWhenLeaseIsGrantedDetails(AnswerYes)),
    tenancyLeaseAgreementExpire = Some(TenancyLeaseAgreementExpire(prefilledDateInput)),
    ultimatelyResponsibleInsideRepairs = Some(UltimatelyResponsibleInsideRepairs(InsideRepairsLandlord, None)),
    ultimatelyResponsibleOutsideRepairs = Some(UltimatelyResponsibleOutsideRepairs(OutsideRepairsTenant, None)),
    ultimatelyResponsibleBuildingInsurance =
      Some(UltimatelyResponsibleBuildingInsurance(BuildingInsuranceBoth, Some("Both")))
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

  val prefilledAboutLeaseOrAgreementPartTwoNo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerNo)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerNo)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerNo)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerNo)),
    payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerNo))
  )

  val prefilledAboutLeaseOrAgreementPartThree: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServicesIndex = 1,
    servicesPaidIndex = 1,
    tradeServices = IndexedSeq(TradeServices(TradeServicesDetails("service-1"), Some(AnswerYes))),
    servicesPaid = IndexedSeq(
      ServicesPaid(ServicePaidSeparately("service-paid-1"), ServicePaidSeparatelyCharge(BigDecimal(1000)), AnswerYes)
    ),
    throughputAffectsRent = ThroughputAffectsRent(AnswerYes, "Throughput affects rent details"),
    isVATPayableForWholeProperty = AnswerYes,
    isRentUnderReview = AnswerNo,
    carParking = CarParking(AnswerYes, CarParkingSpaces(1, 2, 3), AnswerYes, CarParkingSpaces(10), hundred, today),
    rentedEquipmentDetails = "Rented equipment details",
    paymentForTradeServices = Some(PaymentForTradeServices(AnswerYes)),
    typeOfTenure = Some(TypeOfTenure(List("license"), Some("Type of tenure details"))),
    propertyUpdates = Some(PropertyUpdates(AnswerYes)),
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerYes)),
    benefitsGiven = Some(BenefitsGiven(AnswerYes)),
    benefitsGivenDetails = Some(BenefitsGivenDetails("benefits Given Details")),
    workCarriedOutDetails = Some(WorkCarriedOutDetails("workCarriedOutDetails")),
    workCarriedOutCondition = Some(WorkCarriedOutCondition(AnswerYes)),
    provideDetailsOfYourLease = Some("These are details of a lease"),
    rentIncludeTradeServicesDetailsTextArea = Some("trade services text area"),
    rentIncludeFixtureAndFittingsDetailsTextArea = Some("fixture and fittings text area"),
    rentDevelopedLand = Some(AnswerYes),
    rentDevelopedLandDetails = Some("rent developed land details")
  )

  val prefilledAboutLeaseOrAgreementPartFour: AboutLeaseOrAgreementPartFour = AboutLeaseOrAgreementPartFour(
    rentIncludeStructuresBuildings = Some(AnswerYes),
    rentIncludeStructuresBuildingsDetails = Some("rentIncludeStructuresBuildingsDetails"),
    surrenderedLeaseAgreementDetails = Some(SurrenderedLeaseAgreementDetails(100.00, "surrenderedLeaseAgreement")),
    isGivenRentFreePeriod = Some(AnswerYes),
    rentFreePeriodDetails = Some("Rent free period details")
  )

  val prefilledAboutLeaseOrAgreementPartThree6045: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServicesIndex = 1,
    servicesPaidIndex = 1,
    tradeServices = IndexedSeq(TradeServices(TradeServicesDetails("service-1"), Some(AnswerYes))),
    servicesPaid = IndexedSeq(
      ServicesPaid(ServicePaidSeparately("service-paid-1"), ServicePaidSeparatelyCharge(BigDecimal(1000)), AnswerYes)
    ),
    throughputAffectsRent = ThroughputAffectsRent(AnswerYes, "Throughput affects rent details"),
    isVATPayableForWholeProperty = AnswerYes,
    isRentUnderReview = AnswerNo,
    carParking = CarParking(AnswerYes, CarParkingSpaces(1, 2, 3), AnswerYes, CarParkingSpaces(10), hundred, today),
    rentedEquipmentDetails = "Rented equipment details",
    paymentForTradeServices = Some(PaymentForTradeServices(AnswerYes)),
    typeOfTenure = Some(TypeOfTenure(List("license"), Some("Type of tenure details"))),
    propertyUpdates = Some(PropertyUpdates(AnswerYes)),
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerYes)),
    benefitsGiven = Some(BenefitsGiven(AnswerYes)),
    benefitsGivenDetails = Some(BenefitsGivenDetails("benefits Given Details")),
    workCarriedOutDetails = Some(WorkCarriedOutDetails("workCarriedOutDetails")),
    workCarriedOutCondition = Some(WorkCarriedOutCondition(AnswerYes)),
    provideDetailsOfYourLease = Some("These are details of a lease"),
    rentIncludeTradeServicesDetailsTextArea = Some("test"),
    rentDevelopedLand = AnswerYes
  )

  val prefilledAboutLeaseOrAgreementPartThree6045TextArea: AboutLeaseOrAgreementPartThree =
    AboutLeaseOrAgreementPartThree(
      rentIncludeTradeServicesDetailsTextArea = Some("test")
    )

  val prefilledAboutLeaseOrAgreementPartThreeNo: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServicesIndex = 1,
    servicesPaidIndex = 1,
    tradeServices = IndexedSeq(TradeServices(TradeServicesDetails("service-1"), Some(AnswerYes))),
    servicesPaid = IndexedSeq(ServicesPaid(ServicePaidSeparately("service-paid-1"))),
    throughputAffectsRent = ThroughputAffectsRent(AnswerNo, "Throughput affects rent details"),
    isVATPayableForWholeProperty = AnswerNo,
    isRentUnderReview = AnswerNo,
    carParking = CarParking(AnswerNo, CarParkingSpaces(1, 2, 3), AnswerNo, CarParkingSpaces(10), hundred, today),
    rentedEquipmentDetails = "Rented equipment details",
    paymentForTradeServices = Some(PaymentForTradeServices(AnswerNo)),
    typeOfTenure = Some(TypeOfTenure(List("license"), Some("Type of tenure details"))),
    propertyUpdates = Some(PropertyUpdates(AnswerNo)),
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerNo)),
    benefitsGiven = Some(BenefitsGiven(AnswerNo)),
    benefitsGivenDetails = Some(BenefitsGivenDetails("benefits Given Details")),
    workCarriedOutDetails = Some(WorkCarriedOutDetails("workCarriedOutDetails")),
    workCarriedOutCondition = Some(WorkCarriedOutCondition(AnswerNo)),
    provideDetailsOfYourLease = Some("These are details of a lease")
  )

  val prefilledRequestReferenceNumber: RequestReferenceNumberDetails = RequestReferenceNumberDetails(
    Some(RequestReferenceNumber(prefilledFakeName, prefilledNoReferenceContactAddress)),
    Some(RequestReferenceNumberContactDetails(prefilledFakeName, prefilledContactDetails, Some("test"))),
    Some(CheckYourAnswersRequestReferenceNumber("CYA"))
  )

  val prefilledDownloadPDFRef: DownloadPDFDetails = DownloadPDFDetails(
    Some(DownloadPDFReferenceNumber(referenceNumber)),
    Some(DownloadPDF(FOR6010.toString))
  )

  val prefilledFull6020Session = sessionAboutFranchiseOrLetting6020Session.copy(
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOne,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPayPartTwo,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
  )

  val prefilledFull6030Session = sessionAboutFranchiseOrLetting6030YesSession.copy(
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOne,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo6030
  )
}
