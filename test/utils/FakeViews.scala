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

package utils

import play.api.test.Injecting
import views.html.aboutYourLeaseOrTenure._
import views.html.aboutconcessionsorlettings.rentFromConcessions
import views.html.aboutfranchisesorlettings._
import views.html.aboutthetradinghistory.{whatYouWillNeed, _}
import views.html.aboutyouandtheproperty._
import views.html.accommodation._
import views.html.additionalinformation.{checkYourAnswersAdditionalInformation, furtherInformationOrRemarks}
import views.html.connectiontoproperty._
import views.html.downloadFORTypeForm.{downloadPDF, downloadPDFReferenceNumber}
import views.html.error.{JsonParseError, error}
import views.html.feedback.{feedback, feedbackThx}
import views.html.notconnected._
import views.html.requestReferenceNumber.{checkYourAnswersRequestReferenceNumber, confirmationRequestReferenceNumber, requestReferenceNumber, requestReferenceNumberContactDetails}
import views.html._

trait FakeViews { this: Injecting =>

  // Test sign
  lazy val testSignView: testSign = inject[testSign]
  // Sign in
  lazy val loginView: login       = inject[login]

  // Error pages
  lazy val jsonErrorView: JsonParseError                                                      = inject[JsonParseError]
  lazy val errorView: error                                                                   = inject[error]
  // Request reference number
  lazy val checkYourAnswersRequestReferenceNumberView: checkYourAnswersRequestReferenceNumber =
    inject[checkYourAnswersRequestReferenceNumber]
  lazy val confirmationRequestReferenceNumberView: confirmationRequestReferenceNumber         =
    inject[confirmationRequestReferenceNumber]

  // generic confirmation page
  lazy val genericRemoveConfirmationView = inject[genericRemoveConfirmation]
  // Connection to the property

  lazy val areYouThirdPartyView: areYouThirdParty                                                 = inject[areYouThirdParty]
  lazy val areYouStillConnectedView: areYouStillConnected                                         = inject[areYouStillConnected]
  lazy val connectionToThePropertyView: connectionToTheProperty                                   = inject[connectionToTheProperty]
  lazy val editAddressView: editAddress                                                           = inject[editAddress]
  lazy val requestReferenceAddressView: requestReferenceNumber                                    = inject[requestReferenceNumber]
  lazy val downloadPDFReferenceNumberView: downloadPDFReferenceNumber                             = inject[downloadPDFReferenceNumber]
  lazy val downloadPDFView: downloadPDF                                                           = inject[downloadPDF]
  lazy val requestReferenceNumberContactDetailsView: requestReferenceNumberContactDetails         =
    inject[requestReferenceNumberContactDetails]
  lazy val checkYourAnswersConnectionToProperty: checkYourAnswersConnectionToProperty             =
    inject[checkYourAnswersConnectionToProperty]
  lazy val checkYourAnswersConnectionToVacantProperty: checkYourAnswersConnectionToVacantProperty =
    inject[checkYourAnswersConnectionToVacantProperty]
//  lazy val confirmationConnectionToProperty: confirmationConnectionToProperty                     = inject[confirmationConnectionToProperty]
//  lazy val confirmationVacantProperty: confirmationVacantProperty                                 = inject[confirmationVacantProperty]
  lazy val confirmation: confirmation                                                             = inject[confirmation]
  lazy val confirmationRequestReferenceNumber: confirmationRequestReferenceNumber                 =
    inject[confirmationRequestReferenceNumber]
  lazy val tradingNameOperatingFromProperty: tradingNameOperatingFromProperty                     = inject[tradingNameOperatingFromProperty]
  lazy val tradingNameOwnThePropertyView: tradingNameOwnTheProperty                               = inject[tradingNameOwnTheProperty]
  lazy val tradingNamePayRentView: tradingNamePayingRent                                          = inject[tradingNamePayingRent]
  lazy val vacantPropertiesStartDateView: vacantPropertyStartDate                                 = inject[vacantPropertyStartDate]
  lazy val isRentReceivedFromLettingView: isRentReceivedFromLetting                               = inject[isRentReceivedFromLetting]
  lazy val provideContactDetailsView: provideContactDetails                                       = inject[provideContactDetails]
  lazy val addAnotherLettingPartOfPropertyView: addAnotherLettingPartOfProperty                   =
    inject[addAnotherLettingPartOfProperty]
  lazy val tenantDetailsView: tenantDetails                                                       = inject[tenantDetails]
  lazy val lettingPartOfPropertyRentDetailsView: lettingPartOfPropertyRentDetails                 =
    inject[lettingPartOfPropertyRentDetails]
  lazy val lettingPartOfPropertyRentIncludesView: lettingPartOfPropertyRentIncludes               =
    inject[lettingPartOfPropertyRentIncludes]
  // Not connected
  val pastConnectionView: pastConnection                                                          = inject[pastConnection]
  val removeConnectionView: removeConnection                                                      = inject[removeConnection]
  val checkYourAnswersNotConnectedView: checkYourAnswersNotConnected                              = inject[checkYourAnswersNotConnected]
//  val confirmationNotConnectedView: confirmationNotConnected                                      = inject[confirmationNotConnected]
  val vacantPropertiesView: vacantProperties                                                      = inject[vacantProperties]

