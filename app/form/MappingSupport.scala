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

import form.Form6010.ConditionalMapping.nonEmptyTextOr
import models.submissions._
import form.Formats._
import form.Formats.userTypeFormat
import models.submissions.Form6010._
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings._
import models.submissions.aboutyouandtheproperty._
import models.submissions.additionalinformation.AlternativeAddress
import models.submissions.common.{Address, AnswersYesNo, BuildingInsurance, CYAYesNo, ContactDetails, ContactDetailsAddress, InsideRepairs, OutsideRepairs}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty, CorrespondenceAddress, EditAddress, VacantPropertiesDetails, YourContactDetails}
import models.submissions.notconnected.PastConnectionType
import models.submissions.requestReferenceNumber.RequestReferenceNumberAddress
import models.{AnnualRent, NamedEnum, NamedEnumSupport}
import play.api.data.Forms.{boolean, default, email, list, mapping, nonEmptyText, optional, text}
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.validation.Constraints.{maxLength, minLength, nonEmpty, pattern}
import play.api.data.{FormError, Forms, Mapping}
import util.NumberUtil.zeroBigDecimal

import scala.util.Try
import scala.util.matching.Regex

object MappingSupport {

  val postcodeRegex                                                             =
    """(GIR ?0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) ?[0-9][A-Z-[CIKMOV]]{2})""" //scalastyle:ignore
  val phoneRegex                                                                = """^^[0-9\s\+()-]+$"""
  val userType: Mapping[UserType]                                               = Forms.of[UserType]
  val aboutYourPropertyType: Mapping[CurrentPropertyUsed]                       = Forms.of[CurrentPropertyUsed]
  val connectionToThePropertyType: Mapping[ConnectionToProperty]                = Forms.of[ConnectionToProperty]
  val buildingOperatingHaveAWebsiteType: Mapping[BuildingOperationHaveAWebsite] =
    Forms.of[BuildingOperationHaveAWebsite]
  val vacantPropertiesType: Mapping[VacantPropertiesDetails]                    = Forms.of[VacantPropertiesDetails]
  val cyaYesNo: Mapping[CYAYesNo]                                               = Forms.of[CYAYesNo]
  val addressConnectionType: Mapping[AddressConnectionType]                     = Forms.of[AddressConnectionType]
  val pastConnectionType: Mapping[PastConnectionType]                           = Forms.of[PastConnectionType]
  val methodToFixCurrentRentsType: Mapping[MethodToFixCurrentRents]             = Forms.of[MethodToFixCurrentRents]

//  val responsiblePartyType: Mapping[AnswerResponsibleParty] = Forms.of[AnswerResponsibleParty]

  val outsideRepairsType: Mapping[OutsideRepairs]       = Forms.of[OutsideRepairs]
  val insideRepairsType: Mapping[InsideRepairs]         = Forms.of[InsideRepairs]
  val buildingInsuranceType: Mapping[BuildingInsurance] = Forms.of[BuildingInsurance]

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

  val postcode: Mapping[String] = PostcodeMapping.postcode()

  val decimalRegex          = """^[0-9]{1,10}\.?[0-9]{0,2}$"""
  val cdbMaxCurrencyAmount  = 9999999.99
  val spacesIntRegex: Regex = """^\-?\d{1,10}$""".r
  val intRegex: Regex       = """^\d{1,3}$""".r

  lazy val annualRent: Mapping[AnnualRent] = mapping(
    "annualRentExcludingVat" -> currencyMapping(".annualRentExcludingVat")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  lazy val multipleCurrentPropertyUsedMapping: Mapping[List[CurrentPropertyUsed]] =
    list(nonEmptyText)
      .verifying(
        Constraint[List[String]]("constraint.required") { propertyUsages =>
          if (propertyUsages.nonEmpty) Valid
          else Invalid(ValidationError("error.required.propertyUsages"))
        }
      )
      .verifying(
        "Invalid property used",
        propertyUsages => propertyUsages.forall(str => CurrentPropertyUsed.withName(str).isDefined)
      )
      .transform[List[CurrentPropertyUsed]](
        propertyUsages => propertyUsages.flatMap(str => CurrentPropertyUsed.withName(str)),
        currentPropertyUseds => currentPropertyUseds.map(_.name)
      )

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
      "phone" -> default(text, "").verifying(
        nonEmpty(errorMessage = Errors.contactPhoneRequired),
        pattern(phoneRegex.r, error = Errors.invalidPhone),
        minLength(11, "error.contact.phone.minLength"),
        maxLength(20, "error.contact.phone.maxLength")
      ),
      "email" -> default(email, "").verifying(
        nonEmpty(errorMessage = Errors.contactEmailRequired),
        //TODO Add Regex here
        maxLength(50, "contactDetails.email1.email.tooLong")
      )
    )(ContactDetails.apply)(ContactDetails.unapply)

