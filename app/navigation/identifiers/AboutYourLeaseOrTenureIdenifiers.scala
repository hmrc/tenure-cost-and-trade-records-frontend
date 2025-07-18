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

package navigation.identifiers

case object AboutTheLandlordPageId extends Identifier { override def toString: String = "aboutTheLandlordPage" }

case object ConnectedToLandlordPageId extends Identifier { override def toString: String = "connectedToLandlordPage" }

case object ConnectedToLandlordDetailsPageId extends Identifier {
  override def toString: String = "connectedToLandlordDetailsPage"
}

case object ProvideDetailsOfYourLeasePageId extends Identifier {
  override def toString: String = "provideDetailsOfYourLeasePage"
}

case object LeaseOrAgreementDetailsPageId extends Identifier {
  override def toString: String = "leaseOrAgreementDetailsPage"
}

case object CurrentRentPayableWithin12monthsPageId extends Identifier {
  override def toString: String = "currentRentPayableWithin12monthsPage"
}

case object PropertyUseLeasebackAgreementId extends Identifier {
  override def toString: String = "propertyUseLeasebackAgreementPage"
}

case object CurrentAnnualRentPageId extends Identifier { override def toString: String = "currentAnnualRentPage" }

case object CurrentRentFirstPaidPageId extends Identifier { override def toString: String = "currentRentFirstPaidPage" }

case object CurrentLeaseBeginPageId extends Identifier { override def toString: String = "currentLeaseBeginPage" }

case object IncludedInYourRentPageId extends Identifier { override def toString: String = "includedInYourRentPage" }

case object DoesRentPayablePageId extends Identifier { override def toString: String = "doesRentPayablePage" }

case object UltimatelyResponsibleBusinessInsurancePageId extends Identifier {
  override def toString: String = "ultimatelyResponsibleBusinessInsurancePage"
}

case object UltimatelyResponsibleInsideRepairsPageId extends Identifier {
  override def toString: String = "ultimatelyResponsibleInsideRepairsPage"
}

case object UltimatelyResponsibleOutsideRepairsPageId extends Identifier {
  override def toString: String = "ultimatelyResponsibleOutsideRepairsPage"
}

case object RentIncludeTradeServicesPageId extends Identifier {
  override def toString: String = "rentIncludeTradeServicesPage"
}

case object RentIncludeTradeServicesDetailsPageId extends Identifier {
  override def toString: String = "rentIncludeTradeServicesDetailsPage"
}
case object RentIncludesVatPageId extends Identifier {
  override def toString: String = "rentIncludesVatPage"
}

case object RentFixtureAndFittingsPageId extends Identifier {
  override def toString: String = "rentFixtureAndFittingsPage"
}

case object RentFixtureAndFittingsDetailsPageId extends Identifier {
  override def toString: String = "rentFixtureAndFittingsDetailsPage"
}

case object RentOpenMarketPageId extends Identifier { override def toString: String = "rentOpenMarketPage" }

case object WhatRentBasedOnPageId extends Identifier { override def toString: String = "whatRentBasedOnPage" }

case object RentIncreaseByRPIPageId extends Identifier { override def toString: String = "rentIncreaseByRPIPage" }

case object RentPayableVaryAccordingToGrossOrNetId extends Identifier {
  override def toString: String = "rentPayableByGrossOrNetPage"
}

case object RentPayableVaryAccordingToGrossOrNetDetailsId extends Identifier {
  override def toString: String = "rentPayableByGrossOrNetDetailsPage"
}

case object rentVaryQuantityOfBeersId extends Identifier {
  override def toString: String = "rentVaryQuantityOfBeersPage"
}

case object rentVaryQuantityOfBeersDetailsId extends Identifier {
  override def toString: String = "rentVaryQuantityOfBeersDetailsPage"
}

case object HowIsCurrentRentFixedId extends Identifier { override def toString: String = "howIsCurrentRentFixedPage" }

case object MethodToFixCurrentRentsId extends Identifier { override def toString: String = "methodFixCurrentRentPage" }

case object IsRentReviewPlannedId extends Identifier:
  override def toString: String = "isRentReviewPlannedPage"

case object IntervalsOfRentReviewId extends Identifier { override def toString: String = "intervalRentReviewPage" }

case object CanRentBeReducedOnReviewId extends Identifier { override def toString: String = "canRentBeReducedPage" }

case object PropertyUpdatesId extends Identifier { override def toString: String = "propertyUpdatesPage" }

case object IncentivesPaymentsConditionsId extends Identifier {
  override def toString: String = "incentivesPaymentsConditionsPage"
}

case object TenantsAdditionsDisregardedId extends Identifier {
  override def toString: String = "tenantsAdditionsDisregardedPage"
}

