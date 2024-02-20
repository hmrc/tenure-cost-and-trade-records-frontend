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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import views.html.connectiontoproperty._
import views.html.notconnected._
import views.html.aboutyouandtheproperty._
import views.html.aboutYourLeaseOrTenure._
import views.html.aboutconcessionsorlettings.rentFromConcessions
import views.html.aboutfranchisesorlettings._
import views.html.aboutthetradinghistory._
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty
import views.html.additionalinformation.{checkYourAnswersAdditionalInformation, furtherInformationOrRemarks}
import views.html.aboutyouandtheproperty.{alternativeContactDetails, contactDetailsQuestion}
import views.html.downloadFORTypeForm.{downloadPDF, downloadPDFReferenceNumber}
import views.html.error.JsonParseError
import views.html.feedback.{feedback, feedbackThx}
import views.html.requestReferenceNumber.{checkYourAnswersRequestReferenceNumber, confirmationRequestReferenceNumber, requestReferenceNumber, requestReferenceNumberContactDetails}
import views.html.{confirmation, confirmationConnectionToProperty, confirmationNotConnected, genericRemoveConfirmation, login, maxOfLettingsReached}

trait FakeViews { this: GuiceOneAppPerSuite =>

  // Sign in
  lazy val loginView: login = app.injector.instanceOf[login]

  // Error pages
  lazy val jsonErrorView: JsonParseError                                                      = app.injector.instanceOf[JsonParseError]
  // Request reference number
  lazy val checkYourAnswersRequestReferenceNumberView: checkYourAnswersRequestReferenceNumber =
    app.injector.instanceOf[checkYourAnswersRequestReferenceNumber]
  lazy val confirmationRequestReferenceNumberView: confirmationRequestReferenceNumber         =
    app.injector.instanceOf[confirmationRequestReferenceNumber]

  //generic confirmation page
  lazy val genericRemoveConfirmationView = app.injector.instanceOf[genericRemoveConfirmation]
  // Connection to the property

  lazy val areYouThirdPartyView: areYouThirdParty                                                 = app.injector.instanceOf[areYouThirdParty]
  lazy val areYouStillConnectedView: areYouStillConnected                                         = app.injector.instanceOf[areYouStillConnected]
  lazy val connectionToThePropertyView: connectionToTheProperty                                   = app.injector.instanceOf[connectionToTheProperty]
  lazy val editAddressView: editAddress                                                           = app.injector.instanceOf[editAddress]
  lazy val requestReferenceAddressView: requestReferenceNumber                                    = app.injector.instanceOf[requestReferenceNumber]
  lazy val downloadPDFReferenceNumberView: downloadPDFReferenceNumber                             =
    app.injector.instanceOf[downloadPDFReferenceNumber]
  lazy val downloadPDFView: downloadPDF                                                           =
    app.injector.instanceOf[downloadPDF]
  lazy val requestReferenceNumberContactDetailsView: requestReferenceNumberContactDetails         =
    app.injector.instanceOf[requestReferenceNumberContactDetails]
  lazy val checkYourAnswersConnectionToProperty: checkYourAnswersConnectionToProperty             =
    app.injector.instanceOf[checkYourAnswersConnectionToProperty]
  lazy val checkYourAnswersConnectionToVacantProperty: checkYourAnswersConnectionToVacantProperty =
    app.injector.instanceOf[checkYourAnswersConnectionToVacantProperty]
  lazy val confirmationConnectionToProperty: confirmationConnectionToProperty                     =
    app.injector.instanceOf[confirmationConnectionToProperty]
  lazy val confirmationVacantProperty: confirmationVacantProperty                                 =
    app.injector.instanceOf[confirmationVacantProperty]
  lazy val confirmation: confirmation                                                             =
    app.injector.instanceOf[confirmation]
  lazy val confirmationRequestReferenceNumber: confirmationRequestReferenceNumber                 =
    app.injector.instanceOf[confirmationRequestReferenceNumber]
  lazy val tradingNameOperatingFromProperty: tradingNameOperatingFromProperty                     =
    app.injector.instanceOf[tradingNameOperatingFromProperty]
  lazy val tradingNameOwnThePropertyView: tradingNameOwnTheProperty                               = app.injector.instanceOf[tradingNameOwnTheProperty]
  lazy val tradingNamePayRentView: tradingNamePayingRent                                          = app.injector.instanceOf[tradingNamePayingRent]
  lazy val vacantPropertiesStartDateView: vacantPropertyStartDate                                 = app.injector.instanceOf[vacantPropertyStartDate]
  lazy val isRentReceivedFromLettingView: isRentReceivedFromLetting                               = app.injector.instanceOf[isRentReceivedFromLetting]
  lazy val provideContactDetailsView: provideContactDetails                                       = app.injector.instanceOf[provideContactDetails]
  lazy val addAnotherLettingPartOfPropertyView: addAnotherLettingPartOfProperty                   =
    app.injector.instanceOf[addAnotherLettingPartOfProperty]
  lazy val tenantDetailsView: tenantDetails                                                       = app.injector.instanceOf[tenantDetails]
  lazy val lettingPartOfPropertyRentDetailsView: lettingPartOfPropertyRentDetails                 =
    app.injector.instanceOf[lettingPartOfPropertyRentDetails]
  lazy val lettingPartOfPropertyRentIncludesView: lettingPartOfPropertyRentIncludes               =
    app.injector.instanceOf[lettingPartOfPropertyRentIncludes]
  // Not connected
  val pastConnectionView: pastConnection                                                          = app.injector.instanceOf[pastConnection]
  val removeConnectionView: removeConnection                                                      = app.injector.instanceOf[removeConnection]
  val checkYourAnswersNotConnectedView: checkYourAnswersNotConnected                              =
    app.injector.instanceOf[checkYourAnswersNotConnected]
  val confirmationNotConnectedView: confirmationNotConnected                                      = app.injector.instanceOf[confirmationNotConnected]
  val vacantPropertiesView: vacantProperties                                                      = app.injector.instanceOf[vacantProperties]

