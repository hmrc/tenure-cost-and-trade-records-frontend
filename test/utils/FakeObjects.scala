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
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings._
import models.submissions.aboutthetradinghistory._
import models.submissions.aboutyouandtheproperty._
import models.submissions.additionalinformation._
import models.submissions.common._
import models.submissions.connectiontoproperty._
import models.submissions.downloadFORTypeForm.{DownloadPDF, DownloadPDFDetails, DownloadPDFReferenceNumber}
import models.submissions.notconnected.{PastConnectionTypeYes, RemoveConnectionDetails, RemoveConnectionsDetails}
import models.submissions.requestReferenceNumber._
import models.submissions.{ConnectedSubmission, NotConnectedSubmission, aboutfranchisesorlettings}
import models.{AnnualRent, ForTypes, Session, SubmissionDraft}

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String   = "99996010004"
  val forType6010: String       = "FOR6010"
  val forType6011: String       = "FOR6011"
  val forType6015: String       = "FOR6015"
  val forType6016: String       = "FOR6016"
  val forType6030: String       = "FOR6030"
  val forType6020: String       = "FOR6020"
  val forType6076: String       = "FOR6076"
  val forType6045: String       = "FOR6045"
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

  val baseFilled6010Session: Session = Session(referenceNumber, forType6010, prefilledAddress, token)
  val baseFilled6011Session: Session = Session(referenceNumber, forType6011, prefilledAddress, token)
  val baseFilled6015Session: Session = Session(referenceNumber, forType6015, prefilledAddress, token)
  val baseFilled6016Session: Session = Session(referenceNumber, forType6016, prefilledAddress, token)
  val baseFilled6030Session: Session = Session(referenceNumber, forType6030, prefilledAddress, token)
  val baseFilled6020Session: Session = Session(referenceNumber, forType6020, prefilledAddress, token)
  val baseFilled6076Session: Session = Session(referenceNumber, forType6076, prefilledAddress, token)
  val baseFilled6045Session: Session = Session(referenceNumber, forType6045, prefilledAddress, token)
  // Request reference number
  val prefilledRequestRefNumCYA      = RequestReferenceNumberDetails(
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
  val prefilledRequestRefNumBlank    = RequestReferenceNumberDetails()

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
  val stillConnectedDetails6076YesSession: Session            =
    baseFilled6076Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6076NoSession: Session             =
    baseFilled6076Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))
  val stillConnectedDetails6045YesSession: Session            =
    baseFilled6045Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
  val stillConnectedDetails6045NoSession: Session             =
    baseFilled6045Session.copy(stillConnectedDetails = Some(prefilledStillConnectedDetailsNo))

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
  val prefilledPropertyCurrentlyInUsed                                          =
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

  val prefilledAboutYourTradingHistory6076: AboutTheTradingHistory = AboutTheTradingHistory(
    OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9))
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
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire()
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
    caravans = Caravans(AnswerYes, AnswerNo, 26)
  )

  val aboutYourTradingHistory6045YesSession: Session =
    aboutYouAndTheProperty6045YesSession.copy(
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
      aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
    )

  val aboutYourTradingHistory6045CYAOtherHolidayAccommodationSession: Session      =
    aboutYourTradingHistory6045YesSession.copy(aboutTheTradingHistoryPartOne =
      prefilledAboutTheTradingHistoryPartOneCYA6045
    )
  val prefilledAboutTheTradingHistoryPartOneCYA6045: AboutTheTradingHistoryPartOne =
    prefilledTurnoverSections6045.copy(otherHolidayAccommodation =
      Some(OtherHolidayAccommodation(Some(AnswerNo), None))
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
  val sessionAboutFranchiseOrLetting6015YesSession: Session                           =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6015))
  val sessionAboutFranchiseOrLetting6015SIncompleteCatering: Session                  =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015SIncompleteCateringDetail: Session            =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteCateringDetailAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015SIncompleteLetting: Session                   =
    aboutYouAndTheProperty6015YesSession.copy(aboutFranchisesOrLettings =
      Some(prefilledIncompleteAboutFranchiseOrLettings6015)
    )
  val sessionAboutFranchiseOrLetting6015NoSession: Session                            =
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
    Some(AnswerNo)
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
      Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnPercentageOpenMarket, Some("Open market"))),
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
    provideDetailsOfYourLease = Some("These are details of a lease")
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
    Some(DownloadPDF(forType6010))
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
