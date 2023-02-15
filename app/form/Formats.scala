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

package form

import models._
import models.submissions.Form6010._
import models.submissions._
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings.CateringOperation
import models.submissions.abouttheproperty._
import models.submissions.Form6010.FranchiseOrLettingsTiedToProperty
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty}
import play.api.data.FormError
import play.api.data.format.Formatter

object Formats {

  def namedEnumFormatter[T <: NamedEnum](named: NamedEnumSupport[T], missingCode: String): Formatter[T] =
    new Formatter[T] {

      override val format = Some((missingCode, Nil))

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
        val resOpt = for {
          keyVal        <- data.get(key)
          enumTypeValue <- named.fromName(keyVal)
        } yield Right(enumTypeValue)
        resOpt.getOrElse(Left(Seq(FormError(key, missingCode, Nil))))
      }

      def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
    }

  def namedEnumFormatterWithKeys[T <: NamedEnum](
    named: NamedEnumSupport[T],
    missingCodes: Map[String, String]
  ): Formatter[T] = new Formatter[T] {

    override val format = Some((Errors.required, Nil))

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      val resOpt      = for {
        keyVal        <- data.get(key)
        enumTypeValue <- named.fromName(keyVal)
      } yield Right(enumTypeValue)
      val maybeString = missingCodes.get(key)
      resOpt.getOrElse(Left(Seq(FormError(key, maybeString.getOrElse(Errors.required), Nil))))
    }

    def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
  }

  implicit val userTypeFormat: Formatter[UserType]                                                           = namedEnumFormatter(UserType, Errors.userTypeRequired)
  implicit val connectionToPropertyFormat: Formatter[ConnectionToProperty]                                   =
    namedEnumFormatter(ConnectionToProperty, Errors.booleanMissing)
  implicit val aboutYourPropertyFormat: Formatter[CurrentPropertyUsed]                                       =
    namedEnumFormatter(CurrentPropertyUsed, Errors.currentOccupierName)
  implicit val buildingOperatingHaveAWebsiteFormat: Formatter[BuildingOperationHaveAWebsite]                 =
    namedEnumFormatter(BuildingOperationHaveAWebsite, Errors.booleanMissing)
  implicit val addAnotherCateringOperationOrLettingAccommodationFormat
    : Formatter[AddAnotherCateringOperationOrLettingAccommodations]                                          =
    namedEnumFormatter(AddAnotherCateringOperationOrLettingAccommodation, Errors.booleanMissing)
  implicit val addAnotherLettingOtherPartOfPropertyFormat: Formatter[AddAnotherLettingOtherPartOfProperties] =
    namedEnumFormatter(AddAnotherLettingOtherPartOfProperty, Errors.booleanMissing)
  implicit val cateringOperationsFormat: Formatter[CateringOperation]                                        =
    namedEnumFormatter(CateringOperation, Errors.booleanMissing)
  implicit val lettingOtherPartOfPropertiesFormat: Formatter[LettingOtherPartOfProperty]                     =
    namedEnumFormatter(LettingOtherPartOfProperty, Errors.booleanMissing)
  implicit val tenancyLeaseAgreementsFormat: Formatter[TenancyLeaseAgreements]                               =
    namedEnumFormatter(TenancyLeaseAgreement, Errors.booleanMissing)
  implicit val licensableActivitiesFormat: Formatter[LicensableActivities]                                   =
    namedEnumFormatter(LicensableActivities, Errors.booleanMissing)
  implicit val tiedForGoodsFormat: Formatter[TiedForGoods]                                                   = namedEnumFormatter(TiedForGoods, Errors.booleanMissing)
  implicit val premisesLicenseConditionsFormat: Formatter[PremisesLicenseConditions]                         =
    namedEnumFormatter(PremisesLicenseConditions, Errors.booleanMissing)
  implicit val enforcementActionFormat: Formatter[EnforcementAction]                                         =
    namedEnumFormatter(EnforcementAction, Errors.booleanMissing)
  implicit val franchiseOrLettingsTiedToPropertiesFormat: Formatter[FranchiseOrLettingsTiedToProperty]       =
    namedEnumFormatter(FranchiseOrLettingsTiedToProperty, Errors.booleanMissing)
  implicit val rentIncreasedAnnuallyWithRPIsFormat: Formatter[RentIncreasedAnnuallyWithRPIs]                 =
    namedEnumFormatter(RentIncreasedAnnuallyWithRPI, Errors.booleanMissing)
  implicit val rentPayableVaryAccordingToGrossOrNetsFormat: Formatter[RentPayableVaryAccordingToGrossOrNets] =
    namedEnumFormatter(RentPayableVaryAccordingToGrossOrNet, Errors.booleanMissing)
  implicit val rentPayableVaryOnQuantityOfBeersFormat: Formatter[RentPayableVaryOnQuantityOfBeers]           =
    namedEnumFormatter(RentPayableVaryOnQuantityOfBeer, Errors.booleanMissing)
  implicit val rentIncludeTradeServicesFormat: Formatter[RentIncludeTradesServices]                          =
    namedEnumFormatter(RentIncludeTradesService, Errors.booleanMissing)
  implicit val rentIncludeFixturesAndFittingsFormat: Formatter[RentIncludeFixturesAndFittings]               =
    namedEnumFormatter(RentIncludeFixturesAndFitting, Errors.booleanMissing)
  implicit val rentOpenMarketValuesFormat: Formatter[RentOpenMarketValues]                                   =
    namedEnumFormatter(RentOpenMarketValue, Errors.booleanMissing)
  implicit val pastConnectionFormat: Formatter[PastConnectionType]                                           =
    namedEnumFormatter(PastConnectionType, Errors.booleanMissing)
  implicit val methodToFixCurrentRentDetailsFormat: Formatter[MethodToFixCurrentRents]                       =
    namedEnumFormatter(MethodToFixCurrentRent, Errors.booleanMissing)
  implicit val tiedForGoodsDetailsFormat: Formatter[TiedForGoodsInformation]                                 =
    namedEnumFormatter(TiedForGoodsInformation, Errors.booleanMissing)
  implicit val addressConnectionTypeFormatter: Formatter[AddressConnectionType]                              =
    namedEnumFormatter(AddressConnectionType, Errors.isConnectedError)

  implicit val formerLeaseSurrenderedFormatter: Formatter[FormerLeaseSurrendered]                =
    namedEnumFormatter(FormerLeaseSurrender, Errors.booleanMissing)
  implicit val rentReducedOnReviewsFormatter: Formatter[RentReducedOnReviews]                    =
    namedEnumFormatter(RentReducedOnReview, Errors.booleanMissing)
  implicit val capitalSumOrPremiumFormatter: Formatter[CapitalSumOrPremiums]                     =
    namedEnumFormatter(CapitalSumOrPremium, Errors.booleanMissing)
  implicit val receivePaymentWhenLeaseGrantedFormatter: Formatter[ReceivePaymentWhenLeaseGrants] =
    namedEnumFormatter(ReceivePaymentWhenLeaseGranted, Errors.booleanMissing)

  implicit val tenantsAdditionsDisregardedFormatter: Formatter[TenantAdditionalDisregarded] =
    namedEnumFormatter(TenantsAdditionsDisregard, Errors.booleanMissing)
  implicit val legalPlanningRestrictionsFormatter: Formatter[LegalPlanningRestrictions]     =
    namedEnumFormatter(LegalPlanningRestriction, Errors.booleanMissing)

  implicit val outsideRepairsFormatter: Formatter[OutsideRepairs]        =
    namedEnumFormatter(OutsideRepair, Errors.booleanMissing)
  implicit val insideRepairsFormatter: Formatter[InsideRepairs]          =
    namedEnumFormatter(InsideRepair, Errors.booleanMissing)
  implicit val buildingInsuranceFormatter: Formatter[BuildingInsurances] =
    namedEnumFormatter(BuildingInsurance, Errors.booleanMissing)

  implicit val vatFormatter: Formatter[VATs]                          = namedEnumFormatter(VAT, Errors.booleanMissing)
  implicit val nondomesticRatesFormatter: Formatter[NonDomesticRates] =
    namedEnumFormatter(NonDomesticRate, Errors.booleanMissing)
  implicit val waterChargesFormatter: Formatter[WaterCharges]         = namedEnumFormatter(WaterCharge, Errors.booleanMissing)

  implicit val commenceWithinThreeYearsFormatter: Formatter[CommenceWithinThreeYears]               =
    namedEnumFormatter(CommenceWithinThreeYears, Errors.booleanMissing)
  implicit val agreedReviewedAlteredThreeYearsFormatter: Formatter[AgreedReviewedAlteredThreeYears] =
    namedEnumFormatter(AgreedReviewedAlteredThreeYears, Errors.booleanMissing)
  implicit val rentUnderReviewNegotiatedFormatter: Formatter[RentUnderReviewNegotiated]             =
    namedEnumFormatter(RentUnderReviewNegotiated, Errors.booleanMissing)

  implicit val includeLicenseeFormatter: Formatter[IncludeLicensees]            =
    namedEnumFormatter(IncludeLicensee, Errors.booleanMissing)
  implicit val includeOtherPropertyFormatter: Formatter[IncludeOtherProperties] =
    namedEnumFormatter(IncludeOtherProperty, Errors.booleanMissing)
  implicit val onlyPartOfPropertyFormatter: Formatter[OnlyPartOfProperties]     =
    namedEnumFormatter(OnlyPartOfProperty, Errors.booleanMissing)
  implicit val onlyToLandFormatter: Formatter[OnlyToLands]                      = namedEnumFormatter(OnlyToLand, Errors.booleanMissing)
  implicit val shellUnitFormatter: Formatter[ShellUnits]                        = namedEnumFormatter(ShellUnit, Errors.booleanMissing)

  implicit val howIsCurrentRentFixedFormatter: Formatter[CurrentRentFixed] =
    namedEnumFormatter(CurrentRentFix, Errors.booleanMissing)

  implicit val currentRentPayableWithin12MonthsFormatter: Formatter[CurrentRentWithin12Months] =
    namedEnumFormatter(CurrentRentWithin12Months, Errors.booleanMissing)

  implicit val whatIsYourRentBasedOnFormatter: Formatter[CurrentRentBasedOn] =
    namedEnumFormatter(CurrentRentBased, Errors.booleanMissing)
}
