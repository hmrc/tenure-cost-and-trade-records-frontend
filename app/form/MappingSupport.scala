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

package form

import form.AboutYouEmailMapping.validateAboutYouEmail
import form.AboutYouPhoneNumberMapping.validateAboutYouPhoneNumber
import form.AddressLine2Mapping.validateAddressLineTwo
import form.BuildingNameNumberMapping.validateBuildingNameNumber
import form.CountyMapping.validateCounty
import form.EmailMapping.validateEmail
import form.Form6010.ConditionalMapping.nonEmptyTextOr
import form.PhoneNumberMapping.validatePhoneNumber
import form.TownMapping.validateTown
import form.Scala3EnumFieldMapping.enumMappingRequired
import models.submissions.*
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutyouandtheproperty.*
import models.submissions.common.*
import models.submissions.common.ResponsibilityParty.*
import models.submissions.connectiontoproperty.*
import play.api.data.Forms.*
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.Mapping
import play.api.i18n.Messages
import util.NumberUtil.zeroBigDecimal

import scala.util.Try
import scala.util.matching.Regex

object MappingSupport:

  extension [A](seq: Seq[A])
    def toTuple2: Option[(A, A)] = seq match {
      case Seq(a, b) => Some((a, b))
      case _         => None
    }

    def toTuple3: Option[(A, A, A)] = seq match {
      case Seq(a, b, c) => Some((a, b, c))
      case _            => None
    }

  val typeOfLettingMapping: Mapping[TypeOfLetting]                = enumMappingRequired(TypeOfLetting, Errors.typeOfLetting)
  val typeOfIncomeMapping: Mapping[TypeOfIncome]                  = enumMappingRequired(TypeOfIncome, Errors.typeOfIncome)
  val connectionToThePropertyType: Mapping[ConnectionToProperty]  =
    enumMappingRequired(ConnectionToProperty, Errors.connectionToPropertyError)
  val addressConnectionType: Mapping[AddressConnectionType]       =
    enumMappingRequired(AddressConnectionType, Errors.isConnectedError)
  val methodToFixCurrentRentType: Mapping[MethodToFixCurrentRent] =
    enumMappingRequired(MethodToFixCurrentRent, Errors.methodToFixCurrentRents)
  val renewablesPlantMapping: Mapping[RenewablesPlantType]        =
    enumMappingRequired(RenewablesPlantType, Errors.renewablesPlant)
  val outsideRepairsType: Mapping[OutsideRepairs]                 = enumMappingRequired(OutsideRepairs, Errors.outsideRepairs)
  val insideRepairsType: Mapping[InsideRepairs]                   = enumMappingRequired(InsideRepairs, Errors.insideRepairs)
  val buildingInsuranceType: Mapping[BuildingInsurance]           =
    enumMappingRequired(BuildingInsurance, Errors.buildingInsurance)

  val howIsCurrentRentFixedType: Mapping[CurrentRentFixed]      =
    enumMappingRequired(CurrentRentFixed, Errors.howIsCurrentRentFixed)
  val whatIsYourRentBasedOnType: Mapping[CurrentRentBasedOn]    =
    enumMappingRequired(CurrentRentBasedOn, Errors.currentRentBasedOn)
  val currentPropertyUsedMapping: Mapping[CurrentPropertyUsed]  =
    enumMappingRequired(CurrentPropertyUsed, Errors.propertyCurrentlyUsed)
  val tiedForGoodsDetailsType: Mapping[TiedForGoodsInformation] =
    enumMappingRequired(TiedForGoodsInformation, Errors.tiedForGoodsDetails)

  val postcode: Mapping[String] = PostcodeMapping.postcode()

  private val postcodeAlternativeContact: Mapping[String] = PostcodeMapping.postcodeAlternativeContact()

  private val decimalRegex         = """^[0-9]{1,10}\.?[0-9]{0,2}$"""
  private val cdbMaxCurrencyAmount = 9999999.99
  private val numberRegex: Regex   = """^[-+]?\d+$""".r

  def createYesNoType(errorMessage: String): Mapping[AnswersYesNo] = enumMappingRequired(AnswersYesNo, errorMessage)

  def currencyMapping(fieldErrorPart: String = ""): Mapping[BigDecimal] = default(text, "")
    .verifying(nonEmpty(errorMessage = Errors.annualRentExcludingVAT + fieldErrorPart))
    .verifying(
      Errors.annualRentExcludingVATCurrency + fieldErrorPart,
      x => x == "" || (x.replace(",", "").matches(decimalRegex) && BigDecimal(x.replace(",", "")) >= 0.000)
    )
    .transform((s: String) => BigDecimal(s.replace(",", "")), (v: BigDecimal) => v.toString)
    .verifying(Errors.maxCurrencyAmountExceeded + fieldErrorPart, _ <= cdbMaxCurrencyAmount)

  val contactDetailsMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> validatePhoneNumber,
      "email" -> validateEmail
    )(ContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))

  val contactDetailsAboutYouMapping: Mapping[ContactDetails] =
    mapping(
      "phone" -> validateAboutYouPhoneNumber,
      "email" -> validateAboutYouEmail
    )(ContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))

  def addressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def landlordAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def alternativeAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcodeAlternativeContact
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def cateringAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def lettingPartOfPropertyAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> nonEmptyTextOr("correspondenceAddress.postcode", postcode, "error.postcode.required")
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def editAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def yourContactDetailsMapping: Mapping[YourContactDetails] = mapping(
    "fullName"                                   -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.fullName.required"),
      maxLength(50, "error.fullNameProvideDetails.maxLength")
    ),
    "contactDetails"                             -> contactDetailsMapping,
    "provideContactDetailsAdditionalInformation" -> optional(
      default(text, "").verifying(maxLength(2000, "error.char.count.maxLength"))
    )
  )(YourContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))

  def correspondenceAddressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> validateBuildingNameNumber,
    "street1"            -> optional(validateAddressLineTwo),
    "town"               -> validateTown,
    "county"             -> optional(validateCounty),
    "postcode"           -> postcode
  )(Address.apply)(o => Some(Tuple.fromProductTyped(o)))

  def between[T](
    minValue: T,
    maxValue: T,
    errorMessage: String = "error.range"
  )(using ordering: scala.math.Ordering[T]): Constraint[T] =
    Constraint[T]("constraint.between", minValue, maxValue) { v =>
      if ordering.compare(v, minValue) < 0 || ordering.compare(v, maxValue) > 0 then
        Invalid(ValidationError(errorMessage, minValue, maxValue))
      else Valid
    }

  def nonEmptyList[T](errorMessage: String = "error.required"): Constraint[List[T]] =
    Constraint[List[T]]("constraint.nonEmptyList") { l =>
      if l.nonEmpty then Valid
      else Invalid(ValidationError(errorMessage))
    }

  def nonEmptySeq[T](errorMessage: String = "error.required"): Constraint[Seq[T]] =
    Constraint[Seq[T]]("constraint.nonEmptySeq") { seq =>
      if seq.nonEmpty then Valid
      else Invalid(ValidationError(errorMessage))
    }

  def noneCantBeSelectedWithOther[T](
    noneOfTheseValue: T,
    errorMessage: String
  ): Constraint[List[T]] =
    Constraint[List[T]]("constraint.noneCantBeSelectedWithOther") { l =>
      if l.size > 1 && l.contains(noneOfTheseValue) then Invalid(ValidationError(errorMessage))
      else Valid
    }

  def noneCantBeSelectedWithOtherSeq[T](
    noneOfTheseValue: T,
    errorMessage: String
  ): Constraint[Seq[T]] =
    Constraint[Seq[T]]("constraint.noneCantBeSelectedWithOtherSeq") { seq =>
      if seq.size > 1 && seq.contains(noneOfTheseValue) then Invalid(ValidationError(errorMessage))
      else Valid
    }

  def weeksInYearMapping: Mapping[Int] =
    weeksMapping("error.weeksInYearMapping.blank", "error.weeksInYearMapping.invalid")

  def tradingPeriodWeeks(year: String)(using messages: Messages): Mapping[Int] =
    weeksMapping(messages("error.weeksMapping.blank", year), messages("error.weeksMapping.invalid", year), 0)

  def weeksMapping(blankErrorMessage: String, invalidErrorMessage: String, minWeeks: Int = 1): Mapping[Int] =
    default(text, "")
      .verifying(blankErrorMessage, _.trim.nonEmpty)
      .transform[Int](
        str => Try(str.toInt).getOrElse(-1),
        _.toString
      )
      .verifying(invalidErrorMessage, (minWeeks to 52).contains(_))

  def nonNegativeNumberWithYear(field: String, year: String, maxValue: Int = 1000000)(using
    messages: Messages
  ): Mapping[Option[Int]] =
    optional(
      text
        .verifying(messages(s"error.$field.nonNumeric", year), numberRegex.matches)
        .transform[Int](_.toInt, _.toString)
        .verifying(messages(s"error.$field.negative", year), _ >= 0)
        .verifying(messages(s"error.$field.maxValue", year, maxValue), _ <= maxValue)
    ).verifying(messages(s"error.$field.required", year), _.isDefined)

  def rallyAreasMapping(year: String)(using messages: Messages): Mapping[Option[BigDecimal]] = optional(
    text
      .verifying(messages(s"error.rallyAreas.areaInHectares.range", year), s => Try(BigDecimal(s)).isSuccess)
      .transform[BigDecimal](
        s => BigDecimal(s),
        _.toString
      )
      .verifying(messages(s"error.rallyAreas.areaInHectares.range", year), _ >= 0)
  ).verifying(messages(s"error.rallyAreas.areaInHectares.required", year), _.isDefined)

  private val salesMax = BigDecimal(1000000000000L)

  def turnoverSalesMappingWithYear(field: String, year: String)(using messages: Messages): Mapping[Option[BigDecimal]] =
    optional(
      text
        .verifying(messages(s"error.$field.range", year), s => Try(BigDecimal(s)).isSuccess)
        .transform[BigDecimal](
          s => BigDecimal(s),
          _.toString
        )
        .verifying(messages(s"error.$field.negative", year), _ >= 0)
        .verifying(messages(s"error.$field.range", year), _ <= salesMax)
    ).verifying(messages(s"error.$field.required", year), _.isDefined)

  def moneyMappingOptional(field: String): Mapping[Option[BigDecimal]] =
    optional(
      text
        .verifying(s"error.$field.nonNumeric", s => Try(BigDecimal(s)).isSuccess)
        .transform[BigDecimal](
          s => BigDecimal(s),
          _.toString
        )
        .verifying(s"error.$field.negative", _ >= 0)
    )

  def moneyMappingRequired(field: String): Mapping[BigDecimal] =
    moneyMappingOptional(field)
      .verifying(s"error.$field.required", _.isDefined)
      .transform[BigDecimal](_.getOrElse(zeroBigDecimal), Option(_))

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

  def mappingPerYear[T](
    financialYears: Seq[String],
    mappingPerYearAndIdx: (String, Int) => (String, Mapping[T])
  ): Mapping[Seq[T]] = {
    val yearsMapping: Seq[(String, Mapping[T])] = financialYears.take(3).zipWithIndex.map(mappingPerYearAndIdx.tupled)

    yearsMapping match {
      case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
      case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
      case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
      case _            => throw new IllegalArgumentException("Financial years sequence is empty")
    }
  }
