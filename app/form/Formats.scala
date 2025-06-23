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

import models._
import models.submissions.Form6010._
import models.submissions.aboutYourLeaseOrTenure._
import models.submissions.aboutfranchisesorlettings.{TypeOfIncome, TypeOfLetting}
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.{BuildingInsurance, CYAYesNo, InsideRepairs, OutsideRepairs}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty, VacantPropertiesDetails}
import models.submissions.notconnected.PastConnectionType
import play.api.data.FormError
import play.api.data.format.Formatter

object Formats:

  // TODO: substitute by Scala3EnumFieldMapping.enumMappingRequired
  private def namedEnumFormatter[T <: NamedEnum](named: NamedEnumSupport[T], missingCode: String): Formatter[T] =
    new Formatter[T] {

      override val format: Option[(String, Seq[Any])] = Some((missingCode, Nil))

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
        val resOpt = for
          keyVal        <- data.get(key)
          enumTypeValue <- named.fromName(keyVal)
        yield Right(enumTypeValue)
        resOpt.getOrElse(Left(Seq(FormError(key, missingCode, Nil))))
      }

      def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
    }

  given Formatter[CYAYesNo]              = namedEnumFormatter(CYAYesNo, Errors.booleanMissing)
  given Formatter[AddressConnectionType] = namedEnumFormatter(AddressConnectionType, Errors.isConnectedError)
  given Formatter[ConnectionToProperty]  = namedEnumFormatter(ConnectionToProperty, Errors.connectionToPropertyError)

  // Not connected
  given Formatter[PastConnectionType]      = namedEnumFormatter(PastConnectionType, Errors.isPastConnected)
  // About the property
  given Formatter[CurrentPropertyUsed]     = namedEnumFormatter(CurrentPropertyUsed, Errors.propertyCurrentlyUsed)
  given Formatter[VacantPropertiesDetails] = namedEnumFormatter(VacantPropertiesDetails, Errors.vacantProperties)
  given Formatter[MethodToFixCurrentRents] = namedEnumFormatter(MethodToFixCurrentRents, Errors.methodToFixCurrentRents)
  given Formatter[TiedForGoodsInformation] = namedEnumFormatter(TiedForGoodsInformation, Errors.tiedForGoodsDetails)
  given Formatter[RenewablesPlantDetails]  = namedEnumFormatter(RenewablesPlantDetails, Errors.renewablesPlant)
  given Formatter[OutsideRepairs]          = namedEnumFormatter(OutsideRepairs, Errors.outsideRepairs)
  given Formatter[InsideRepairs]           = namedEnumFormatter(InsideRepairs, Errors.insideRepairs)
  given Formatter[BuildingInsurance]       = namedEnumFormatter(BuildingInsurance, Errors.buildingInsurance)
  given Formatter[IncludeLicensees]        = namedEnumFormatter(IncludeLicensee, Errors.booleanMissing)
  given Formatter[IncludeOtherProperties]  = namedEnumFormatter(IncludeOtherProperty, Errors.booleanMissing)
  given Formatter[OnlyPartOfProperties]    = namedEnumFormatter(OnlyPartOfProperty, Errors.booleanMissing)
  given Formatter[OnlyToLands]             = namedEnumFormatter(OnlyToLand, Errors.booleanMissing)
  given Formatter[ShellUnits]              = namedEnumFormatter(ShellUnit, Errors.booleanMissing)
  given Formatter[CurrentRentFixed]        = namedEnumFormatter(CurrentRentFixed, Errors.howIsCurrentRentFixed)
  given Formatter[TenancyThreeYears]       = namedEnumFormatter(TenancyThreeYears, Errors.tenancy3Years)
  given Formatter[RentThreeYears]          = namedEnumFormatter(RentThreeYears, Errors.rent3Years)
  given Formatter[UnderReview]             = namedEnumFormatter(UnderReview, Errors.underReview)
  given Formatter[CurrentRentBasedOn]      = namedEnumFormatter(CurrentRentBasedOn, Errors.currentRentBasedOn)

  given Formatter[BuildingOperationHaveAWebsite] =
    namedEnumFormatter(BuildingOperationHaveAWebsite, Errors.buildingOperatingHaveAWebsite)

  given Formatter[CurrentRentWithin12Months] =
    namedEnumFormatter(CurrentRentWithin12Months, Errors.currentRentPayableWithin12Months)

  given Formatter[IncludedInYourRentInformation] =
    namedEnumFormatter(IncludedInYourRentInformation, Errors.currentRentBasedOn)

  // About Franchises or Lettings
  given Formatter[TypeOfLetting] = namedEnumFormatter(TypeOfLetting, Errors.typeOfLetting)
  given Formatter[TypeOfIncome]  = namedEnumFormatter(TypeOfIncome, Errors.typeOfIncome)