  // About you and the property
  lazy val aboutYouView: aboutYou                                                    = app.injector.instanceOf[aboutYou]
  lazy val aboutThePropertyView: aboutTheProperty                                    = app.injector.instanceOf[aboutTheProperty]
  lazy val aboutThePropertyStringView: aboutThePropertyString                        = app.injector.instanceOf[aboutThePropertyString]
  lazy val websiteForPropertyView: websiteForProperty                                = app.injector.instanceOf[websiteForProperty]
  lazy val premisesLicenceGrantedView: premisesLicenseGranted                        = app.injector.instanceOf[premisesLicenseGranted]
  lazy val premisesLicenceGrantedDetailsView: premisesLicenseGrantedDetails          =
    app.injector.instanceOf[premisesLicenseGrantedDetails]
  lazy val licensableActivitiesView: licensableActivities                            = app.injector.instanceOf[licensableActivities]
  lazy val licensableActivitiesDetailsView: licensableActivitiesDetails              =
    app.injector.instanceOf[licensableActivitiesDetails]
  lazy val premisesLicensableView: premisesLicenseConditions                         = app.injector.instanceOf[premisesLicenseConditions]
  lazy val premisesLicenceConditionsDetailsView: premisesLicenseConditionsDetails    =
    app.injector.instanceOf[premisesLicenseConditionsDetails]
  lazy val enforcementActionsTakenView: enforcementActionBeenTaken                   = app.injector.instanceOf[enforcementActionBeenTaken]
  lazy val enforcemenntActionBeenTakenDetailsView: enforcementActionBeenTakenDetails =
    app.injector.instanceOf[enforcementActionBeenTakenDetails]
  lazy val tiedForGoodsView: tiedForGoods                                            = app.injector.instanceOf[tiedForGoods]
  lazy val tiedForGoodsDetailsView: tiedForGoodsDetails                              = app.injector.instanceOf[tiedForGoodsDetails]
  lazy val checkYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty    =
    app.injector.instanceOf[checkYourAnswersAboutTheProperty]
  lazy val charityQuestionView                                                       = app.injector.instanceOf[charityQuestion]
  lazy val tradingActivityView                                                       = app.injector.instanceOf[tradingActivity]

