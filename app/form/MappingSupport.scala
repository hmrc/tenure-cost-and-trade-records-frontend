/*
 * Copyright 2022 HM Revenue & Customs
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

package form

import form.ConditionalMapping.nonEmptyTextOr
import models.submissions._
import form.Formats._
import form.Formats.userTypeFormat
import play.api.data.Forms.{boolean, default, email, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, minLength, nonEmpty, pattern}
import play.api.data.{Forms, Mapping}
import views.html.Form6010.licensableActivitiesDetails


object MappingSupport {

  val postcodeRegex = """(GIR ?0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) ?[0-9][A-Z-[CIKMOV]]{2})""" //scalastyle:ignore
  val phoneRegex = """^^[0-9\s\+()-]+$"""
  val userType: Mapping[UserType] = Forms.of[UserType]
  val aboutYourPropertyType: Mapping[CurrentPropertyUsed] = Forms.of[CurrentPropertyUsed]
  val buildingOperatingHaveAWebsiteType: Mapping[BuildingOperationHaveAWebsite] = Forms.of[BuildingOperationHaveAWebsite]
  val cateringOperationType: Mapping[CateringOperationOrLettingAccommodation] = Forms.of[CateringOperationOrLettingAccommodation]
  val lettingOtherPartOfPropertiesType: Mapping[LettingOtherPartOfProperties] = Forms.of[LettingOtherPartOfProperties]
  val licensableActivitiesType: Mapping[LicensableActivities] = Forms.of[LicensableActivities]
  val tiedForGoodsType: Mapping[TiedForGoods] = Forms.of[TiedForGoods]
  val premisesLicenseType: Mapping[PremisesLicenses] = Forms.of[PremisesLicenses]
  val tenancyLeaseAgreementType: Mapping[TenancyLeaseAgreements] = Forms.of[TenancyLeaseAgreements]
  val enforcementActionType: Mapping[EnforcementActions] = Forms.of[EnforcementActions]
  val franchiseOrLettingsTiedToPropertyType: Mapping[FranchiseOrLettingsTiedToProperties] = Forms.of[FranchiseOrLettingsTiedToProperties]
  val rentIncreasedAnnuallyWithRPIDetailsType: Mapping[RentIncreasedAnnuallyWithRPIs] = Forms.of[RentIncreasedAnnuallyWithRPIs]
  val rentPayableVaryAccordingToGrossOrNetDetailsType: Mapping[RentPayableVaryAccordingToGrossOrNets] = Forms.of[RentPayableVaryAccordingToGrossOrNets]
  val rentPayableVaryOnQuantityOfBeersType: Mapping[RentPayableVaryOnQuantityOfBeers] = Forms.of[RentPayableVaryOnQuantityOfBeers]
  val rentIncludeTradeServicesType: Mapping[RentIncludeTradesServices] = Forms.of[RentIncludeTradesServices]
  val addressConnectionType: Mapping[AddressConnectionType] = Forms.of[AddressConnectionType]
  val rentIncludeFixturesAndFittingsType: Mapping[RentIncludeFixturesAndFittings] = Forms.of[RentIncludeFixturesAndFittings]
  val rentOpenMarketValuesType: Mapping[RentOpenMarketValues] = Forms.of[RentOpenMarketValues]
  val areYouStillConnectedNoType: Mapping[AreYouStillConnectedNo] = Forms.of[AreYouStillConnectedNo]
  val methodToFixCurrentRentsType: Mapping[MethodToFixCurrentRents] = Forms.of[MethodToFixCurrentRents]

  val formerLeaseSurrenderedType: Mapping[FormerLeaseSurrendered] = Forms.of[FormerLeaseSurrendered]
  val rentReducedOnReviewsType: Mapping[RentReducedOnReviews] = Forms.of[RentReducedOnReviews]
  val capitalSumOrPremiumType: Mapping[CapitalSumOrPremiums] = Forms.of[CapitalSumOrPremiums]
  val receivePaymentWhenLeaseGrantedType: Mapping[ReceivePaymentWhenLeaseGrants] = Forms.of[ReceivePaymentWhenLeaseGrants]

  val vatType: Mapping[VATs] = Forms.of[VATs]
  val nondomesticRatesType: Mapping[NonDomesticRates] = Forms.of[NonDomesticRates]
  val waterChargesType: Mapping[WaterCharges] = Forms.of[WaterCharges]

  val commenceWithinThreeYearsType: Mapping[CommenceWithinThreeYears] = Forms.of[CommenceWithinThreeYears]
  val agreedReviewedAlteredThreeYearsType: Mapping[AgreedReviewedAlteredThreeYears] = Forms.of[AgreedReviewedAlteredThreeYears]
  val rentUnderReviewNegotiatedType: Mapping[RentUnderReviewNegotiated] = Forms.of[RentUnderReviewNegotiated]

  val tiedForGoodsDetailsType: Mapping[TiedForGoodsInformationDetail] = Forms.of[TiedForGoodsInformationDetail]
  val postcode: Mapping[String] = PostcodeMapping.postcode()

  val contactDetailsMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> default(text, "").verifying(
        nonEmpty(errorMessage = Errors.contactPhoneRequired),
        pattern(phoneRegex.r, error = Errors.invalidPhone),
        minLength(11, "error.contact.phone.minLength"),
        maxLength(20, "error.contact.phone.maxLength")
      ),
      "email1" -> default(email, "").verifying(
        nonEmpty(errorMessage = Errors.contactEmailRequired),
        maxLength(50, "contactDetails.email1.email.tooLong")
      )
    )(ContactDetails.apply)(ContactDetails.unapply)

  def addressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1" -> optional(text(maxLength = 50)),
    "street2" -> optional(text(maxLength = 50)),
    "postcode" ->  nonEmptyTextOr("postcode", postcode, "error.postcode.required")
  )(Address.apply)(Address.unapply)

  def mandatoryBooleanWithError(message: String) = {
    optional(boolean)
      .verifying(message, _.isDefined)
      .transform({ s: Option[Boolean] => s.get }, { v: Boolean => Some(v) })
  }

  val mandatoryBoolean = optional(boolean)
    .verifying(Errors.booleanMissing, _.isDefined)
    .transform({ s: Option[Boolean] => s.get }, { v: Boolean => Some(v) })

}
