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

package models.submissions.aboutYourLeaseOrTenure

import actions.SessionRequest
import models.Session
import play.api.libs.json.{Json, OFormat}

//Currently 21 parameters in this case class, so only one more is allowed!
case class AboutLeaseOrAgreementPartTwo(
  rentPayableVaryAccordingToGrossOrNetDetails: Option[RentPayableVaryAccordingToGrossOrNetDetails] = None,
  rentPayableVaryAccordingToGrossOrNetInformationDetails: Option[
    RentPayableVaryAccordingToGrossOrNetInformationDetails
  ] = None,
  rentPayableVaryOnQuantityOfBeersDetails: Option[RentPayableVaryOnQuantityOfBeersDetails] = None,
  rentPayableVaryOnQuantityOfBeersInformationDetails: Option[RentPayableVaryOnQuantityOfBeersInformationDetails] = None,
  howIsCurrentRentFixed: Option[HowIsCurrentRentFixed] = None,
  methodToFixCurrentRentDetails: Option[MethodToFixCurrentRentDetails] = None,
  intervalsOfRentReview: Option[IntervalsOfRentReview] = None,
  canRentBeReducedOnReviewDetails: Option[CanRentBeReducedOnReviewDetails] = None,
  incentivesPaymentsConditionsDetails: Option[IncentivesPaymentsConditionsDetails] = None,
  tenantAdditionsDisregardedDetails: Option[TenantAdditionsDisregardedDetails] = None,
  tenantsAdditionsDisregardedDetails: Option[TenantsAdditionsDisregardedDetails] = None,
  payACapitalSumDetails: Option[PayACapitalSumDetails] = None,
  payACapitalSumInformationDetails: Option[PayACapitalSumInformationDetails] = None, // Added Feb 2024 - 6030 Journey
  capitalSumDescription: Option[CapitalSumDescription] = None, // 6020
  paymentWhenLeaseIsGrantedDetails: Option[PaymentWhenLeaseIsGrantedDetails] = None,
  tenancyLeaseAgreementExpire: Option[TenancyLeaseAgreementExpire] = None,
  legalOrPlanningRestrictions: Option[LegalOrPlanningRestrictions] = None,
  legalOrPlanningRestrictionsDetails: Option[LegalOrPlanningRestrictionsDetails] = None,
  ultimatelyResponsibleInsideRepairs: Option[UltimatelyResponsibleInsideRepairs] = None,
  ultimatelyResponsibleOutsideRepairs: Option[UltimatelyResponsibleOutsideRepairs] = None,
  ultimatelyResponsibleBuildingInsurance: Option[UltimatelyResponsibleBuildingInsurance] = None
)

object AboutLeaseOrAgreementPartTwo {
  implicit val format: OFormat[AboutLeaseOrAgreementPartTwo] = Json.format

  def updateAboutLeaseOrAgreementPartTwo(
    copy: AboutLeaseOrAgreementPartTwo => AboutLeaseOrAgreementPartTwo
  )(implicit sessionRequest: SessionRequest[?]): Session = {
    val currentAboutLeaseOrAgreementPartTwo = sessionRequest.sessionData.aboutLeaseOrAgreementPartTwo

    val updatedAboutLeaseOrAgreementPartTwo = currentAboutLeaseOrAgreementPartTwo match {
      case Some(_) => sessionRequest.sessionData.aboutLeaseOrAgreementPartTwo.map(copy)
      case _       => Some(copy(AboutLeaseOrAgreementPartTwo()))
    }

    sessionRequest.sessionData.copy(aboutLeaseOrAgreementPartTwo = updatedAboutLeaseOrAgreementPartTwo)

  }
}