  // About your trading history
  lazy val aboutYourTradingHistoryView: aboutYourTradingHistory                               = app.injector.instanceOf[aboutYourTradingHistory]
  lazy val financialYearEndView: financialYearEnd                                             = app.injector.instanceOf[financialYearEnd]
  lazy val financialYearEndDatesView: financialYearEndDates                                   = app.injector.instanceOf[financialYearEndDates]
  lazy val turnoverView: turnover                                                             = app.injector.instanceOf[turnover]
  lazy val incomeExpenditureSummaryView: incomeExpenditureSummary                             = app.injector.instanceOf[incomeExpenditureSummary]
  lazy val unusualCircumstancesView: unusualCircumstances                                     = app.injector.instanceOf[unusualCircumstances]
  lazy val checkYourAnswersAboutTheTradingHistoryView: checkYourAnswersAboutTheTradingHistory =
    app.injector.instanceOf[checkYourAnswersAboutTheTradingHistory]

  // About the franchise or letting
  lazy val franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty                        =
    app.injector.instanceOf[franchiseOrLettingsTiedToProperty]
  lazy val cateringOperationView: cateringOperationOrLettingAccommodation                                  =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails                    =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val cateringOperationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails            =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val cateringOperationRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes          =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val lettingOtherPartOfPropertyView: cateringOperationOrLettingAccommodation                         =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val lettingOtherPartOfPropertyDetailsView: cateringOperationOrLettingAccommodationDetails           =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val lettingOtherPartOfPropertyRentDetailsView: cateringOperationOrLettingAccommodationRentDetails   =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val lettingOtherPartOfPropertyRentIncludesView: cateringOperationOrLettingAccommodationRentIncludes =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val concessionOrFranchiseView: cateringOperationOrLettingAccommodation                              =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val addAnotherOperationConcessionFranchise: addAnotherCateringOperationOrLettingAccommodation       =
    app.injector.instanceOf[addAnotherCateringOperationOrLettingAccommodation]
  lazy val checkYourAnswersAboutFranchiseOrLettings: checkYourAnswersAboutFranchiseOrLettings              =
    app.injector.instanceOf[checkYourAnswersAboutFranchiseOrLettings]
  lazy val rentFromConcession: rentFromConcessions                                                         =
    app.injector.instanceOf[rentFromConcessions]
  lazy val rentReceivedFromView: rentReceivedFrom                                                          =
    app.injector.instanceOf[rentReceivedFrom]
  lazy val calculatingTheRentView: calculatingTheRentFor                                                   =
    app.injector.instanceOf[calculatingTheRentFor]

  // About the lease or tenure
  lazy val aboutYourLandlordView = app.injector.instanceOf[aboutYourLandlord]

  lazy val currentRentPayableWithin12MonthsView                               =
    app.injector.instanceOf[currentRentPayableWithin12Months]
  lazy val propertyUseLeasebackAgreementView: propertyUseLeasebackArrangement =
    app.injector.instanceOf[propertyUseLeasebackArrangement]
  lazy val currentAnnualRentView                                              =
    app.injector.instanceOf[currentAnnualRent]
  lazy val leaseOrAgreementYearsView                                          =
    app.injector.instanceOf[leaseOrAgreementYears]