  // About you and the property
  lazy val aboutYouView: aboutYou                                                     = inject[aboutYou]
  lazy val aboutThePropertyView: aboutTheProperty                                     = inject[aboutTheProperty]
  lazy val aboutThePropertyStringView: aboutThePropertyString                         = inject[aboutThePropertyString]
  lazy val propertyCurrentlyUsedView: propertyCurrentlyUsed                           = inject[propertyCurrentlyUsed]
  lazy val websiteForPropertyView: websiteForProperty                                 = inject[websiteForProperty]
  lazy val premisesLicenceGrantedView: premisesLicenseGranted                         = inject[premisesLicenseGranted]
  lazy val premisesLicenceGrantedDetailsView: premisesLicenseGrantedDetails           = inject[premisesLicenseGrantedDetails]
  lazy val licensableActivitiesView: licensableActivities                             = inject[licensableActivities]
  lazy val licensableActivitiesDetailsView: licensableActivitiesDetails               = inject[licensableActivitiesDetails]
  lazy val premisesLicensableView: premisesLicenseConditions                          = inject[premisesLicenseConditions]
  lazy val premisesLicenceConditionsDetailsView: premisesLicenseConditionsDetails     =
    inject[premisesLicenseConditionsDetails]
  lazy val enforcementActionsTakenView: enforcementActionBeenTaken                    = inject[enforcementActionBeenTaken]
  lazy val enforcemenntActionBeenTakenDetailsView: enforcementActionBeenTakenDetails  =
    inject[enforcementActionBeenTakenDetails]
  lazy val tiedForGoodsView: tiedForGoods                                             = inject[tiedForGoods]
  lazy val tiedForGoodsDetailsView: tiedForGoodsDetails                               = inject[tiedForGoodsDetails]
  lazy val checkYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty     =
    inject[checkYourAnswersAboutTheProperty]
  lazy val charityQuestionView: charityQuestion                                       = inject[charityQuestion]
  lazy val tradingActivityView: tradingActivity                                       = inject[tradingActivity]
  lazy val renewablesPlantView: renewablesPlant                                       = inject[renewablesPlant]
  lazy val threeYearsConstructedView: threeYearsConstructed                           = inject[threeYearsConstructed]
  lazy val costsBreakdownView: costsBreakdown                                         = inject[costsBreakdown]
  lazy val plantAndTechnologyView: plantAndTechnology                                 = inject[plantAndTechnology]
  lazy val generatorCapacityView: generatorCapacity                                   = inject[generatorCapacity]
  lazy val batteriesCapacityView: batteriesCapacity                                   = inject[batteriesCapacity]
  //  6048
  lazy val commercialLettingQuestionView: commercialLettingQuestion                   = inject[commercialLettingQuestion]
  lazy val commercialLettingAvailabilityView: commercialLettingAvailability           = inject[commercialLettingAvailability]
  lazy val commercialLettingAvailabilityWelshView: commercialLettingAvailabilityWelsh =
    inject[commercialLettingAvailabilityWelsh]
  lazy val completedCommercialLettingsView: completedCommercialLettings               = inject[completedCommercialLettings]
  lazy val completedCommercialLettingsWelshView: completedCommercialLettingsWelsh     =
    inject[completedCommercialLettingsWelsh]
  lazy val partsUnavailableView: partsUnavailable                                     = inject[partsUnavailable]
  lazy val occupiersDetailsView: occupiersDetails                                     = inject[occupiersDetails]
  lazy val occupiersDetailsListView: occupiersDetailsList                             = inject[occupiersDetailsList]

