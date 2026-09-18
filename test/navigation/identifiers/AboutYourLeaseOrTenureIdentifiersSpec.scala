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

package navigation.identifiers

import uk.gov.hmrc.vo.unit.test.BaseSpec

class AboutYourLeaseOrTenureIdentifiersSpec extends BaseSpec:

  "Lease or tenure page identifiers" should {
    "be correct for the about landlord page" in {
      AboutTheLandlordPageId.toString shouldBe "aboutTheLandlordPage"
    }

    "be correct for the connected to landlord page" in {
      ConnectedToLandlordPageId.toString shouldBe "connectedToLandlordPage"
    }

    "be correct for the connected to landlord details page" in {
      ConnectedToLandlordDetailsPageId.toString shouldBe "connectedToLandlordDetailsPage"
    }

    "be correct for the current annual rent page" in {
      CurrentAnnualRentPageId.toString shouldBe "currentAnnualRentPage"
    }

    "be correct for the lease or agreement details page" in {
      LeaseOrAgreementDetailsPageId.toString shouldBe "leaseOrAgreementDetailsPage"
    }

    "be correct for the current rent payable within 12 months page" in {
      CurrentRentPayableWithin12monthsPageId.toString shouldBe "currentRentPayableWithin12monthsPage"
    }

    "be correct for the provide details of your lease page" in {
      ProvideDetailsOfYourLeasePageId.toString shouldBe "provideDetailsOfYourLeasePage"
    }

    "be correct for the property use leaseback agreement page" in {
      PropertyUseLeasebackAgreementId.toString shouldBe "propertyUseLeasebackAgreementPage"
    }

    "be correct for the current rent first paid page" in {
      CurrentRentFirstPaidPageId.toString shouldBe "currentRentFirstPaidPage"
    }

    "be correct for the current lease begin page" in {
      CurrentLeaseBeginPageId.toString shouldBe "currentLeaseBeginPage"
    }

    "be correct for the included in your rent page" in {
      IncludedInYourRentPageId.toString shouldBe "includedInYourRentPage"
    }

    "be correct for the does rent payable page" in {
      DoesRentPayablePageId.toString shouldBe "doesRentPayablePage"
    }

    "be correct for the ultimately responsible business insurance page" in {
      UltimatelyResponsibleBusinessInsurancePageId.toString shouldBe "ultimatelyResponsibleBusinessInsurancePage"
    }

    "be correct for the ultimately responsible inside repairs page" in {
      UltimatelyResponsibleInsideRepairsPageId.toString shouldBe "ultimatelyResponsibleInsideRepairsPage"
    }

    "be correct for the ultimately responsible outside repairs page" in {
      UltimatelyResponsibleOutsideRepairsPageId.toString shouldBe "ultimatelyResponsibleOutsideRepairsPage"
    }

    "be correct for the rent includes trade services page" in {
      RentIncludeTradeServicesPageId.toString shouldBe "rentIncludeTradeServicesPage"
    }

    "be correct for the rent includes trade services details page" in {
      RentIncludeTradeServicesDetailsPageId.toString shouldBe "rentIncludeTradeServicesDetailsPage"
    }

    "be correct for the rent includes VAT page" in {
      RentIncludesVatPageId.toString shouldBe "rentIncludesVatPage"
    }

    "be correct for the rent fixture and fittings page" in {
      RentFixtureAndFittingsPageId.toString shouldBe "rentFixtureAndFittingsPage"
    }

    "be correct for the rent fixture and fittings details page" in {
      RentFixtureAndFittingsDetailsPageId.toString shouldBe "rentFixtureAndFittingsDetailsPage"
    }

    "be correct for the rent open market page" in {
      RentOpenMarketPageId.toString shouldBe "rentOpenMarketPage"
    }

    "be correct for the what rent based on page" in {
      WhatRentBasedOnPageId.toString shouldBe "whatRentBasedOnPage"
    }

    "be correct for the rent increase by RPI page" in {
      RentIncreaseByRPIPageId.toString shouldBe "rentIncreaseByRPIPage"
    }

    "be correct for the rent payable vary by gross or net turnover page" in {
      RentPayableVaryAccordingToGrossOrNetId.toString shouldBe "rentPayableByGrossOrNetPage"
    }

    "be correct for the rent payable vary by gross or net turnover details page" in {
      RentPayableVaryAccordingToGrossOrNetDetailsId.toString shouldBe "rentPayableByGrossOrNetDetailsPage"
    }

    "be correct for the rent payable vary by quantity of beers page" in {
      RentVaryQuantityOfBeersId.toString shouldBe "rentVaryQuantityOfBeersPage"
    }

    "be correct for the rent payable vary by quantity of beers details page" in {
      RentVaryQuantityOfBeersDetailsId.toString shouldBe "rentVaryQuantityOfBeersDetailsPage"
    }

    "be correct for the how is current rent fixed page" in {
      HowIsCurrentRentFixedId.toString shouldBe "howIsCurrentRentFixedPage"
    }

    "be correct for the method fix current rent page" in {
      MethodToFixCurrentRentsId.toString shouldBe "methodFixCurrentRentPage"
    }

    "be correct for the interval of rent reviews page" in {
      IntervalsOfRentReviewId.toString shouldBe "intervalRentReviewPage"
    }

    "be correct for the can rent be reduced on review page" in {
      CanRentBeReducedOnReviewId.toString shouldBe "canRentBeReducedPage"
    }

    "be correct for the property updates page" in {
      PropertyUpdatesId.toString shouldBe "propertyUpdatesPage"
    }

    "be correct for the incentives payments and conditions page" in {
      IncentivesPaymentsConditionsId.toString shouldBe "incentivesPaymentsConditionsPage"
    }

    "be correct for the tenants additions disregarded page" in {
      TenantsAdditionsDisregardedId.toString shouldBe "tenantsAdditionsDisregardedPage"
    }

    "be correct for the lease surrendered early page" in {
      LeaseSurrenderedEarlyId.toString shouldBe "leaseSurrenderedEarlyPage"
    }

    "be correct for the benefits given page" in {
      BenefitsGivenId.toString shouldBe "benefitsGivenPage"
    }

    "be correct for the benefits given details page" in {
      BenefitsGivenDetailsId.toString shouldBe "benefitsGivenDetailsPage"
    }

    "be correct for the capital sum description page" in {
      CapitalSumDescriptionId.toString shouldBe "capitalSumDescriptionPage"
    }

    "be correct for the work carried out details page" in {
      WorkCarriedOutDetailsId.toString shouldBe "workCarriedOutDetailsPage"
    }

    "be correct for the work carried out condition page" in {
      WorkCarriedOutConditionId.toString shouldBe "workCarriedOutConditionPage"
    }

    "be correct for the rent free period page" in {
      IsGivenRentFreePeriodId.toString shouldBe "isGivenRentFreePeriodPage"
    }

    "be correct for the rent free period details page" in {
      RentFreePeriodDetailsId.toString shouldBe "rentFreePeriodDetailsPage"
    }

    "be correct for the car parking annual rent page" in {
      CarParkingAnnualRentId.toString shouldBe "carParkingAnnualRentPage"
    }

    "be correct for the does the rent include parking page" in {
      DoesRentIncludeParkingId.toString shouldBe "doesRentIncludeParkingPage"
    }

    "be correct for the included in rent parking spaces page" in {
      IncludedInRentParkingSpacesId.toString shouldBe "includedInRentParkingSpacesPage"
    }

    "be correct for the is parking rent paid separately page" in {
      IsParkingRentPaidSeparatelyId.toString shouldBe "IsParkingRentPaidSeparatelyPage"
    }

    "be correct for the rented equipment details page" in {
      RentedEquipmentDetailsId.toString shouldBe "rentedEquipmentDetailsPage"
    }

    "be correct for the included in rent for 6020 page" in {
      IncludedInRent6020Id.toString shouldBe "includedInRent6020Page"
    }

    "be correct for the service paid separately charge page" in {
      ServicePaidSeparatelyChargeId.toString shouldBe "servicePaidSeparatelyChargePage"
    }

    "be correct for the rented separately parking spaces page" in {
      RentedSeparatelyParkingSpacesId.toString shouldBe "rentedSeparatelyParkingSpacesPage"
    }

    "be correct for the tenants additions disregarded details page" in {
      TenantsAdditionsDisregardedDetailsId.toString shouldBe "tenantsAdditionsDisregardedDetailsPage"
    }

    "be correct for the pay capital sum page" in {
      PayCapitalSumId.toString shouldBe "payCapitalSumPage"
    }

    "be correct for the pay capital sum details page" in {
      PayCapitalSumDetailsId.toString shouldBe "payCapitalSumDetailsPage"
    }

    "be correct for the pay capital sum amount details page" in {
      PayCapitalSumAmountDetailsId.toString shouldBe "payCapitalSumAmountDetailsPage"
    }

    "be correct for the pay when lease granted page" in {
      PayWhenLeaseGrantedId.toString shouldBe "payWhenLeaseGrantedPage"
    }

    "be correct for the legal or planning restrictions page" in {
      LegalOrPlanningRestrictionId.toString shouldBe "legalOrPlanningRestrictionPage"
    }

    "be correct for the legal or planning restrictions details page" in {
      LegalOrPlanningRestrictionDetailsId.toString shouldBe "legalOrPlanningRestrictionDetailsPage"
    }

    "be correct for the tenancy lease agreement expire page (6011 only)" in {
      TenancyLeaseAgreementExpirePageId.toString shouldBe "tenancyLeaseAgreementExpirePage"
    }

    "be correct for the lease or tenure CYA page" in {
      CheckYourAnswersAboutYourLeaseOrTenureId.toString shouldBe "checkYourAnswersAboutYourLeaseOrTenurePage"
    }

    "be correct for the service paid separately page" in {
      ServicePaidSeparatelyId.toString shouldBe "servicePaidSeparatelyPage"
    }

    "be correct for the service paid separately list page" in {
      ServicePaidSeparatelyListId.toString shouldBe "servicePaidSeparatelyListPage"
    }

    "be correct for the payment for trade services page" in {
      PaymentForTradeServicesId.toString shouldBe "paymentForTradeServicesPage"
    }

    "be correct for the trade services description page" in {
      TradeServicesDescriptionId.toString shouldBe "tradeServicesDescriptionPage"
    }

    "be correct for the trade services list page" in {
      TradeServicesListId.toString shouldBe "tradeServicesListPage"
    }

    "be correct for the type of tenure page" in {
      TypeOfTenureId.toString shouldBe "typeOfTenurePage"
    }

    "be correct for the rent under review page" in {
      IsRentUnderReviewId.toString shouldBe "isRentUnderReviewPage"
    }

    "be correct for the is VAT payable for whole property page" in {
      IsVATPayableForWholePropertyId.toString shouldBe "isVATPayableForWholePropertyPage"
    }

    "be correct for the throughput affects rent page" in {
      ThroughputAffectsRentId.toString shouldBe "throughputAffectsRentPage"
    }

    "be correct for the throughput affects rent details page" in {
      ThroughputAffectsRentDetailsId.toString shouldBe "throughputAffectsRentDetailsPage"
    }

    "be correct for the rent developed land page" in {
      RentDevelopedLandId.toString shouldBe "rentDevelopedLandPage"
    }

    "be correct for the rent developed land details page" in {
      RentDevelopedLandDetailsId.toString shouldBe "rentDevelopedLandDetailsPage"
    }

    "be correct for the rent includes structures buildings page" in {
      RentIncludeStructuresBuildingsId.toString shouldBe "rentIncludeStructuresBuildingsPage"
    }

    "be correct for the rent includes structures buildings details page" in {
      RentIncludeStructuresBuildingsDetailsId.toString shouldBe "rentIncludeStructuresBuildingsDetailsPage"
    }

    "be correct for the surrendered lease agreement details page" in {
      SurrenderedLeaseAgreementDetailsId.toString shouldBe "surrenderedLeaseAgreementDetailsPage"
    }
  }