  lazy val rentIncludeTradeServicesView                    =
    app.injector.instanceOf[rentIncludeTradeServices]
  lazy val doesTheRentPayableView                          =
    app.injector.instanceOf[doesTheRentPayable]
  lazy val currentRentFirstPaidView                        =
    app.injector.instanceOf[currentRentFirstPaid]
  lazy val ultimatelyResponsibleInsideRepairsView          =
    app.injector.instanceOf[ultimatelyResponsibleInsideRepairs]
  lazy val ultimatelyResponsibleOutsideRepairsView         =
    app.injector.instanceOf[ultimatelyResponsibleOutsideRepairs]
  lazy val ultimatelyResponsibleBuildingInsuranceView      =
    app.injector.instanceOf[ultimatelyResponsibleBuildingInsurance]
  lazy val intervalsOfRentReviewView                       =
    app.injector.instanceOf[intervalsOfRentReview]
  lazy val includedInYourRentView                          =
    app.injector.instanceOf[includedInYourRent]
  lazy val currentLeaseOrAgreementBeginView                =
    app.injector.instanceOf[currentLeaseOrAgreementBegin]
  lazy val connectedToLandlordView                         =
    app.injector.instanceOf[connectedToLandlord]
  lazy val connectedToLandlordDetailsView                  =
    app.injector.instanceOf[connectedToLandlordDetails]
  lazy val rentOpenMarketValueView                         =
    app.injector.instanceOf[rentOpenMarketValue]
  lazy val whatIsYourRentBasedOnView                       =
    app.injector.instanceOf[whatIsYourRentBasedOn]
  lazy val rentIncreaseAnnuallyWithRPIView                 =
    app.injector.instanceOf[rentIncreaseAnnuallyWithRPI]
  lazy val rentIncludeFixtureAndFittingsView               =
    app.injector.instanceOf[rentIncludeFixtureAndFittings]
  lazy val rentIncludeFixtureAndFittingsDetailsView        =
    app.injector.instanceOf[rentIncludeFixtureAndFittingsDetails]
  lazy val rentIncludesVatView                             =
    app.injector.instanceOf[rentIncludesVat]
  lazy val rentIncludeTradeServicesDetailsView             =
    app.injector.instanceOf[rentIncludeTradeServicesDetails]
  lazy val rentPayableVaryAccordingToGrossOrNetView        =
    app.injector.instanceOf[rentPayableVaryAccordingToGrossOrNet]
  lazy val legalOrPlanningRestrictionsView                 =
    app.injector.instanceOf[legalOrPlanningRestrictions]
  lazy val legalOrPlanningRestrictionsDetailsView          =
    app.injector.instanceOf[legalOrPlanningRestrictionsDetails]
  lazy val checkYourAnswersAboutYourLeaseOrTenureView      =
    app.injector.instanceOf[checkYourAnswersAboutYourLeaseOrTenure]
  lazy val tenantsLeaseAgreementExpireView                 =
    app.injector.instanceOf[tenancyLeaseAgreementExpire]
  lazy val tenantsAdditionsDisregardedDetailsView          =
    app.injector.instanceOf[tenantsAdditionsDisregardedDetails]
  lazy val tenantsAdditionsDisregardedView                 =
    app.injector.instanceOf[tenantsAdditionsDisregarded]
  lazy val methodToFixCurrentRentView                      =
    app.injector.instanceOf[methodToFixCurrentRent]
  lazy val rentPayableVaryAccordingToGrossOrNetDetailsView =
    app.injector.instanceOf[rentPayableVaryAccordingToGrossOrNetDetails]
  lazy val rentPayableVaryOnQuantityOfBeersView            =
    app.injector.instanceOf[rentPayableVaryOnQuantityOfBeers]
  lazy val incentivesPaymentsConditionsView                =
    app.injector.instanceOf[incentivesPaymentsConditions]
  lazy val tenancyLeaseAgreementView                       =
    app.injector.instanceOf[tenancyLeaseAgreement]
  lazy val paymentWhenLeaseIsGrantedView                   =
    app.injector.instanceOf[paymentWhenLeaseIsGranted]
  lazy val payACapitalSumView                              =
    app.injector.instanceOf[payACapitalSum]
  lazy val payACapitalSumDetailsView                       =
    app.injector.instanceOf[payACapitalSumDetails]
  lazy val canRentBeReducedOnReviewView                    =
    app.injector.instanceOf[canRentBeReducedOnReview]
  lazy val rentPayableVaryOnQuantityOfBeersDetailsView     =
    app.injector.instanceOf[rentPayableVaryOnQuantityOfBeersDetails]
  lazy val howIsCurrentRentFixedView                       =
    app.injector.instanceOf[howIsCurrentRentFixed]
  lazy val checkYourAnswersAboutLeaseAndTenureView         =
    app.injector.instanceOf[checkYourAnswersAboutYourLeaseOrTenure]

  // Max letting reached
  lazy val maxOfLettingsReachedView =
    app.injector.instanceOf[maxOfLettingsReached]

  // Additional information
  lazy val furtherInformationOrRemarksView: furtherInformationOrRemarks                     =
    app.injector.instanceOf[furtherInformationOrRemarks]
  lazy val alternativeContactDetailsView: alternativeContactDetails                         =
    app.injector.instanceOf[alternativeContactDetails]
  lazy val contactDetailsQuestionView: contactDetailsQuestion                               =
    app.injector.instanceOf[contactDetailsQuestion]
  lazy val checkYourAnswersAdditionalInformationView: checkYourAnswersAdditionalInformation =
    app.injector.instanceOf[checkYourAnswersAdditionalInformation]

  // Feedback
  val feedbackView: feedback   = app.injector.instanceOf[feedback]
  val feedbackThx: feedbackThx = app.injector.instanceOf[feedbackThx]
}
