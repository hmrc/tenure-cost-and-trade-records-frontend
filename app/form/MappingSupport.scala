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

package form

import form.AboutYouEmailMapping.validateAboutYouEmail
import form.AboutYouPhoneNumberMapping.validateAboutYouPhoneNumber
import form.AddressLine2Mapping.validateAddressLineTwo
import form.BuildingNameNumberMapping.validateBuildingNameNumber
import form.CountyMapping.validateCounty
import form.EmailMapping.validateEmail
import form.Form6010.ConditionalMapping.nonEmptyTextOr
import models.submissions._
import form.Formats._
import form.Formats.userTypeFormat
import form.PhoneNumberMapping.validatePhoneNumber
import form.TownMapping.validateTown
import models.submissions.Form6010._
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings._
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.{Address, AnswersYesNo, BuildingInsurance, CYAYesNo, ContactDetails, InsideRepairs, OutsideRepairs}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty, CorrespondenceAddress, EditAddress, VacantPropertiesDetails, YourContactDetails}
import models.submissions.notconnected.PastConnectionType
import models.submissions.requestReferenceNumber.RequestReferenceNumberAddress
import models.AnnualRent
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import play.api.data.{Forms, Mapping}
import play.api.i18n.Messages
import util.NumberUtil.zeroBigDecimal

import scala.util.Try
import scala.util.matching.Regex

object MappingSupport {

  implicit class EnrichedSeq[A](seq: Seq[A]) {
    def toTuple2: Option[(A, A)]    = seq match {
      case Seq(a, b) => Some((a, b))
      case _         => None
    }
    def toTuple3: Option[(A, A, A)] = seq match {
      case Seq(a, b, c) => Some((a, b, c))
      case _            => None
    }
  }

  val userType: Mapping[UserType]                                               = Forms.of[UserType]
  val typeOfLettingMapping: Mapping[TypeOfLetting]                              = Forms.of[TypeOfLetting]
  val connectionToThePropertyType: Mapping[ConnectionToProperty]                = Forms.of[ConnectionToProperty]
  val buildingOperatingHaveAWebsiteType: Mapping[BuildingOperationHaveAWebsite] =
    Forms.of[BuildingOperationHaveAWebsite]
  val vacantPropertiesType: Mapping[VacantPropertiesDetails]                    = Forms.of[VacantPropertiesDetails]
  val cyaYesNo: Mapping[CYAYesNo]                                               = Forms.of[CYAYesNo]
  val addressConnectionType: Mapping[AddressConnectionType]                     = Forms.of[AddressConnectionType]
  val pastConnectionType: Mapping[PastConnectionType]                           = Forms.of[PastConnectionType]
  val methodToFixCurrentRentsType: Mapping[MethodToFixCurrentRents]             = Forms.of[MethodToFixCurrentRents]
  val renewablesPlantMapping: Mapping[RenewablesPlantDetails]                   = Forms.of[RenewablesPlantDetails]
  val outsideRepairsType: Mapping[OutsideRepairs]                               = Forms.of[OutsideRepairs]
  val insideRepairsType: Mapping[InsideRepairs]                                 = Forms.of[InsideRepairs]
  val buildingInsuranceType: Mapping[BuildingInsurance]                         = Forms.of[BuildingInsurance]

  val includeLicenseeType: Mapping[IncludeLicensees]            = Forms.of[IncludeLicensees]
  val includeOtherPropertyType: Mapping[IncludeOtherProperties] = Forms.of[IncludeOtherProperties]
  val onlyPartOfPropertyType: Mapping[OnlyPartOfProperties]     = Forms.of[OnlyPartOfProperties]
  val onlyToLandType: Mapping[OnlyToLands]                      = Forms.of[OnlyToLands]
  val shellUnitType: Mapping[ShellUnits]                        = Forms.of[ShellUnits]

  val howIsCurrentRentFixedType: Mapping[CurrentRentFixed] = Forms.of[CurrentRentFixed]

  val whatIsYourRentBasedOnType: Mapping[CurrentRentBasedOn]                = Forms.of[CurrentRentBasedOn]
  val includedInYourRentInformation: Mapping[IncludedInYourRentInformation] = Forms.of[IncludedInYourRentInformation]