case object LeaseSurrenderedEarlyId extends Identifier {
  override def toString: String = "leaseSurrenderedEarlyPage"
}
case object BenefitsGivenId extends Identifier {
  override def toString: String = "benefitsGivenPage"
}
case object BenefitsGivenDetailsId extends Identifier {
  override def toString: String = "benefitsGivenDetailsPage"
}
case object CapitalSumDescriptionId extends Identifier {
  override def toString: String = "capitalSumDescriptionPage"
}
case object WorkCarriedOutDetailsId extends Identifier {
  override def toString: String = "workCarriedOutDetailsPage"
}
case object WorkCarriedOutConditionId extends Identifier {
  override def toString: String = "workCarriedOutConditionPage"
}

case object TenantsAdditionsDisregardedDetailsId extends Identifier {
  override def toString: String = "tenantsAdditionsDisregardedDetailsPage"
}

case object PayCapitalSumId extends Identifier { override def toString: String = "payCapitalSumPage" }
case object PayCapitalSumDetailsId extends Identifier { override def toString: String = "payCapitalSumDetailsPage" }
case object PayCapitalSumAmountDetailsId extends Identifier {
  override def toString: String = "payCapitalSumAmountDetailsPage"
}

case object PayWhenLeaseGrantedId extends Identifier { override def toString: String = "payWhenLeaseGrantedPage" }

case object LegalOrPlanningRestrictionId extends Identifier {
  override def toString: String = "legalOrPlanningRestrictionPage"
}

case object LegalOrPlanningRestrictionDetailsId extends Identifier {
  override def toString: String = "legalOrPlanningRestrictionDetailsPage"
}

// 6011 page only
case object TenancyLeaseAgreementExpirePageId extends Identifier {
  override def toString: String = "tenancyLeaseAgreementExpirePage"
}

case object CheckYourAnswersAboutYourLeaseOrTenureId extends Identifier {
  override def toString: String = "checkYourAnswersAboutYourLeaseOrTenurePage"
}

// 6030 page only
case object TradeServicesDescriptionId extends Identifier {
  override def toString: String = "tradeServicesDescriptionPage"
}

case object TradeServicesListId extends Identifier {
  override def toString: String = "tradeServicesListPage"
}

case object PaymentForTradeServicesId extends Identifier {
  override def toString: String = "paymentForTradeServicesPage"
}

case object ServicePaidSeparatelyId extends Identifier {
  override def toString: String = "servicePaidSeparatelyPage"
}

case object ServicePaidSeparatelyChargeId extends Identifier {
  override def toString: String = "servicePaidSeparatelyChargePage"
}

case object ServicePaidSeparatelyListId extends Identifier {
  override def toString: String = "servicePaidSeparatelyListPage"
}

case object TypeOfTenureId extends Identifier {
  override def toString: String = "typeOfTenurePage"
}

case object ThroughputAffectsRentId extends Identifier {
  override def toString: String = "throughputAffectsRentPage"
}

case object ThroughputAffectsRentDetailsId extends Identifier {
  override def toString: String = "throughputAffectsRentDetailsPage"
}

case object IsVATPayableForWholePropertyId extends Identifier {
  override def toString: String = "isVATPayableForWholePropertyPage"
}

case object IsRentUnderReviewId extends Identifier {
  override def toString: String = "isRentUnderReviewPage"
}

case object DoesRentIncludeParkingId extends Identifier {
  override def toString: String = "doesRentIncludeParkingPage"
}

case object IsParkingRentPaidSeparatelyId extends Identifier {
  override def toString: String = "IsParkingRentPaidSeparatelyPage"
}

case object IncludedInRentParkingSpacesId extends Identifier {
  override def toString: String = "includedInRentParkingSpacesPage"
}

case object RentedSeparatelyParkingSpacesId extends Identifier {
  override def toString: String = "rentedSeparatelyParkingSpacesPage"
}

case object CarParkingAnnualRentId extends Identifier {
  override def toString: String = "carParkingAnnualRentPage"
}

case object RentedEquipmentDetailsId extends Identifier {
  override def toString: String = "rentedEquipmentDetailsPage"
}

case object IsGivenRentFreePeriodId extends Identifier {
  override def toString: String = "isGivenRentFreePeriodPage"
}

case object RentFreePeriodDetailsId extends Identifier {
  override def toString: String = "rentFreePeriodDetailsPage"
}

case object IncludedInRent6020Id extends Identifier {
  override def toString: String = "includedInRent6020Page"
}

case object RentDevelopedLandId extends Identifier {
  override def toString: String = "rentDevelopedLandPage"
}

case object RentDevelopedLandDetailsId extends Identifier {
  override def toString: String = "rentDevelopedLandDetailsPage"
}

case object RentIncludeStructuresBuildingsId extends Identifier {
  override def toString: String = "rentIncludeStructuresBuildingsPage"
}

case object RentIncludeStructuresBuildingsDetailsId extends Identifier {
  override def toString: String = "rentIncludeStructuresBuildingsDetailsPage"
}

case object SurrenderedLeaseAgreementDetailsId extends Identifier {
  override def toString: String = "surrenderedLeaseAgreementDetailsPage"
}
