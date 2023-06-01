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
import models.submissions.common.{Address, AnswerResponsibleParty, AnswersYesNo, CYAYesNo, ContactDetails, ContactDetailsAddress}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty}
import models.submissions.notconnected.PastConnectionType
import models.{AnnualRent, NamedEnum, NamedEnumSupport}
import play.api.data.Forms.{boolean, default, email, list, mapping, nonEmptyText, optional, text}
import play.api.data.format.Formatter
import play.api.data.validation.Constraints.{maxLength, minLength, nonEmpty, pattern}
import play.api.data.{FormError, Forms, Mapping}

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
  val yesNoType: Mapping[AnswersYesNo]                                          = Forms.of[AnswersYesNo]
  val cyaYesNo: Mapping[CYAYesNo]                                               = Forms.of[CYAYesNo]
  val addressConnectionType: Mapping[AddressConnectionType]                     = Forms.of[AddressConnectionType]
  val pastConnectionType: Mapping[PastConnectionType]                           = Forms.of[PastConnectionType]
  val methodToFixCurrentRentsType: Mapping[MethodToFixCurrentRents]             = Forms.of[MethodToFixCurrentRents]

  val responsiblePartyType: Mapping[AnswerResponsibleParty] = Forms.of[AnswerResponsibleParty]

  val includeLicenseeType: Mapping[IncludeLicensees]            = Forms.of[IncludeLicensees]
  val includeOtherPropertyType: Mapping[IncludeOtherProperties] = Forms.of[IncludeOtherProperties]
  val onlyPartOfPropertyType: Mapping[OnlyPartOfProperties]     = Forms.of[OnlyPartOfProperties]
  val onlyToLandType: Mapping[OnlyToLands]                      = Forms.of[OnlyToLands]
  val shellUnitType: Mapping[ShellUnits]                        = Forms.of[ShellUnits]

  val howIsCurrentRentFixedType: Mapping[CurrentRentFixed] = Forms.of[CurrentRentFixed]

  val whatIsYourRentBasedOnType: Mapping[CurrentRentBasedOn] = Forms.of[CurrentRentBasedOn]

  val currentRentPayableWithin12MonthsType: Mapping[CurrentRentWithin12Months] = Forms.of[CurrentRentWithin12Months]

  val tiedForGoodsDetailsType: Mapping[TiedForGoodsInformation] = Forms.of[TiedForGoodsInformation]
  val postcode: Mapping[String]                                 = PostcodeMapping.postcode()

  val decimalRegex          = """^[0-9]{1,10}\.?[0-9]{0,2}$"""
  val cdbMaxCurrencyAmount  = 9999999.99
  val spacesIntRegex: Regex = """^\-?\d{1,10}$""".r
  val intRegex: Regex       = """^\d{1,3}$""".r

  val multipleCurrentPropertyUsedMapping: Mapping[List[CurrentPropertyUsed]] = {
    list(nonEmptyText).verifying("Invalid property used", strs => strs.forall(str => CurrentPropertyUsed.withName(str).isDefined))
      .transform[List[CurrentPropertyUsed]](
        strs => strs.flatMap(str => CurrentPropertyUsed.withName(str)),
        currentPropertyUseds => currentPropertyUseds.map(_.name)
      )
  }
  lazy val annualRent: Mapping[AnnualRent] = mapping(
    "annualRentExcludingVat" -> currencyMapping(".annualRentExcludingVat")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  lazy val rentIncludeFixturesAndFittingsDetails: Mapping[AnnualRent] = mapping(
    "rentIncludeFixturesAndFittingsDetails" -> currencyMapping(".rentIncludeFixturesAndFittingsDetails")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  lazy val rentIncludeTradeServiceDetails: Mapping[AnnualRent] = mapping(
    "sumIncludedInRent" -> currencyMapping(".sumIncludedInRent")
  )(AnnualRent.apply)(AnnualRent.unapply).verifying(Errors.maxCurrencyAmountExceeded, _.amount <= cdbMaxCurrencyAmount)

  val currency: Mapping[BigDecimal] = currencyMapping()

  def currencyMapping(fieldErrorPart: String = ""): Mapping[BigDecimal] = default(text, "")
    .verifying(nonEmpty(errorMessage = Errors.required + fieldErrorPart))
    .verifying(
      Errors.invalidCurrency + fieldErrorPart,
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
    "street1"            -> optional(text(maxLength = 50)),
    "street2"            -> optional(text(maxLength = 50)),
    "postcode"           -> nonEmptyTextOr("postcode", postcode, "error.postcode.required")
  )(Address.apply)(Address.unapply)

  def landlordAddressMapping: Mapping[LandlordAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(text(maxLength = 50)),
    "town"               -> optional(text(maxLength = 50)),
    "county"             -> optional(text(maxLength = 50)),
    "postcode"           -> nonEmptyTextOr("landlordAddress.postcode", postcode, "error.postcode.required")
  )(LandlordAddress.apply)(LandlordAddress.unapply)

  def cateringAddressMapping: Mapping[CateringAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(text(maxLength = 50)),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(text(maxLength = 50)),
    "postcode"           -> nonEmptyTextOr("cateringAddress.postcode", postcode, "error.postcode.required")
  )(CateringAddress.apply)(CateringAddress.unapply)

  def lettingOtherPartAddressMapping: Mapping[LettingAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(text(maxLength = 50)),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.townCity.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "county"             -> optional(text(maxLength = 50)),
    "postcode"           -> nonEmptyTextOr("lettingAddress.postcode", postcode, "error.postcode.required")
  )(LettingAddress.apply)(LettingAddress.unapply)

  def contactAddressMapping: Mapping[ContactDetailsAddress] = mapping(
    "buildingNameNumber" -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.buildingNameNumber.required"),
      maxLength(50, "error.buildingNameNumber.maxLength")
    ),
    "street1"            -> optional(text(maxLength = 50)),
    "town"               -> default(text, "").verifying(
      nonEmpty(errorMessage = "error.town.required"),
      maxLength(50, "error.town.maxLength")
    ),
    "county"             -> optional(text(maxLength = 50)),
    "postcode"           -> nonEmptyTextOr("alternativeContactAddress.postcode", postcode, "error.postcode.required")
  )(ContactDetailsAddress.apply)(ContactDetailsAddress.unapply)

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
