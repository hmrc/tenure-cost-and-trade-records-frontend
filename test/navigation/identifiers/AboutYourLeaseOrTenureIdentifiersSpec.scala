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

package navigation.identifiers

import utils.TestBaseSpec

class AboutYourLeaseOrTenureIdentifiersSpec extends TestBaseSpec {

  "About your lease or tenure identifiers" when {

    "Identifier for about your landlord page" in {
      assert(AboutTheLandlordPageId.toString.equals("aboutTheLandlordPage"))
    }

    "Identifier for connected to landlord page" in {
      assert(ConnectedToLandlordPageId.toString.equals("connectedToLandlordPage"))
    }

    "Identifier for connected to landlord details page" in {
      assert(ConnectedToLandlordDetailsPageId.toString.equals("connectedToLandlordDetailsPage"))
    }

    "Identifier for current annual rent page" in {
      assert(CurrentAnnualRentPageId.toString.equals("currentAnnualRentPage"))
    }

    "Identifier for lease agreement details page" in {
      assert(LeaseOrAgreementDetailsPageId.toString.equals("leaseOrAgreementDetailsPage"))
    }

    "Identifier for current rent payable with 12 months page" in {
      assert(CurrentRentPayableWithin12monthsPageId.toString.equals("currentRentPayableWithin12monthsPage"))
    }

    "Identifier for property use leaseback agreement page" in {
      assert(PropertyUseLeasebackAgreementId.toString.equals("propertyUseLeasebackAgreementPage"))
    }

    "Identifier for current rent first paid page" in {
      assert(CurrentRentFirstPaidPageId.toString.equals("currentRentFirstPaidPage"))
    }

    "Identifier for current rent begin page" in {
      assert(CurrentLeaseBeginPageId.toString.equals("currentLeaseBeginPage"))
    }

    "Identifier for included in your rent page" in {
      assert(IncludedInYourRentPageId.toString.equals("includedInYourRentPage"))
    }

    "Identifier for does rent payable include page" in {
      assert(DoesRentPayablePageId.toString.equals("doesRentPayablePage"))
    }

    "Identifier for ultimately responsible business insurance page" in {
      assert(UltimatelyResponsibleBusinessInsurancePageId.toString.equals("ultimatelyResponsibleBusinessInsurancePage"))
    }

    "Identifier for ultimately responsible inside repairs page" in {
      assert(UltimatelyResponsibleInsideRepairsPageId.toString.equals("ultimatelyResponsibleInsideRepairsPage"))
    }

    "Identifier for ultimately responsible outside repairs page" in {
      assert(UltimatelyResponsibleOutsideRepairsPageId.toString.equals("ultimatelyResponsibleOutsideRepairsPage"))
    }

    "Identifier for rent include trade services page" in {
      assert(RentIncludeTradeServicesPageId.toString.equals("rentIncludeTradeServicesPage"))
    }

    "Identifier for rent include trade services details page" in {
      assert(RentIncludesVatPageId.toString.equals("rentIncludesVatPage"))
    }

    "Identifier for rent include vat page" in {
      assert(RentIncludeTradeServicesDetailsPageId.toString.equals("rentIncludeTradeServicesDetailsPage"))
    }

    "Identifier for rent fixture and fittings page" in {
      assert(RentFixtureAndFittingsPageId.toString.equals("rentFixtureAndFittingsPage"))
    }

    "Identifier for rent fixture and fittings details page" in {
      assert(RentFixtureAndFittingsDetailsPageId.toString.equals("rentFixtureAndFittingsDetailsPage"))
    }

    "Identifier for rent open market page" in {
      assert(RentOpenMarketPageId.toString.equals("rentOpenMarketPage"))
    }

    "Identifier for what rent based on page" in {
      assert(WhatRentBasedOnPageId.toString.equals("whatRentBasedOnPage"))
    }

    "Identifier for rent increase by RPI page" in {
      assert(RentIncreaseByRPIPageId.toString.equals("rentIncreaseByRPIPage"))
    }

    "Identifier for rent payable vary by gross or net turnover page" in {
      assert(RentPayableVaryAccordingToGrossOrNetId.toString.equals("rentPayableByGrossOrNetPage"))
    }

    "Identifier for rent payable vary by gross or net turnover details page" in {
      assert(RentPayableVaryAccordingToGrossOrNetDetailsId.toString.equals("rentPayableByGrossOrNetDetailsPage"))
    }

    "Identifier for rent payable vary by beer page" in {
      assert(rentVaryQuantityOfBeersId.toString.equals("rentVaryQuantityOfBeersPage"))
    }

    "Identifier for rent payable vary by beer details page" in {
      assert(rentVaryQuantityOfBeersDetailsId.toString.equals("rentVaryQuantityOfBeersDetailsPage"))
    }

    "Identifier for how current rent fixed page" in {
      assert(HowIsCurrentRentFixedId.toString.equals("howIsCurrentRentFixedPage"))
    }

    "Identifier for method fix current rent page" in {
      assert(MethodToFixCurrentRentsId.toString.equals("methodFixCurrentRentPage"))
    }

    "Identifier for intervals of rent reviews page" in {
      assert(IntervalsOfRentReviewId.toString.equals("intervalRentReviewPage"))
    }

    "Identifier for can rent be reduced on review page" in {
      assert(CanRentBeReducedOnReviewId.toString.equals("canRentBeReducedPage"))
    }

    "Identifier for property updates page" in {
      assert(PropertyUpdatesId.toString.equals("propertyUpdatesPage"))
    }

    "Identifier for conditions payments and conditions page" in {
      assert(IncentivesPaymentsConditionsId.toString.equals("incentivesPaymentsConditionsPage"))
    }

    "Identifier for tenants additions disregarded page" in {
      assert(TenantsAdditionsDisregardedId.toString.equals("tenantsAdditionsDisregardedPage"))
    }

    "Identifier for lease surrendered early page" in {
      assert(LeaseSurrenderedEarlyId.toString.equals("leaseSurrenderedEarlyPage"))
    }

    "Identifier for tenants additions disregarded details page" in {
      assert(TenantsAdditionsDisregardedDetailsId.toString.equals("tenantsAdditionsDisregardedDetailsPage"))
    }

    "Identifier for pay capital sum page" in {
      assert(PayCapitalSumId.toString.equals("payCapitalSumPage"))
    }

    "Identifier for pay capital sum details page" in {
      assert(PayCapitalSumDetailsId.toString.equals("payCapitalSumDetailsPage"))
    }

    "Identifier for pay when lease granted page" in {
      assert(PayWhenLeaseGrantedId.toString.equals("payWhenLeaseGrantedPage"))
    }

    "Identifier for legal or planning restrictions page" in {
      assert(LegalOrPlanningRestrictionId.toString.equals("legalOrPlanningRestrictionPage"))
    }

    "Identifier for legal or planning restrictions details page" in {
      assert(LegalOrPlanningRestrictionDetailsId.toString.equals("legalOrPlanningRestrictionDetailsPage"))
    }

    "Identifier for tenancy lease agreement expire page (6011 only)" in {
      assert(TenancyLeaseAgreementExpirePageId.toString.equals("tenancyLeaseAgreementExpirePage"))
    }

    "Identifier for check your answers about your lease or tenure" in {
      assert(CheckYourAnswersAboutYourLeaseOrTenureId.toString.equals("checkYourAnswersAboutYourLeaseOrTenurePage"))
    }
    "Identifier for service paid separately" in {
      assert(ServicePaidSeparatelyId.toString.equals("servicePaidSeparatelyPage"))
    }
    "Identifier for service paid separately list" in {
      assert(ServicePaidSeparatelyListId.toString.equals("servicePaidSeparatelyListPage"))
    }
    "Identifier for service paid separately charge" in {
      assert(ServicePaidSeparatelyChargeId.toString.equals("servicePaidSeparatelyChargePage"))
    }
    "Identifier for payment for trade services" in {
      assert(PaymentForTradeServicesId.toString.equals("paymentForTradeServicesPage"))
    }
    "Identifier for trade services description page" in {
      assert(TradeServicesDescriptionId.toString.equals("tradeServicesDescriptionPage"))
    }
    "Identifier for trade services list" in {
      assert(TradeServicesListId.toString.equals("tradeServicesListPage"))
    }
    "Identifier for type of tenure" in {
      assert(TypeOfTenureId.toString.equals("typeOfTenurePage"))
    }
  }
}
