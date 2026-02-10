/*
 * Copyright 2026 HM Revenue & Customs
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
import views.html.aboutfranchisesorlettings._
import views.html.aboutthetradinghistory.{whatYouWillNeed, _}
import views.html.aboutyouandtheproperty._
import views.html.accommodation._
import views.html.additionalinformation.{checkYourAnswersAdditionalInformation, furtherInformationOrRemarks}
import views.html.connectiontoproperty._
import views.html.downloadFORTypeForm.downloadPDF
import views.html.error.{JsonParseError, error}
import views.html.feedback.{feedback, feedbackThx}
import views.html.notconnected._
import views.html.requestReferenceNumber.requestReferenceNumberPropertyDetails
import views.html.requestReferenceNumber.requestReferenceNumberContactDetails
import views.html.requestReferenceNumber.requestReferenceNumberCheckYourAnswers
import views.html.requestReferenceNumber.requestReferenceNumberConfirmation
import views.html._

trait FakeViews { this: Injecting =>

  // Test sign
  val testSignView: testSign = inject[testSign]
  // Sign in
  val loginView: login       = inject[login]

  // Error pages
  val jsonErrorView: JsonParseError = inject[JsonParseError]
  val errorView: error              = inject[error]

  // Request reference number
  val requestReferenceNumberPropertyDetailsView: requestReferenceNumberPropertyDetails   =
    inject[requestReferenceNumberPropertyDetails]
  val requestReferenceNumberContactDetailsView: requestReferenceNumberContactDetails     =
    inject[requestReferenceNumberContactDetails]
  val requestReferenceNumberCheckYourAnswersView: requestReferenceNumberCheckYourAnswers =
    inject[requestReferenceNumberCheckYourAnswers]
  val requestReferenceNumberConfirmationView: requestReferenceNumberConfirmation         =
    inject[requestReferenceNumberConfirmation]

  // generic confirmation page
  val genericRemoveConfirmationView: genericRemoveConfirmation = inject[genericRemoveConfirmation]

  // Connection to the property
  val areYouThirdPartyView: areYouThirdParty                                                 = inject[areYouThirdParty]
  val areYouStillConnectedView: areYouStillConnected                                         = inject[areYouStillConnected]
  val connectionToThePropertyView: connectionToTheProperty                                   = inject[connectionToTheProperty]
  val editAddressView: editAddress                                                           = inject[editAddress]
  val referenceNumberView: referenceNumber                                                   = inject[referenceNumber]
  val downloadPDFView: downloadPDF                                                           = inject[downloadPDF]
  val checkYourAnswersConnectionToProperty: checkYourAnswersConnectionToProperty             =
    inject[checkYourAnswersConnectionToProperty]
  val checkYourAnswersConnectionToVacantProperty: checkYourAnswersConnectionToVacantProperty =
    inject[checkYourAnswersConnectionToVacantProperty]
//  val confirmationConnectionToProperty: confirmationConnectionToProperty                     = inject[confirmationConnectionToProperty]
//  val confirmationVacantProperty: confirmationVacantProperty                                 = inject[confirmationVacantProperty]
  val confirmation: confirmation                                                             = inject[confirmation]
  val tradingNameOperatingFromProperty: tradingNameOperatingFromProperty                     = inject[tradingNameOperatingFromProperty]
  val tradingNameOwnThePropertyView: tradingNameOwnTheProperty                               = inject[tradingNameOwnTheProperty]
  val tradingNamePayRentView: tradingNamePayingRent                                          = inject[tradingNamePayingRent]
  val vacantPropertiesStartDateView: vacantPropertyStartDate                                 = inject[vacantPropertyStartDate]
  val isRentReceivedFromLettingView: isRentReceivedFromLetting                               = inject[isRentReceivedFromLetting]
  val provideContactDetailsView: provideContactDetails                                       = inject[provideContactDetails]
  val addAnotherLettingPartOfPropertyView: addAnotherLettingPartOfProperty                   =
    inject[addAnotherLettingPartOfProperty]
  val tenantDetailsView: tenantDetails                                                       = inject[tenantDetails]
  val lettingPartOfPropertyRentDetailsView: lettingPartOfPropertyRentDetails                 =
    inject[lettingPartOfPropertyRentDetails]
  val lettingPartOfPropertyRentIncludesView: lettingPartOfPropertyRentIncludes               =
    inject[lettingPartOfPropertyRentIncludes]
  // Not connected
  val pastConnectionView: pastConnection                                                     = inject[pastConnection]
  val removeConnectionView: removeConnection                                                 = inject[removeConnection]
  val checkYourAnswersNotConnectedView: checkYourAnswersNotConnected                         = inject[checkYourAnswersNotConnected]
//  val confirmationNotConnectedView: confirmationNotConnected                                      = inject[confirmationNotConnected]
  val vacantPropertiesView: vacantProperties                                                 = inject[vacantProperties]

  // About you and the property
  val aboutYouView: aboutYou                                                     = inject[aboutYou]
  val aboutThePropertyView: aboutTheProperty                                     = inject[aboutTheProperty]
  val aboutThePropertyStringView: aboutThePropertyString                         = inject[aboutThePropertyString]
  val propertyCurrentlyUsedView: propertyCurrentlyUsed                           = inject[propertyCurrentlyUsed]
  val websiteForPropertyView: websiteForProperty                                 = inject[websiteForProperty]
  val premisesLicenceGrantedView: premisesLicenseGranted                         = inject[premisesLicenseGranted]
  val premisesLicenceGrantedDetailsView: premisesLicenseGrantedDetails           = inject[premisesLicenseGrantedDetails]
  val licensableActivitiesView: licensableActivities                             = inject[licensableActivities]
  val licensableActivitiesDetailsView: licensableActivitiesDetails               = inject[licensableActivitiesDetails]
  val premisesLicensableView: premisesLicenseConditions                          = inject[premisesLicenseConditions]
  val premisesLicenceConditionsDetailsView: premisesLicenseConditionsDetails     =
    inject[premisesLicenseConditionsDetails]
  val enforcementActionsTakenView: enforcementActionBeenTaken                    = inject[enforcementActionBeenTaken]
  val enforcementActionBeenTakenDetailsView: enforcementActionBeenTakenDetails   =
    inject[enforcementActionBeenTakenDetails]
  val tiedForGoodsView: tiedForGoods                                             = inject[tiedForGoods]
  val tiedForGoodsDetailsView: tiedForGoodsDetails                               = inject[tiedForGoodsDetails]
  val checkYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty     =
    inject[checkYourAnswersAboutTheProperty]
  val charityQuestionView: charityQuestion                                       = inject[charityQuestion]
  val tradingActivityView: tradingActivity                                       = inject[tradingActivity]
  val renewablesPlantView: renewablesPlant                                       = inject[renewablesPlant]
  val threeYearsConstructedView: threeYearsConstructed                           = inject[threeYearsConstructed]
  val costsBreakdownView: costsBreakdown                                         = inject[costsBreakdown]
  val plantAndTechnologyView: plantAndTechnology                                 = inject[plantAndTechnology]
  val generatorCapacityView: generatorCapacity                                   = inject[generatorCapacity]
  val batteriesCapacityView: batteriesCapacity                                   = inject[batteriesCapacity]
  //  6048
  val commercialLettingQuestionView: commercialLettingQuestion                   = inject[commercialLettingQuestion]
  val commercialLettingAvailabilityView: commercialLettingAvailability           = inject[commercialLettingAvailability]
  val commercialLettingAvailabilityWelshView: commercialLettingAvailabilityWelsh =
    inject[commercialLettingAvailabilityWelsh]
  val completedCommercialLettingsView: completedCommercialLettings               = inject[completedCommercialLettings]
  val completedCommercialLettingsWelshView: completedCommercialLettingsWelsh     =
    inject[completedCommercialLettingsWelsh]
  val partsUnavailableView: partsUnavailable                                     = inject[partsUnavailable]
  val occupiersDetailsView: occupiersDetails                                     = inject[occupiersDetails]
  val occupiersDetailsListView: occupiersDetailsList                             = inject[occupiersDetailsList]

  // Accommodation details
  val accommodationUnitView: accommodationUnit6048                     = inject[accommodationUnit6048]
  val availableRoomsView: availableRooms6048                           = inject[availableRooms6048]
  val accommodationLettingHistoryView: accommodationLettingHistory6048 = inject[accommodationLettingHistory6048]
  val highSeasonTariffView: highSeasonTariff6048                       = inject[highSeasonTariff6048]
  val includedTariffItemsView: includedTariffItems6048                 = inject[includedTariffItems6048]
  val accommodationUnitListView: accommodationUnitList6048             = inject[accommodationUnitList6048]
  val removeLastUnitView: removeLastUnit6048                           = inject[removeLastUnit6048]
  val addedMaximumListItemsView: addedMaximumListItems                 = inject[addedMaximumListItems]
  val accommodationDetailsCYAView: accommodationDetailsCYA6048         = inject[accommodationDetailsCYA6048]

  // About your trading history
  val whenDidYouFistOccupyView: whenDidYouFirstOccupy                                          = inject[whenDidYouFirstOccupy]
  val financialYearEndView: financialYearEnd                                                   = inject[financialYearEnd]
  val financialYearEndDatesView: financialYearEndDates                                         = inject[financialYearEndDates]
  val editFinancialYearEndDateView: editFinancialYearEndDate                                   = inject[editFinancialYearEndDate]
  val financialYearEndDatesSummaryView: financialYearEndDatesSummary                           = inject[financialYearEndDatesSummary]
  val checkYourAnswersAccountingInfoView: checkYourAnswersAccountingInfo                       = inject[checkYourAnswersAccountingInfo]
  val turnoverView: turnover                                                                   = inject[turnover]
  val turnover6020View: turnover6020                                                           = inject[turnover6020]
  val turnover6030View: turnover6030                                                           = inject[turnover6030]
  val otherCostsView: otherCosts                                                               = inject[otherCosts]
  val electricityGenerated6076View: electricityGenerated6076                                   = inject[electricityGenerated6076]
  val grossReceiptsExcludingVATView: grossReceiptsExcludingVAT                                 = inject[grossReceiptsExcludingVAT]
  val grossReceiptsForBaseLoadView: grossReceiptsForBaseLoad                                   = inject[grossReceiptsForBaseLoad]
  val premisesCostsView: premisesCosts                                                         = inject[premisesCosts]
  val otherIncome6076View: otherIncome6076                                                     = inject[otherIncome6076]
  val costOfSales6076View: costOfSales6076                                                     = inject[costOfSales6076]
  val costOfSales6076IntermittentView: costOfSales6076Intermittent                             = inject[costOfSales6076Intermittent]
  val staffCostsView: staffCosts                                                               = inject[staffCosts]
  val operationalExpenses6076View: operationalExpenses6076                                     = inject[operationalExpenses6076]
  val headOfficeExpenses6076View: headOfficeExpenses6076                                       = inject[headOfficeExpenses6076]
  val fixedOperatingExpensesView: fixedOperatingExpenses                                       = inject[fixedOperatingExpenses]
  val variableOperatingExpensesView: variableOperatingExpenses                                 = inject[variableOperatingExpenses]
  val totalPayrollCostsView: totalPayrollCosts                                                 = inject[totalPayrollCosts]
  val bunkeredFuelQuestionView: bunkeredFuelQuestion                                           = inject[bunkeredFuelQuestion]
  val bunkerFuelCardDetailsView: bunkerFuelCardsDetails                                        = inject[bunkerFuelCardsDetails]
  val addAnotherBunkerFuelCardsDetailsView: addAnotherBunkerFuelCardDetails                    =
    inject[addAnotherBunkerFuelCardDetails]
  val acceptLowMarginFuelCardView: acceptLowMarginFuelCard                                     = inject[acceptLowMarginFuelCard]
  val changeOccupationAndAccountingInfoView: changeOccupationAndAccountingInfo                 =
    inject[changeOccupationAndAccountingInfo]
  val areYouVATRegisteredView: areYouVATRegistered                                             = inject[areYouVATRegistered]
  val income6048View: income6048                                                               = inject[income6048]
  val fixedCosts6048View: fixedCosts6048                                                       = inject[fixedCosts6048]
  val accountingCosts6048View: accountingCosts6048                                             = inject[accountingCosts6048]
  val administrativeCosts6048View: administrativeCosts6048                                     = inject[administrativeCosts6048]
  val operationalCosts6048View: operationalCosts6048                                           = inject[operationalCosts6048]
  val lowMarginFuelCardsDetailsView: lowMarginFuelCardsDetails                                 = inject[lowMarginFuelCardsDetails]
  val addAnotherLowMarginFuelCardsDetailsView: addAnotherLowMarginFuelCardDetails              =
    inject[addAnotherLowMarginFuelCardDetails]
  val totalFuelSoldView: totalFuelSold                                                         = inject[totalFuelSold]
  val percentageFromFuelCardsView: percentageFromFuelCards                                     = inject[percentageFromFuelCards]
  val customerCreditAccountsView: customerCreditAccounts                                       = inject[customerCreditAccounts]
  val bunkeredFuelSoldView: bunkeredFuelSold                                                   = inject[bunkeredFuelSold]
  val incomeExpenditureSummaryView: incomeExpenditureSummary                                   = inject[incomeExpenditureSummary]
  val incomeExpenditureSummary6076View: incomeExpenditureSummary6076                           = inject[incomeExpenditureSummary6076]
  val unusualCircumstancesView: unusualCircumstances                                           = inject[unusualCircumstances]
  val whatYouWillNeedView: whatYouWillNeed                                                     = inject[whatYouWillNeed]
  val electricVehicleChargingPointsView: electricVehicleChargingPoints                         = inject[electricVehicleChargingPoints]
  val grossReceiptsCaravanFleetHireView: grossReceiptsCaravanFleetHire6045                     =
    inject[grossReceiptsCaravanFleetHire6045]
  val staticCaravansView: staticCaravans                                                       = inject[staticCaravans]
  val caravansTrading6045View: caravansTrading6045                                             = inject[caravansTrading6045]
  val caravansAgeCategoriesView: caravansAgeCategories                                         = inject[caravansAgeCategories]
  val caravansTotalSiteCapacityView: caravansTotalSiteCapacity                                 = inject[caravansTotalSiteCapacity]
  val caravansPerServiceView: caravansPerService                                               = inject[caravansPerService]
  val caravansAnnualPitchFeeView: caravansAnnualPitchFee                                       = inject[caravansAnnualPitchFee]
  val otherHolidayAccommodationView: otherHolidayAccommodation                                 = inject[otherHolidayAccommodation]
  val totalSiteCapacity6045View: totalSiteCapacity6045                                         = inject[totalSiteCapacity6045]
  val checkYourAnswersTentingPitchesView: checkYourAnswersTentingPitches                       = inject[checkYourAnswersTentingPitches]
  val tentingPitchesOnSiteView: tentingPitchesOnSite                                           = inject[tentingPitchesOnSite]
  val pitchesForCaravansView: pitchesForCaravans                                               = inject[pitchesForCaravans]
  val pitchesForGlampingView: pitchesForGlamping                                               = inject[pitchesForGlamping]
  val rallyAreasView: rallyAreas                                                               = inject[rallyAreas]
  val tentingPitchesTotalView: tentingPitchesTotal                                             = inject[tentingPitchesTotal]
  val tentingPitchesCertificatedView: tentingPitchesCertificated                               = inject[tentingPitchesCertificated]
  val additionalActivitiesOnSiteView: additionalActivitiesOnSite                               = inject[additionalActivitiesOnSite]
  val additionalShopsView: additionalShops                                                     = inject[additionalShops]
  val additionalCateringView: additionalCatering                                               = inject[additionalCatering]
  val additionalBarsClubsView: additionalBarsClubs                                             = inject[additionalBarsClubs]
  val additionalAmusementsView: additionalAmusements                                           = inject[additionalAmusements]
  val additionalMiscView: additionalMisc                                                       = inject[additionalMisc]
  val checkYourAnswersAdditionalActivities: checkYourAnswersAdditionalActivities               =
    inject[checkYourAnswersAdditionalActivities]
  val checkYourAnswersTentingPitches: checkYourAnswersTentingPitches                           =
    inject[checkYourAnswersTentingPitches]
  val grossReceiptsLettingUnitsView: grossReceiptsLettingUnits6045                             = inject[grossReceiptsLettingUnits6045]
  val grossReceiptsSubLetUnitsView: grossReceiptsSubLetUnits6045                               = inject[grossReceiptsSubLetUnits6045]
  val checkYourAnswersAdditionalActivitiesView: checkYourAnswersAdditionalActivities           =
    inject[checkYourAnswersAdditionalActivities]
  val checkYourAnswersOtherHolidayAccommodationView: checkYourAnswersOtherHolidayAccommodation =
    inject[checkYourAnswersOtherHolidayAccommodation]
  val checkYourAnswersAboutTheTradingHistoryView: checkYourAnswersAboutTheTradingHistory       =
    inject[checkYourAnswersAboutTheTradingHistory]

  // About the franchise or letting
  val franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty              =
    inject[franchiseOrLettingsTiedToProperty]
  val cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails          =
    inject[cateringOperationOrLettingAccommodationDetails]
  val franchiseTypeDetailsView: franchiseTypeDetails                                        =
    inject[franchiseTypeDetails]
  val concessionTypeDetailsView: concessionTypeDetails                                      =
    inject[concessionTypeDetails]
  val lettingTypeDetailsView: lettingTypeDetails                                            =
    inject[lettingTypeDetails]
  val rentalIncomeRentView: rentalIncomeRent                                                =
    inject[rentalIncomeRent]
  val rentalIncomeIncludedView: rentalIncomeIncluded                                        =
    inject[rentalIncomeIncluded]
  val feeReceivedView: feeReceived                                                          = inject[feeReceived]
  val lettingOtherPartOfPropertyDetailsView: cateringOperationOrLettingAccommodationDetails =
    inject[cateringOperationOrLettingAccommodationDetails]
  val checkYourAnswersAboutFranchiseOrLettings: checkYourAnswersAboutFranchiseOrLettings    =
    inject[checkYourAnswersAboutFranchiseOrLettings]
  val rentReceivedFromView: rentReceivedFrom                                                = inject[rentReceivedFrom]
  val calculatingTheRentView: calculatingTheRentFor                                         = inject[calculatingTheRentFor]
  val typeOfLettingView: typeOfLetting                                                      = inject[typeOfLetting]
  val typeOfIncomeView: typeOfIncome                                                        = inject[typeOfIncome]
  val telecomMastLettingView: telecomMastLetting                                            = inject[telecomMastLetting]
  val rentDetailsView: rentDetails                                                          = inject[rentDetails]
  val otherLettingView: otherLetting                                                        = inject[otherLetting]
  val atmLettingView: atmLetting                                                            = inject[atmLetting]
  val advertisingRightView: advertisingRightLetting                                         = inject[advertisingRightLetting]
  val addOrRemoveLettingView: addOrRemoveLetting                                            = inject[addOrRemoveLetting]
  val rentalIncomeListView: rentalIncomeList                                                = inject[rentalIncomeList]
  // About the lease or tenure
  val aboutYourLandlordView: aboutYourLandlord                                              = inject[aboutYourLandlord]
  val typeOfTenureView: typeOfTenure                                                        = inject[typeOfTenure]

  val currentRentPayableWithin12MonthsView: currentRentPayableWithin12Months = inject[currentRentPayableWithin12Months]
  val propertyUseLeasebackAgreementView: propertyUseLeasebackArrangement     = inject[propertyUseLeasebackArrangement]
  val currentAnnualRentView: currentAnnualRent                               = inject[currentAnnualRent]
  val leaseOrAgreementYearsView: leaseOrAgreementYears                       = inject[leaseOrAgreementYears]

  val rentIncludeTradeServicesView: rentIncludeTradeServices                                         = inject[rentIncludeTradeServices]
  val doesTheRentPayableView: doesTheRentPayable                                                     = inject[doesTheRentPayable]
  val currentRentFirstPaidView: currentRentFirstPaid                                                 = inject[currentRentFirstPaid]
  val ultimatelyResponsibleInsideRepairsView: ultimatelyResponsibleInsideRepairs                     =
    inject[ultimatelyResponsibleInsideRepairs]
  val ultimatelyResponsibleOutsideRepairsView: ultimatelyResponsibleOutsideRepairs                   =
    inject[ultimatelyResponsibleOutsideRepairs]
  val ultimatelyResponsibleBuildingInsuranceView: ultimatelyResponsibleBuildingInsurance             =
    inject[ultimatelyResponsibleBuildingInsurance]
  val isRentReviewPlannedView: isRentReviewPlanned                                                   = inject[isRentReviewPlanned]
  val intervalsOfRentReviewView: intervalsOfRentReview                                               = inject[intervalsOfRentReview]
  val includedInYourRentView: includedInYourRent                                                     = inject[includedInYourRent]
  val currentLeaseOrAgreementBeginView: currentLeaseOrAgreementBegin                                 = inject[currentLeaseOrAgreementBegin]
  val connectedToLandlordView: connectedToLandlord                                                   = inject[connectedToLandlord]
  val connectedToLandlordDetailsView: connectedToLandlordDetails                                     = inject[connectedToLandlordDetails]
  val provideDetailsOfYourLeaseView: provideDetailsOfYourLease                                       = inject[provideDetailsOfYourLease]
  val rentOpenMarketValueView: rentOpenMarketValue                                                   = inject[rentOpenMarketValue]
  val whatIsYourRentBasedOnView: whatIsYourRentBasedOn                                               = inject[whatIsYourRentBasedOn]
  val rentIncreaseAnnuallyWithRPIView: rentIncreaseAnnuallyWithRPI                                   = inject[rentIncreaseAnnuallyWithRPI]
  val rentIncludeFixtureAndFittingsView: rentIncludeFixtureAndFittings                               = inject[rentIncludeFixtureAndFittings]
  val rentIncludeFixtureAndFittingsDetailsView: rentIncludeFixtureAndFittingsDetails                 =
    inject[rentIncludeFixtureAndFittingsDetails]
  val rentIncludeFixtureAndFittingsDetailsTextAreaView: rentIncludeFixtureAndFittingsDetailsTextArea =
    inject[rentIncludeFixtureAndFittingsDetailsTextArea]
  val rentIncludesVatView: rentIncludesVat                                                           = inject[rentIncludesVat]
  val rentIncludeTradeServicesDetailsView: rentIncludeTradeServicesDetails                           = inject[rentIncludeTradeServicesDetails]
  val rentIncludeTradeServicesDetailsTextAreaView: rentIncludeTradeServicesDetailsTextArea           =
    inject[rentIncludeTradeServicesDetailsTextArea]
  val rentPayableVaryAccordingToGrossOrNetView: rentPayableVaryAccordingToGrossOrNet                 =
    inject[rentPayableVaryAccordingToGrossOrNet]
  val legalOrPlanningRestrictionsView: legalOrPlanningRestrictions                                   = inject[legalOrPlanningRestrictions]
  val legalOrPlanningRestrictionsDetailsView: legalOrPlanningRestrictionsDetails                     =
    inject[legalOrPlanningRestrictionsDetails]
  val checkYourAnswersAboutYourLeaseOrTenureView: checkYourAnswersAboutYourLeaseOrTenure             =
    inject[checkYourAnswersAboutYourLeaseOrTenure]
  val tenantsLeaseAgreementExpireView: tenancyLeaseAgreementExpire                                   = inject[tenancyLeaseAgreementExpire]
  val tenantsAdditionsDisregardedDetailsView: tenantsAdditionsDisregardedDetails                     =
    inject[tenantsAdditionsDisregardedDetails]
  val workCarriedOutDetailsView: workCarriedOutDetails                                               = inject[workCarriedOutDetails]
  val workCarriedOutConditionView: workCarriedOutCondition                                           = inject[workCarriedOutCondition]
  val isGivenRentFreePeriodView: isGivenRentFreePeriod                                               = inject[isGivenRentFreePeriod]
  val rentFreePeriodDetailsView: rentFreePeriodDetails                                               = inject[rentFreePeriodDetails]
  val tenantsAdditionsDisregardedView: tenantsAdditionsDisregarded                                   = inject[tenantsAdditionsDisregarded]
  val leaseSurrenderedEarlyView: leaseSurrenderdEarly                                                = inject[leaseSurrenderdEarly]
  val benefitsGivenView: benefitsGiven                                                               = inject[benefitsGiven]
  val benefitsGivenDetailsView: benefitsGivenDetails                                                 = inject[benefitsGivenDetails]
  val capitalSumDescriptionView: capitalSumDescription                                               = inject[capitalSumDescription]
  val methodToFixCurrentRentView: methodToFixCurrentRent                                             = inject[methodToFixCurrentRent]
  val rentPayableVaryAccordingToGrossOrNetDetailsView: rentPayableVaryAccordingToGrossOrNetDetails   =
    inject[rentPayableVaryAccordingToGrossOrNetDetails]
  val rentPayableVaryOnQuantityOfBeersView: rentPayableVaryOnQuantityOfBeers                         = inject[rentPayableVaryOnQuantityOfBeers]
  val incentivesPaymentsConditionsView: incentivesPaymentsConditions                                 = inject[incentivesPaymentsConditions]
  val paymentWhenLeaseIsGrantedView: paymentWhenLeaseIsGranted                                       = inject[paymentWhenLeaseIsGranted]
  val propertyUseLeasebackArrangementView: propertyUseLeasebackArrangement                           = inject[propertyUseLeasebackArrangement]
  val payACapitalSumView: payACapitalSum                                                             = inject[payACapitalSum]
  val payACapitalSumDetailsView: payACapitalSumDetails                                               = inject[payACapitalSumDetails]
  val payACapitalSumAmountDetailsView: payACapitalSumAmountDetails                                   = inject[payACapitalSumAmountDetails]
  val canRentBeReducedOnReviewView: canRentBeReducedOnReview                                         = inject[canRentBeReducedOnReview]
  val rentPayableVaryOnQuantityOfBeersDetailsView: rentPayableVaryOnQuantityOfBeersDetails           =
    inject[rentPayableVaryOnQuantityOfBeersDetails]
  val howIsCurrentRentFixedView: howIsCurrentRentFixed                                               = inject[howIsCurrentRentFixed]
  val checkYourAnswersAboutLeaseAndTenureView: checkYourAnswersAboutYourLeaseOrTenure                =
    inject[checkYourAnswersAboutYourLeaseOrTenure]
  val paymentForTradeServicesView: paymentForTradeServices                                           = inject[paymentForTradeServices]
  val tradeServicesDescriptionView: tradeServicesDescription                                         = inject[tradeServicesDescription]
  val tradeServicesListView: tradeServicesList                                                       = inject[tradeServicesList]
  val servicePaidSeparatelyView: servicePaidSeparately                                               = inject[servicePaidSeparately]
  val servicePaidSeparatelyChargeView: servicePaidSeparatelyCharge                                   = inject[servicePaidSeparatelyCharge]
  val servicePaidSeparatelyListView: servicePaidSeparatelyList                                       = inject[servicePaidSeparatelyList]
  val propertyUpdatesView: propertyUpdates                                                           = inject[propertyUpdates]
  val rentDevelopedLandView: rentDevelopedLand                                                       = inject[rentDevelopedLand]
  val rentDevelopedLandDetailsView: rentDevelopedLandDetails                                         = inject[rentDevelopedLandDetails]
  val rentIncludeStructuresBuildingsView: rentIncludeStructuresBuildings                             = inject[rentIncludeStructuresBuildings]
  val rentIncludeStructuresBuildingsDetailsView: rentIncludeStructuresBuildingsDetails               =
    inject[rentIncludeStructuresBuildingsDetails]
  val surrenderedLeaseAgreementView: surrenderedLeaseAgreementDetails                                = inject[surrenderedLeaseAgreementDetails]

  // Car parking lease
  val doesRentIncludeParkingView: doesRentIncludeParking               = inject[doesRentIncludeParking]
  val includedInRentParkingSpacesView: includedInRentParkingSpaces     = inject[includedInRentParkingSpaces]
  val isParkingRentPaidSeparatelyView: isParkingRentPaidSeparately     = inject[isParkingRentPaidSeparately]
  val rentedSeparatelyParkingSpacesView: rentedSeparatelyParkingSpaces = inject[rentedSeparatelyParkingSpaces]
  val carParkingAnnualRentView: carParkingAnnualRent                   = inject[carParkingAnnualRent]

  val throughputAffectsRentView: throughputAffectsRent               = inject[throughputAffectsRent]
  val throughputAffectsRentDetailsView: throughputAffectsRentDetails = inject[throughputAffectsRentDetails]
  val isVATPayableForWholePropertyView: isVATPayableForWholeProperty = inject[isVATPayableForWholeProperty]
  val isRentUnderReviewView: isRentUnderReview                       = inject[isRentUnderReview]
  val rentedEquipmentDetailsView: rentedEquipmentDetails             = inject[rentedEquipmentDetails]
  val includedInRent6020View: includedInRent6020                     = inject[includedInRent6020]

  // Max letting reached
  val maxOfLettingsReachedView: maxOfLettingsReached = inject[maxOfLettingsReached]

  // Additional information
  val furtherInformationOrRemarksView: furtherInformationOrRemarks                     = inject[furtherInformationOrRemarks]
  val contactDetailsQuestionView: contactDetailsQuestion                               = inject[contactDetailsQuestion]
  val checkYourAnswersAdditionalInformationView: checkYourAnswersAdditionalInformation =
    inject[checkYourAnswersAdditionalInformation]

  // Feedback
  val feedbackView: feedback   = inject[feedback]
  val feedbackThx: feedbackThx = inject[feedbackThx]

}