  // Accommodation details
  lazy val accommodationUnitView: accommodationUnit6048                           = inject[accommodationUnit6048]
  lazy val availableRoomsView: availableRooms6048                                 = inject[availableRooms6048]
  lazy val accommodationLettingHistoryView: accommodationLettingHistory6048       = inject[accommodationLettingHistory6048]
  lazy val highSeasonTariffView: highSeasonTariff6048                             = inject[highSeasonTariff6048]
  lazy val includedTariffItemsView: includedTariffItems6048                       = inject[includedTariffItems6048]
  lazy val accommodationUnitListView: accommodationUnitList6048                   = inject[accommodationUnitList6048]
  lazy val removeLastUnitView: removeLastUnit6048                                 = inject[removeLastUnit6048]
  lazy val addedMaximumAccommodationUnitsView: addedMaximumAccommodationUnits6048 =
    inject[addedMaximumAccommodationUnits6048]
  lazy val accommodationDetailsCYAView: accommodationDetailsCYA6048               = inject[accommodationDetailsCYA6048]

  // About your trading history
  lazy val aboutYourTradingHistoryView: aboutYourTradingHistory                                     = inject[aboutYourTradingHistory]
  lazy val financialYearEndView: financialYearEnd                                                   = inject[financialYearEnd]
  lazy val financialYearEndDatesView: financialYearEndDates                                         = inject[financialYearEndDates]
  lazy val editFinancialYearEndDateView: editFinancialYearEndDate                                   = inject[editFinancialYearEndDate]
  lazy val financialYearEndDatesSummaryView: financialYearEndDatesSummary                           = inject[financialYearEndDatesSummary]
  lazy val financialYearsView: financialYears                                                       = inject[financialYears]
  lazy val turnoverView: turnover                                                                   = inject[turnover]
  lazy val turnover6020View: turnover6020                                                           = inject[turnover6020]
  lazy val turnover6030View: turnover6030                                                           = inject[turnover6030]
  lazy val otherCostsView: otherCosts                                                               = inject[otherCosts]
  lazy val electricityGenerated6076View: electricityGenerated6076                                   = inject[electricityGenerated6076]
  lazy val grossReceiptsExcludingVATView: grossReceiptsExcludingVAT                                 = inject[grossReceiptsExcludingVAT]
  lazy val grossReceiptsForBaseLoadView: grossReceiptsForBaseLoad                                   = inject[grossReceiptsForBaseLoad]
  lazy val premisesCostsView: premisesCosts                                                         = inject[premisesCosts]
  lazy val otherIncome6076View: otherIncome6076                                                     = inject[otherIncome6076]
  lazy val costOfSales6076View: costOfSales6076                                                     = inject[costOfSales6076]
  lazy val costOfSales6076IntermittentView: costOfSales6076Intermittent                             = inject[costOfSales6076Intermittent]
  lazy val staffCostsView: staffCosts                                                               = inject[staffCosts]
  lazy val operationalExpenses6076View: operationalExpenses6076                                     = inject[operationalExpenses6076]
  lazy val headOfficeExpenses6076View: headOfficeExpenses6076                                       = inject[headOfficeExpenses6076]
  lazy val fixedOperatingExpensesView: fixedOperatingExpenses                                       = inject[fixedOperatingExpenses]
  lazy val variableOperatingExpensesView: variableOperatingExpenses                                 = inject[variableOperatingExpenses]
  lazy val totalPayrollCostsView: totalPayrollCosts                                                 = inject[totalPayrollCosts]
  lazy val bunkeredFuelQuestionView: bunkeredFuelQuestion                                           = inject[bunkeredFuelQuestion]
  lazy val bunkerFuelCardDetailsView: bunkerFuelCardsDetails                                        = inject[bunkerFuelCardsDetails]
  lazy val addAnotherBunkerFuelCardsDetailsView: addAnotherBunkerFuelCardDetails                    =
    inject[addAnotherBunkerFuelCardDetails]
  lazy val acceptLowMarginFuelCardView: acceptLowMarginFuelCard                                     = inject[acceptLowMarginFuelCard]
  lazy val changeOccupationAndAccountingInfoView: changeOccupationAndAccountingInfo                 =
    inject[changeOccupationAndAccountingInfo]
  lazy val areYouVATRegisteredView: areYouVATRegistered                                             = inject[areYouVATRegistered]
  lazy val income6048View: income6048                                                               = inject[income6048]
  lazy val fixedCosts6048View: fixedCosts6048                                                       = inject[fixedCosts6048]
  lazy val accountingCosts6048View: accountingCosts6048                                             = inject[accountingCosts6048]
  lazy val administrativeCosts6048View: administrativeCosts6048                                     = inject[administrativeCosts6048]
  lazy val operationalCosts6048View: operationalCosts6048                                           = inject[operationalCosts6048]
  lazy val lowMarginFuelCardsDetailsView: lowMarginFuelCardsDetails                                 = inject[lowMarginFuelCardsDetails]
  lazy val addAnotherLowMarginFuelCardsDetailsView: addAnotherLowMarginFuelCardDetails              =
    inject[addAnotherLowMarginFuelCardDetails]
  lazy val totalFuelSoldView: totalFuelSold                                                         = inject[totalFuelSold]
  lazy val percentageFromFuelCardsView: percentageFromFuelCards                                     = inject[percentageFromFuelCards]
  lazy val customerCreditAccountsView: customerCreditAccounts                                       = inject[customerCreditAccounts]
  lazy val bunkeredFuelSoldView: bunkeredFuelSold                                                   = inject[bunkeredFuelSold]
  lazy val incomeExpenditureSummaryView: incomeExpenditureSummary                                   = inject[incomeExpenditureSummary]
  lazy val incomeExpenditureSummary6076View: incomeExpenditureSummary6076                           = inject[incomeExpenditureSummary6076]
  lazy val unusualCircumstancesView: unusualCircumstances                                           = inject[unusualCircumstances]
  lazy val whatYouWillNeedView: whatYouWillNeed                                                     = inject[whatYouWillNeed]
  lazy val electricVehicleChargingPointsView: electricVehicleChargingPoints                         = inject[electricVehicleChargingPoints]
  lazy val grossReceiptsCaravanFleetHireView: grossReceiptsCaravanFleetHire6045                     =
    inject[grossReceiptsCaravanFleetHire6045]
  lazy val staticCaravansView: staticCaravans                                                       = inject[staticCaravans]
  lazy val caravansOpenAllYearView: caravansOpenAllYear                                             = inject[caravansOpenAllYear]
  lazy val caravansTrading6045View: caravansTrading6045                                             = inject[caravansTrading6045]
  lazy val caravansAgeCategoriesView: caravansAgeCategories                                         = inject[caravansAgeCategories]
  lazy val caravansTotalSiteCapacityView: caravansTotalSiteCapacity                                 = inject[caravansTotalSiteCapacity]
  lazy val caravansPerServiceView: caravansPerService                                               = inject[caravansPerService]
  lazy val caravansAnnualPitchFeeView: caravansAnnualPitchFee                                       = inject[caravansAnnualPitchFee]
  lazy val otherHolidayAccommodationView: otherHolidayAccommodation                                 = inject[otherHolidayAccommodation]
  lazy val totalSiteCapacity6045View: totalSiteCapacity6045                                         = inject[totalSiteCapacity6045]
  lazy val checkYourAnswersTentingPitchesView: checkYourAnswersTentingPitches                       = inject[checkYourAnswersTentingPitches]
  lazy val tentingPitchesAllYearView: tentingPitchesAllYear                                         = inject[tentingPitchesAllYear]
  lazy val tentingPitchesOnSiteView: tentingPitchesOnSite                                           = inject[tentingPitchesOnSite]
  lazy val pitchesForCaravansView: pitchesForCaravans                                               = inject[pitchesForCaravans]
  lazy val pitchesForGlampingView: pitchesForGlamping                                               = inject[pitchesForGlamping]
  lazy val rallyAreasView: rallyAreas                                                               = inject[rallyAreas]
  lazy val tentingPitchesTotalView: tentingPitchesTotal                                             = inject[tentingPitchesTotal]
  lazy val tentingPitchesCertificatedView: tentingPitchesCertificated                               = inject[tentingPitchesCertificated]
  lazy val otherHolidayAccommodationDetailsView: otherHolidayAccommodationDetails                   =
    inject[otherHolidayAccommodationDetails]
  lazy val additionalActivitiesOnSiteView: additionalActivitiesOnSite                               = inject[additionalActivitiesOnSite]
  lazy val additionalActivitiesAllYearView: additionalActivitiesAllYear                             = inject[additionalActivitiesAllYear]
  lazy val additionalShopsView: additionalShops                                                     = inject[additionalShops]
  lazy val additionalCateringView: additionalCatering                                               = inject[additionalCatering]
  lazy val additionalBarsClubsView: additionalBarsClubs                                             = inject[additionalBarsClubs]
  lazy val additionalAmusementsView: additionalAmusements                                           = inject[additionalAmusements]
  lazy val additionalMiscView: additionalMisc                                                       = inject[additionalMisc]
  lazy val checkYourAnswersAdditionalActivities: checkYourAnswersAdditionalActivities               =
    inject[checkYourAnswersAdditionalActivities]
  lazy val checkYourAnswersTentingPitches: checkYourAnswersTentingPitches                           =
    inject[checkYourAnswersTentingPitches]
  lazy val grossReceiptsLettingUnitsView: grossReceiptsLettingUnits6045                             = inject[grossReceiptsLettingUnits6045]
  lazy val grossReceiptsSubLetUnitsView: grossReceiptsSubLetUnits6045                               = inject[grossReceiptsSubLetUnits6045]
  lazy val checkYourAnswersAdditionalActivitiesView: checkYourAnswersAdditionalActivities           =
    inject[checkYourAnswersAdditionalActivities]
  lazy val checkYourAnswersOtherHolidayAccommodationView: checkYourAnswersOtherHolidayAccommodation =
    inject[checkYourAnswersOtherHolidayAccommodation]
  lazy val checkYourAnswersAboutTheTradingHistoryView: checkYourAnswersAboutTheTradingHistory       =
    inject[checkYourAnswersAboutTheTradingHistory]

