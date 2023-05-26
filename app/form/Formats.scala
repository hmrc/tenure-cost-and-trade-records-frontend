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
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.{AnswerResponsibleParty, AnswersYesNo, CYAYesNo}
import models.submissions.connectiontoproperty.{AddressConnectionType, ConnectionToProperty}
import models.submissions.notconnected.PastConnectionType
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

    override val format: Option[(String, Nil.type)] = Some((Errors.required, Nil))

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

  implicit val userTypeFormat: Formatter[UserType]        = namedEnumFormatter(UserType, Errors.userTypeRequired)
  implicit val answerYesNoFormat: Formatter[AnswersYesNo] =
    namedEnumFormatter(AnswersYesNo, Errors.booleanMissing)
  implicit val cyaYesNoFormat: Formatter[CYAYesNo]        =
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
    namedEnumFormatter(CurrentPropertyUsed, Errors.currentOccupierName)
  implicit val buildingOperatingHaveAWebsiteFormat: Formatter[BuildingOperationHaveAWebsite] =
    namedEnumFormatter(BuildingOperationHaveAWebsite, Errors.booleanMissing)

  implicit val methodToFixCurrentRentDetailsFormat: Formatter[MethodToFixCurrentRents] =
    namedEnumFormatter(MethodToFixCurrentRents, Errors.booleanMissing)
  implicit val tiedForGoodsDetailsFormat: Formatter[TiedForGoodsInformation]           =
    namedEnumFormatter(TiedForGoodsInformation, Errors.booleanMissing)

  implicit val answerResponsiblePartyFormatter: Formatter[AnswerResponsibleParty] =
    namedEnumFormatter(AnswerResponsibleParty, Errors.booleanMissing)

  implicit val includeLicenseeFormatter: Formatter[IncludeLicensees]            =
    namedEnumFormatter(IncludeLicensee, Errors.booleanMissing)
  implicit val includeOtherPropertyFormatter: Formatter[IncludeOtherProperties] =
    namedEnumFormatter(IncludeOtherProperty, Errors.booleanMissing)
  implicit val onlyPartOfPropertyFormatter: Formatter[OnlyPartOfProperties]     =
    namedEnumFormatter(OnlyPartOfProperty, Errors.booleanMissing)
  implicit val onlyToLandFormatter: Formatter[OnlyToLands]                      = namedEnumFormatter(OnlyToLand, Errors.booleanMissing)
  implicit val shellUnitFormatter: Formatter[ShellUnits]                        = namedEnumFormatter(ShellUnit, Errors.booleanMissing)

  implicit val howIsCurrentRentFixedFormatter: Formatter[CurrentRentFixed] =
    namedEnumFormatter(CurrentRentFixed, Errors.booleanMissing)

  implicit val currentRentPayableWithin12MonthsFormatter: Formatter[CurrentRentWithin12Months] =
    namedEnumFormatter(CurrentRentWithin12Months, Errors.booleanMissing)

  implicit val whatIsYourRentBasedOnFormatter: Formatter[CurrentRentBasedOn] =
    namedEnumFormatter(CurrentRentBasedOn, Errors.booleanMissing)

}