  val currentRentPayableWithin12MonthsType: Mapping[CurrentRentWithin12Months] = Forms.of[CurrentRentWithin12Months]

  val tiedForGoodsDetailsType: Mapping[TiedForGoodsInformation] = Forms.of[TiedForGoodsInformation]

  //Lease or Agreement Details - Three Radio buttons on one page
  val tenancy3Years: Mapping[TenancyThreeYears] = Forms.of[TenancyThreeYears]
  val rent3Years: Mapping[RentThreeYears]       = Forms.of[RentThreeYears]
  val underReview: Mapping[UnderReview]         = Forms.of[UnderReview]

  val postcode: Mapping[String]                   = PostcodeMapping.postcode()
  val postcodeAlternativeContact: Mapping[String] = PostcodeMapping.postcodeAlternativeContact()

  val decimalRegex         = """^[0-9]{1,10}\.?[0-9]{0,2}$"""
  val cdbMaxCurrencyAmount = 9999999.99
  val intRegex: Regex      = """^\d{1,3}$""".r
  val invalidCharRegex     = """^[0-9A-Za-z\s\-\,]+$"""

  private val numberRegex: Regex = """^[-+]?\d+$""".r

  lazy val annualRent: Mapping[AnnualRent] = mapping(
    "annualRentExcludingVat" -> currencyMapping(".annualRentExcludingVat")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  lazy val currentPropertyUsedMapping: Mapping[CurrentPropertyUsed] = Forms.of[CurrentPropertyUsed]

  lazy val rentIncludeFixturesAndFittingsDetails: Mapping[AnnualRent] = mapping(
    "rentIncludeFixturesAndFittingsDetails" -> currencyMapping(".rentIncludeFixturesAndFittingsDetails")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  lazy val rentIncludeTradeServiceDetails: Mapping[AnnualRent] = mapping(
    "sumIncludedInRent" -> currencyMapping(".sumIncludedInRent")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  val currency: Mapping[BigDecimal] = currencyMapping()

  def createYesNoType(errorMessage: String): Mapping[AnswersYesNo] =
    Forms.of[AnswersYesNo](answersYesNoDefFormat(errorMessage))

  def currencyMapping(fieldErrorPart: String = ""): Mapping[BigDecimal] = default(text, "")
    .verifying(nonEmpty(errorMessage = Errors.annualRentExcludingVAT + fieldErrorPart))
    .verifying(
      Errors.annualRentExcludingVATCurrency + fieldErrorPart,
      x => x == "" || ((x.replace(",", "") matches decimalRegex) && BigDecimal(x.replace(",", "")) >= 0.000)
    )
    .transform({ s: String => BigDecimal(s.replace(",", "")) }, { v: BigDecimal => v.toString })
    .verifying(Errors.maxCurrencyAmountExceeded + fieldErrorPart, _ <= cdbMaxCurrencyAmount)

  val contactDetailsMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> validatePhoneNumber,
      "email" -> validateEmail
    )(ContactDetails.apply)(ContactDetails.unapply)

  val contactDetailsAboutYouMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> validateAboutYouPhoneNumber,
      "email" -> validateAboutYouEmail
    )(ContactDetails.apply)(ContactDetails.unapply)

  def addressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(Address.unapply)

  def requestReferenceNumberAddressMapping: Mapping[RequestReferenceNumberAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> nonEmptyTextOr("requestReferenceNumberAddress.postcode", postcode, "error.postcode.required")
  )(RequestReferenceNumberAddress.apply)(RequestReferenceNumberAddress.unapply)

  def landlordAddressMapping: Mapping[LandlordAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(LandlordAddress.apply)(LandlordAddress.unapply)

  def alternativeAddressMapping: Mapping[AlternativeAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcodeAlternativeContact
  )(AlternativeAddress.apply)(AlternativeAddress.unapply)

  def cateringAddressMapping: Mapping[CateringAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(CateringAddress.apply)(CateringAddress.unapply)

  def lettingOtherPartAddressMapping: Mapping[LettingAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> nonEmptyTextOr("lettingAddress.postcode", postcode, "error.postcode.required")
  )(LettingAddress.apply)(LettingAddress.unapply)

  def lettingPartOfPropertyAddressMapping: Mapping[LettingAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> nonEmptyTextOr("correspondenceAddress.postcode", postcode, "error.postcode.required")
  )(LettingAddress.apply)(LettingAddress.unapply)

  def editAddressMapping: Mapping[EditAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(EditAddress.apply)(EditAddress.unapply)

  def yourContactDetailsMapping: Mapping[YourContactDetails] = mapping(
    "fullName"                                   -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.fullName.required"),
      maxLength(50, "error.fullNameProvideDetails.maxLength")
    ),
    "contactDetails"                             -> contactDetailsMapping,
    "provideContactDetailsAdditionalInformation" -> optional(
      default(text, "").verifying(maxLength(1000, "error.char.count.maxLength"))
    )
  )(YourContactDetails.apply)(YourContactDetails.unapply)

  def correspondenceAddressMapping: Mapping[CorrespondenceAddress] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(CorrespondenceAddress.apply)(CorrespondenceAddress.unapply)

  def mandatoryBooleanWithError(message: String) =
    optional(boolean)
      .verifying(message, _.isDefined)
      .transform({ s: Option[Boolean] => s.get }, { v: Boolean => Some(v) })

  val mandatoryBoolean = optional(boolean)
    .verifying(Errors.booleanMissing, _.isDefined)
    .transform({ s: Option[Boolean] => s.get }, { v: Boolean => Some(v) })

  def intMapping(): Mapping[Int] = default(text, "0")
    .verifying("error.invalid_number", x => x == "0" || intRegex.findFirstIn(x).isDefined)
    .transform[Int](_.replace(",", "").toInt, _.toString)
    .verifying(s"error.empty.required", _ >= 1)

  def between[T](
    minValue: T,
    maxValue: T,
    errorMessage: String = "error.range"
  )(implicit ordering: scala.math.Ordering[T]): Constraint[T] =
    Constraint[T]("constraint.between", minValue, maxValue) { v =>
      if (ordering.compare(v, minValue) < 0 || ordering.compare(v, maxValue) > 0) {
        Invalid(ValidationError(errorMessage, minValue, maxValue))
      } else {
        Valid
      }
    }

  def nonEmptyList[T](errorMessage: String = "error.required"): Constraint[List[T]] =
    Constraint[List[T]]("constraint.nonEmptyList") { l =>
      if (l.nonEmpty) Valid
      else Invalid(ValidationError(errorMessage))
    }

  def noneCantBeSelectedWithOther[T](
    noneOfTheseValue: T,
    errorMessage: String
  ): Constraint[List[T]] =
    Constraint[List[T]]("constraint.noneCantBeSelectedWithOther") { l =>
      if (l.size > 1 && l.contains(noneOfTheseValue)) Invalid(ValidationError(errorMessage))
      else Valid
    }

  private val salesMax = BigDecimal(1000000000000L)

  def turnoverSalesMappingWithYear(field: String, year: String)(implicit
    messages: Messages
  ): Mapping[Option[BigDecimal]] = optional(
    text
      .verifying(messages(s"error.$field.range", year), s => Try(BigDecimal(s)).isSuccess)
      .transform[BigDecimal](
        s => BigDecimal(s),
        _.toString
      )
      .verifying(messages(s"error.$field.negative", year), _ >= 0)
      .verifying(messages(s"error.$field.range", year), _ <= salesMax)
  ).verifying(messages(s"error.$field.required", year), _.isDefined)

  def nonNegativeNumber(field: String, defaultValue: String = ""): Mapping[Int] =
    default(text, defaultValue)
      .verifying(s"error.$field.required", _.nonEmpty)
      .verifying(s"error.$field.nonNumeric", s => s.isEmpty || numberRegex.matches(s))
      .transform[Int](_.toInt, _.toString)
      .verifying(s"error.$field.negative", _ >= 0)

  def otherCostValueMapping(field: String): Mapping[Option[BigDecimal]] = optional(
    text
      .transform[BigDecimal](s => Try(BigDecimal(s)).getOrElse(-1), _.toString)
      .verifying(between(zeroBigDecimal, salesMax, s"error.$field.range"))
  ).verifying(s"error.$field.required", _.nonEmpty)

}