  // About the franchise or letting
  lazy val franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty                        =
    inject[franchiseOrLettingsTiedToProperty]
  lazy val cateringOperationView: cateringOperationOrLettingAccommodation                                  =
    inject[cateringOperationOrLettingAccommodation]
  lazy val cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails                    =
    inject[cateringOperationOrLettingAccommodationDetails]
  lazy val franchiseTypeDetailsView: franchiseTypeDetails                                                  =
    inject[franchiseTypeDetails]
  lazy val concessionTypeDetailsView: concessionTypeDetails                                                =
    inject[concessionTypeDetails]
  lazy val lettingTypeDetailsView: lettingTypeDetails                                                      =
    inject[lettingTypeDetails]
  lazy val rentalIncomeRentView: rentalIncomeRent                                                          =
    inject[rentalIncomeRent]
  lazy val rentalIncomeIncludedView: rentalIncomeIncluded                                                  =
    inject[rentalIncomeIncluded]
  lazy val cateringOperationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails            =
    inject[cateringOperationOrLettingAccommodationRentDetails]
  lazy val cateringOperationRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes          =
    inject[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val feeReceivedView: feeReceived                                                                    = inject[feeReceived]
  lazy val lettingOtherPartOfPropertyView: cateringOperationOrLettingAccommodation                         =
    inject[cateringOperationOrLettingAccommodation]
  lazy val lettingOtherPartOfPropertyDetailsView: cateringOperationOrLettingAccommodationDetails           =
    inject[cateringOperationOrLettingAccommodationDetails]
  lazy val lettingOtherPartOfPropertyRentDetailsView: cateringOperationOrLettingAccommodationRentDetails   =
    inject[cateringOperationOrLettingAccommodationRentDetails]
  lazy val lettingOtherPartOfPropertyRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes =
    inject[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val concessionOrFranchiseView: cateringOperationOrLettingAccommodation                              =
    inject[cateringOperationOrLettingAccommodation]
  lazy val addAnotherOperationConcessionFranchise: addAnotherCateringOperationOrLettingAccommodation       =
    inject[addAnotherCateringOperationOrLettingAccommodation]
  lazy val checkYourAnswersAboutFranchiseOrLettings: checkYourAnswersAboutFranchiseOrLettings              =
    inject[checkYourAnswersAboutFranchiseOrLettings]
  lazy val rentFromConcession: rentFromConcessions                                                         = inject[rentFromConcessions]
  lazy val rentReceivedFromView: rentReceivedFrom                                                          = inject[rentReceivedFrom]
  lazy val calculatingTheRentView: calculatingTheRentFor                                                   = inject[calculatingTheRentFor]
  lazy val typeOfLettingView: typeOfLetting                                                                = inject[typeOfLetting]
  lazy val typeOfIncomeView: typeOfIncome                                                                  = inject[typeOfIncome]
  lazy val telecomMastLettingView: telecomMastLetting                                                      = inject[telecomMastLetting]
  lazy val rentDetailsView: rentDetails                                                                    = inject[rentDetails]
  lazy val otherLettingView: otherLetting                                                                  = inject[otherLetting]
  lazy val atmLettingView: atmLetting                                                                      = inject[atmLetting]
  lazy val advertisingRightView: advertisingRightLetting                                                   = inject[advertisingRightLetting]
  lazy val addOrRemoveLettingView: addOrRemoveLetting                                                      = inject[addOrRemoveLetting]
  lazy val rentalIncomeListView: rentalIncomeList                                                          = inject[rentalIncomeList]
  // About the lease or tenure
  lazy val aboutYourLandlordView: aboutYourLandlord                                                        = inject[aboutYourLandlord]
  lazy val typeOfTenureView: typeOfTenure                                                                  = inject[typeOfTenure]

  lazy val currentRentPayableWithin12MonthsView                               = inject[currentRentPayableWithin12Months]
  lazy val propertyUseLeasebackAgreementView: propertyUseLeasebackArrangement = inject[propertyUseLeasebackArrangement]
  lazy val currentAnnualRentView                                              = inject[currentAnnualRent]
  lazy val leaseOrAgreementYearsView                                          = inject[leaseOrAgreementYears]

  lazy val rentIncludeTradeServicesView                     = inject[rentIncludeTradeServices]
  lazy val doesTheRentPayableView                           = inject[doesTheRentPayable]
  lazy val currentRentFirstPaidView                         = inject[currentRentFirstPaid]
  lazy val ultimatelyResponsibleInsideRepairsView           = inject[ultimatelyResponsibleInsideRepairs]
  lazy val ultimatelyResponsibleOutsideRepairsView          = inject[ultimatelyResponsibleOutsideRepairs]
  lazy val ultimatelyResponsibleBuildingInsuranceView       = inject[ultimatelyResponsibleBuildingInsurance]
  lazy val intervalsOfRentReviewView                        = inject[intervalsOfRentReview]
  lazy val includedInYourRentView                           = inject[includedInYourRent]
  lazy val currentLeaseOrAgreementBeginView                 = inject[currentLeaseOrAgreementBegin]
  lazy val connectedToLandlordView                          = inject[connectedToLandlord]
  lazy val connectedToLandlordDetailsView                   = inject[connectedToLandlordDetails]
  lazy val provideDetailsOfYourLeaseView                    = inject[provideDetailsOfYourLease]
  lazy val rentOpenMarketValueView                          = inject[rentOpenMarketValue]
  lazy val whatIsYourRentBasedOnView                        = inject[whatIsYourRentBasedOn]
  lazy val rentIncreaseAnnuallyWithRPIView                  = inject[rentIncreaseAnnuallyWithRPI]
  lazy val rentIncludeFixtureAndFittingsView                = inject[rentIncludeFixtureAndFittings]
  lazy val rentIncludeFixtureAndFittingsDetailsView         = inject[rentIncludeFixtureAndFittingsDetails]
  lazy val rentIncludeFixtureAndFittingsDetailsTextAreaView = inject[rentIncludeFixtureAndFittingsDetailsTextArea]
  lazy val rentIncludesVatView                              = inject[rentIncludesVat]
  lazy val rentIncludeTradeServicesDetailsView              = inject[rentIncludeTradeServicesDetails]
  lazy val rentIncludeTradeServicesDetailsTextAreaView      = inject[rentIncludeTradeServicesDetailsTextArea]
  lazy val rentPayableVaryAccordingToGrossOrNetView         = inject[rentPayableVaryAccordingToGrossOrNet]
  lazy val legalOrPlanningRestrictionsView                  = inject[legalOrPlanningRestrictions]
  lazy val legalOrPlanningRestrictionsDetailsView           = inject[legalOrPlanningRestrictionsDetails]
  lazy val checkYourAnswersAboutYourLeaseOrTenureView       = inject[checkYourAnswersAboutYourLeaseOrTenure]
  lazy val tenantsLeaseAgreementExpireView                  = inject[tenancyLeaseAgreementExpire]
  lazy val tenantsAdditionsDisregardedDetailsView           = inject[tenantsAdditionsDisregardedDetails]
  lazy val workCarriedOutDetailsView                        = inject[workCarriedOutDetails]
  lazy val workCarriedOutConditionView                      = inject[workCarriedOutCondition]
  lazy val isGivenRentFreePeriodView                        = inject[isGivenRentFreePeriod]
  lazy val rentFreePeriodDetailsView                        = inject[rentFreePeriodDetails]
  lazy val tenantsAdditionsDisregardedView                  = inject[tenantsAdditionsDisregarded]
  lazy val leaseSurrenderedEarlyView                        = inject[leaseSurrenderdEarly]
  lazy val benefitsGivenView                                = inject[benefitsGiven]
  lazy val benefitsGivenDetailsView                         = inject[benefitsGivenDetails]
  lazy val capitalSumDescriptionView                        = inject[capitalSumDescription]
  lazy val methodToFixCurrentRentView                       = inject[methodToFixCurrentRent]
  lazy val rentPayableVaryAccordingToGrossOrNetDetailsView  = inject[rentPayableVaryAccordingToGrossOrNetDetails]
  lazy val rentPayableVaryOnQuantityOfBeersView             = inject[rentPayableVaryOnQuantityOfBeers]
  lazy val incentivesPaymentsConditionsView                 = inject[incentivesPaymentsConditions]
  lazy val paymentWhenLeaseIsGrantedView                    = inject[paymentWhenLeaseIsGranted]
  lazy val propertyUseLeasebackArrangementView              = inject[propertyUseLeasebackArrangement]
  lazy val payACapitalSumView                               = inject[payACapitalSum]
  lazy val payACapitalSumDetailsView                        = inject[payACapitalSumDetails]
  lazy val payACapitalSumAmountDetailsView                  = inject[payACapitalSumAmountDetails]
  lazy val canRentBeReducedOnReviewView                     = inject[canRentBeReducedOnReview]
  lazy val rentPayableVaryOnQuantityOfBeersDetailsView      = inject[rentPayableVaryOnQuantityOfBeersDetails]
  lazy val howIsCurrentRentFixedView                        = inject[howIsCurrentRentFixed]
  lazy val checkYourAnswersAboutLeaseAndTenureView          = inject[checkYourAnswersAboutYourLeaseOrTenure]
  lazy val paymentForTradeServicesView                      = inject[paymentForTradeServices]
  lazy val tradeServicesDescriptionView                     = inject[tradeServicesDescription]
  lazy val tradeServicesListView                            = inject[tradeServicesList]
  lazy val servicePaidSeparatelyView                        = inject[servicePaidSeparately]
  lazy val servicePaidSeparatelyChargeView                  = inject[servicePaidSeparatelyCharge]
  lazy val servicePaidSeparatelyListView                    = inject[servicePaidSeparatelyList]
  lazy val propertyUpdatesView                              = inject[propertyUpdates]
  lazy val rentDevelopedLandView                            = inject[rentDevelopedLand]
  lazy val rentDevelopedLandDetailsView                     = inject[rentDevelopedLandDetails]
  lazy val rentIncludeStructuresBuildingsView               = inject[rentIncludeStructuresBuildings]
  lazy val rentIncludeStructuresBuildingsDetailsView        = inject[rentIncludeStructuresBuildingsDetails]
  lazy val surrenderedLeaseAgreementView                    = inject[surrenderedLeaseAgreementDetails]

  // Car parking lease
  lazy val doesRentIncludeParkingView        = inject[doesRentIncludeParking]
  lazy val includedInRentParkingSpacesView   = inject[includedInRentParkingSpaces]
  lazy val isParkingRentPaidSeparatelyView   = inject[isParkingRentPaidSeparately]
  lazy val rentedSeparatelyParkingSpacesView = inject[rentedSeparatelyParkingSpaces]
  lazy val carParkingAnnualRentView          = inject[carParkingAnnualRent]

  lazy val throughputAffectsRentView        = inject[throughputAffectsRent]
  lazy val throughputAffectsRentDetailsView = inject[throughputAffectsRentDetails]
  lazy val isVATPayableForWholePropertyView = inject[isVATPayableForWholeProperty]
  lazy val isRentUnderReviewView            = inject[isRentUnderReview]
  lazy val rentedEquipmentDetailsView       = inject[rentedEquipmentDetails]
  lazy val includedInRent6020View           = inject[includedInRent6020]

  // Max letting reached
  lazy val maxOfLettingsReachedView = inject[maxOfLettingsReached]

  // Additional information
  lazy val furtherInformationOrRemarksView: furtherInformationOrRemarks                     = inject[furtherInformationOrRemarks]
  lazy val alternativeContactDetailsView: alternativeContactDetails                         = inject[alternativeContactDetails]
  lazy val contactDetailsQuestionView: contactDetailsQuestion                               = inject[contactDetailsQuestion]
  lazy val checkYourAnswersAdditionalInformationView: checkYourAnswersAdditionalInformation =
    inject[checkYourAnswersAdditionalInformation]

  // Feedback
  val feedbackView: feedback   = inject[feedback]
  val feedbackThx: feedbackThx = inject[feedbackThx]

}
