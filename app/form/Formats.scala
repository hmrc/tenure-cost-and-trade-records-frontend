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

import models._
import models.submissions.Form6010._
import models.submissions._
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings.{TypeOfIncome, TypeOfLetting}
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.{AnswersYesNo, BuildingInsurance, CYAYesNo, InsideRepairs, OutsideRepairs}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty, VacantPropertiesDetails}
import models.submissions.notconnected.PastConnectionType
import play.api.data.FormError
import play.api.data.format.Formatter

object Formats {

  def namedEnumFormatter[T <: NamedEnum](named: NamedEnumSupport[T], missingCode: String): Formatter[T] =
    new Formatter[T] {

      override val format = Some((missingCode, Nil))

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
        val resOpt = for
          keyVal        <- data.get(key)
          enumTypeValue <- named.fromName(keyVal)
        yield Right(enumTypeValue)
        resOpt.getOrElse(Left(Seq(FormError(key, missingCode, Nil))))
      }

      def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
    }

  def namedEnumFormatterWithKeys[T <: NamedEnum](
    named: NamedEnumSupport[T],
    missingCodes: Map[String, String]
  ): Formatter[T] = new Formatter[T] {

    override val format: Option[(String, Nil.type)] = Some((Errors.required, Nil))

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      val resOpt      = for
        keyVal        <- data.get(key)
        enumTypeValue <- named.fromName(keyVal)
      yield Right(enumTypeValue)
      val maybeString = missingCodes.get(key)
      resOpt.getOrElse(Left(Seq(FormError(key, maybeString.getOrElse(Errors.required), Nil))))
    }

    def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
  }
  def answersYesNoDefFormat(errorMessage: String): Formatter[AnswersYesNo]                              =
    namedEnumFormatter(AnswersYesNo, errorMessage)
  implicit val userTypeFormat: Formatter[UserType]                                                      = namedEnumFormatter(UserType, Errors.userTypeRequired)
  implicit val cyaYesNoFormat: Formatter[CYAYesNo]                                                      =
    namedEnumFormatter(CYAYesNo, Errors.booleanMissing)

  implicit val addressConnectionTypeFormatter: Formatter[AddressConnectionType]              =
    namedEnumFormatter(AddressConnectionType, Errors.isConnectedError)
  implicit val connectionToPropertyFormat: Formatter[ConnectionToProperty]                   =
    namedEnumFormatter(ConnectionToProperty, Errors.connectionToPropertyError)
  // Not connected
  implicit val pastConnectionFormat: Formatter[PastConnectionType]                           =
    namedEnumFormatter(PastConnectionType, Errors.isPastConnected)
  // About the property
  implicit val aboutYourPropertyFormat: Formatter[CurrentPropertyUsed]                       =
    namedEnumFormatter(CurrentPropertyUsed, Errors.propertyCurrentlyUsed)
  implicit val buildingOperatingHaveAWebsiteFormat: Formatter[BuildingOperationHaveAWebsite] =
    namedEnumFormatter(BuildingOperationHaveAWebsite, Errors.buildingOperatingHaveAWebsite)
  implicit val vacantPropertiesFormat: Formatter[VacantPropertiesDetails]                    =
    namedEnumFormatter(VacantPropertiesDetails, Errors.vacantProperties)

  implicit val methodToFixCurrentRentDetailsFormat: Formatter[MethodToFixCurrentRents] =
    namedEnumFormatter(MethodToFixCurrentRents, Errors.methodToFixCurrentRents)

  implicit val tiedForGoodsDetailsFormat: Formatter[TiedForGoodsInformation] =
    namedEnumFormatter(TiedForGoodsInformation, Errors.tiedForGoodsDetails)

  implicit val renewablesPlantFormat: Formatter[RenewablesPlantDetails] =
    namedEnumFormatter(RenewablesPlantDetails, Errors.renewablesPlant)

  implicit val outsideRepairsFormatter: Formatter[OutsideRepairs]       =
    namedEnumFormatter(OutsideRepairs, Errors.outsideRepairs)
  implicit val insideRepairsFormatter: Formatter[InsideRepairs]         =
    namedEnumFormatter(InsideRepairs, Errors.insideRepairs)
  implicit val buildingInsuranceFormatter: Formatter[BuildingInsurance] =
    namedEnumFormatter(BuildingInsurance, Errors.buildingInsurance)

  implicit val includeLicenseeFormatter: Formatter[IncludeLicensees]            =
    namedEnumFormatter(IncludeLicensee, Errors.booleanMissing)
  implicit val includeOtherPropertyFormatter: Formatter[IncludeOtherProperties] =
    namedEnumFormatter(IncludeOtherProperty, Errors.booleanMissing)
  implicit val onlyPartOfPropertyFormatter: Formatter[OnlyPartOfProperties]     =
    namedEnumFormatter(OnlyPartOfProperty, Errors.booleanMissing)
  implicit val onlyToLandFormatter: Formatter[OnlyToLands]                      = namedEnumFormatter(OnlyToLand, Errors.booleanMissing)
  implicit val shellUnitFormatter: Formatter[ShellUnits]                        = namedEnumFormatter(ShellUnit, Errors.booleanMissing)

  implicit val howIsCurrentRentFixedFormatter: Formatter[CurrentRentFixed] =
    namedEnumFormatter(CurrentRentFixed, Errors.howIsCurrentRentFixed)

  implicit val currentRentPayableWithin12MonthsFormatter: Formatter[CurrentRentWithin12Months] =
    namedEnumFormatter(CurrentRentWithin12Months, Errors.currentRentPayableWithin12Months)

  implicit val tenancy3YearsFormatter: Formatter[TenancyThreeYears] =
    namedEnumFormatter(TenancyThreeYears, Errors.tenancy3Years)
  implicit val rent3YearsFormatter: Formatter[RentThreeYears]       =
    namedEnumFormatter(RentThreeYears, Errors.rent3Years)
  implicit val underReviewFormatter: Formatter[UnderReview]         =
    namedEnumFormatter(UnderReview, Errors.underReview)

  implicit val whatIsYourRentBasedOnFormatter: Formatter[CurrentRentBasedOn] =
    namedEnumFormatter(CurrentRentBasedOn, Errors.currentRentBasedOn)

  implicit val includedInYourRentFormatter: Formatter[IncludedInYourRentInformation] =
    namedEnumFormatter(IncludedInYourRentInformation, Errors.currentRentBasedOn)

  // About Franchises or Lettings
  implicit val typeOfLettingFormat: Formatter[TypeOfLetting] =
    namedEnumFormatter(TypeOfLetting, Errors.typeOfLetting)

  implicit val typeOfIncomeFormat: Formatter[TypeOfIncome] =
    namedEnumFormatter(TypeOfIncome, Errors.typeOfIncome)
}
