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
import views.html.aboutYourLeaseOrTenure._
import views.html.aboutfranchisesorlettings._
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty
import views.html.additionalinformation.furtherInformationOrRemarks
import views.html.form._
import views.html.login

trait FakeViews { this: GuiceOneAppPerSuite =>

  // Sign in
  lazy val loginView = app.injector.instanceOf[login]

  // Connection to the property
  lazy val areYouStillConnectedView    = app.injector.instanceOf[areYouStillConnected]
  lazy val connectionToThePropertyView = app.injector.instanceOf[connectionToTheProperty]
  lazy val editAddressView             = app.injector.instanceOf[editAddress]

  lazy val checkYourAnswersAboutThePropertyView = app.injector.instanceOf[checkYourAnswersAboutTheProperty]

  // About the franchise or letting
  lazy val franchiseOrLettingsTiedToPropertyView      =
    app.injector.instanceOf[franchiseOrLettingsTiedToProperty]
  lazy val cateringOperationView                      =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val cateringOperationDetailsView               =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val cateringOperationRentDetailsView           =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val cateringOperationRentIncludesView          =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val lettingOtherPartOfPropertyView             =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]
  lazy val lettingOtherPartOfPropertyDetailsView      =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationDetails]
  lazy val lettingOtherPartOfPropertyRentDetailsView  =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentDetails]
  lazy val lettingOtherPartOfPropertyRentIncludesView =
    app.injector.instanceOf[cateringOperationOrLettingAccommodationRentIncludes]
  lazy val concessionOrFranchiseView                  =
    app.injector.instanceOf[cateringOperationOrLettingAccommodation]

  // About the lease or tenure
  lazy val aboutYourLandlordView = app.injector.instanceOf[aboutYourLandlord]

  lazy val currentRentPayableWithin12MonthsView =
    app.injector.instanceOf[currentRentPayableWithin12Months]
  lazy val currentAnnualRentView                =
    app.injector.instanceOf[currentAnnualRent]
  lazy val leaseOrAgreementYearsView            =
    app.injector.instanceOf[leaseOrAgreementYears]

  lazy val rentIncludeTradeServicesView                    =
    app.injector.instanceOf[rentIncludeTradeServices]
  lazy val doesTheRentPayableView                          =
    app.injector.instanceOf[doesTheRentPayable]
  lazy val currentRentFirstPaidView                        =
    app.injector.instanceOf[currentRentFirstPaid]
  lazy val ultimatelyResponsibleView                       =
    app.injector.instanceOf[ultimatelyResponsible]
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
  lazy val furtherInformationOrRemarksView                 =
    app.injector.instanceOf[furtherInformationOrRemarks]
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
  lazy val canRentBeReducedOnReviewView                    =
    app.injector.instanceOf[canRentBeReducedOnReview]
  lazy val rentPayableVaryOnQuantityOfBeersDetailsView     =
    app.injector.instanceOf[rentPayableVaryOnQuantityOfBeersDetails]
  lazy val howIsCurrentRentFixedView                       =
    app.injector.instanceOf[howIsCurrentRentFixed]
  lazy val checkYourAnswersAboutLeaseAndTenureView         =
    app.injector.instanceOf[checkYourAnswersAboutYourLeaseOrTenure]

}