  def addressMapping: Mapping[Address] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("postcode", postcode, "error.postcode.required")
  )(Address.apply)(Address.unapply)

  def requestReferenceNumberAddressMapping: Mapping[RequestReferenceNumberAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("requestReferenceNumberAddress.postcode", postcode, "error.postcode.required")
  )(RequestReferenceNumberAddress.apply)(RequestReferenceNumberAddress.unapply)

  def landlordAddressMapping: Mapping[LandlordAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("landlordAddress.postcode", postcode, "error.postcode.required")
  )(LandlordAddress.apply)(LandlordAddress.unapply)

  def alternativeAddressMapping: Mapping[AlternativeAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("alternativeContactAddress.postcode", postcode, "error.postcode.required")
  )(AlternativeAddress.apply)(AlternativeAddress.unapply)

  def cateringAddressMapping: Mapping[CateringAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("cateringAddress.postcode", postcode, "error.postcode.required")
  )(CateringAddress.apply)(CateringAddress.unapply)

  def lettingOtherPartAddressMapping: Mapping[LettingAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("lettingAddress.postcode", postcode, "error.postcode.required")
  )(LettingAddress.apply)(LettingAddress.unapply)

  def contactAddressMapping: Mapping[ContactDetailsAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.town.required"),
      maxLength(50, "error.town.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("editAddress.postcode", postcode, "error.postcode.required")
  )(ContactDetailsAddress.apply)(ContactDetailsAddress.unapply)

  def editAddressMapping: Mapping[EditAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.town.required"),
      maxLength(50, "error.town.maxLength")
    ),
    "county"             -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"           -> nonEmptyTextOr("editAddress.postcode", postcode, "error.postcode.required")
  )(EditAddress.apply)(EditAddress.unapply)

  def yourContactDetailsMapping: Mapping[YourContactDetails] = mapping(
    "fullName"                                   -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.fullName.required")
    ),
    "contactDetails"                             -> contactDetailsMapping,
    "provideContactDetailsAdditionalInformation" -> optional(
      default(text, "").verifying(maxLength(1000, "error.char.count.maxLength"))
    )
  )(YourContactDetails.apply)(YourContactDetails.unapply)

  def correspondenceAddressMapping: Mapping[CorrespondenceAddress] = mapping(
    "addressLineOne" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.addressLineOne.required"),
      maxLength(50, "error.addressLineOne.maxLength")
    ),
    "addressLineTwo" -> optional(
      default(text, "").verifying(
        maxLength(50, "error.addressLineTwo.maxLength")
      )
    ),
    "town"           -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.town.maxLength")
    ),
    "county"         -> optional(
      default(text, "").verifying(
        maxLength(50, "error.county.maxLength")
      )
    ),
    "postcode"       -> nonEmptyTextOr("correspondenceAddress.postcode", postcode, "error.postcode.required")
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

  def turnoverSalesMapping(field: String): Mapping[Option[BigDecimal]] = optional(
    text
      .transform[BigDecimal](s => Try(BigDecimal(s)).getOrElse(-1), _.toString)
      .verifying(between(zeroBigDecimal, salesMax, s"error.$field.range"))
  ).verifying(s"error.$field.required", _.nonEmpty)

}

object EnumMapping {
  def apply[T <: NamedEnum](named: NamedEnumSupport[T], defaultErrorMessageKey: String = Errors.noValueSelected) =
    Forms.of[T](new Formatter[T] {

      override val format = Some((defaultErrorMessageKey, Nil))

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
        val resOpt = for {
          keyVal        <- data.get(key)
          enumTypeValue <- named.fromName(keyVal)
        } yield Right(enumTypeValue)
        resOpt.getOrElse(Left(Seq(FormError(key, defaultErrorMessageKey, Nil))))
      }

      def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
    })
}
