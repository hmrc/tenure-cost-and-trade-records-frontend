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

package models.submissions.aboutfranchisesorlettings

import models.submissions.common.AnswersYesNo
import play.api.libs.json._

import java.time.LocalDate

sealed trait LettingPartOfProperty {
  def typeOfLetting: TypeOfLetting
  def rentalDetails: Option[RentDetails]
  def addAnotherLetting: Option[AnswersYesNo]
}

object LettingPartOfProperty {

  implicit val lettingReads: Reads[LettingPartOfProperty] = (__ \ "type").read[String].flatMap {
    case "ATMLetting"              => implicitly[Reads[ATMLetting]].map(identity)
    case "TelecomMastLetting"      => implicitly[Reads[TelecomMastLetting]].map(identity)
    case "AdvertisingRightLetting" => implicitly[Reads[AdvertisingRightLetting]].map(identity)
    case "OtherLetting"            => implicitly[Reads[OtherLetting]].map(identity)
    case other                     => Reads(_ => JsError(s"Unknown type: $other"))
  }

  implicit val lettingWrites: Writes[LettingPartOfProperty] = Writes {
    case atmLetting: ATMLetting                           =>
      Json.obj("type" -> "ATMLetting") ++ Json.toJson(atmLetting)(using ATMLetting.format).as[JsObject]
    case telecomMastLetting: TelecomMastLetting           =>
      Json.obj("type" -> "TelecomMastLetting") ++ Json
        .toJson(telecomMastLetting)(using TelecomMastLetting.format)
        .as[JsObject]
    case advertisingRightLetting: AdvertisingRightLetting =>
      Json.obj("type" -> "AdvertisingRightLetting") ++ Json
        .toJson(advertisingRightLetting)(using AdvertisingRightLetting.format)
        .as[JsObject]
    case otherLetting: OtherLetting                       =>
      Json.obj("type" -> "OtherLetting") ++ Json.toJson(otherLetting)(using OtherLetting.format).as[JsObject]
  }

  implicit val lettingFormat: Format[LettingPartOfProperty] = Format(lettingReads, lettingWrites)

}

case class RentDetails(
  annualRent: BigDecimal,
  sumFixedDate: LocalDate
)
object RentDetails {
  implicit val format: Format[RentDetails] = Json.format
}

case class ATMLetting(
  bankOrCompany: Option[String],
  correspondenceAddress: Option[LettingAddress],
  rentalDetails: Option[RentDetails] = None,
  override val addAnotherLetting: Option[AnswersYesNo] = None
) extends LettingPartOfProperty {
  override def typeOfLetting: TypeOfLetting = TypeOfLettingAutomatedTellerMachine
}

object ATMLetting {
  implicit val format: Format[ATMLetting] = Json.format
}

case class TelecomMastLetting(
  operatingCompanyName: Option[String],
  siteOfMast: Option[String],
  correspondenceAddress: Option[LettingAddress],
  rentalDetails: Option[RentDetails] = None,
  override val addAnotherLetting: Option[AnswersYesNo] = None
) extends LettingPartOfProperty {
  override def typeOfLetting: TypeOfLetting = TypeOfLettingTelecomMast
}

object TelecomMastLetting {
  implicit val format: Format[TelecomMastLetting] = Json.format
}

case class AdvertisingRightLetting(
  descriptionOfSpace: Option[String],
  advertisingCompanyName: Option[String],
  correspondenceAddress: Option[LettingAddress],
  rentalDetails: Option[RentDetails] = None,
  override val addAnotherLetting: Option[AnswersYesNo] = None
) extends LettingPartOfProperty {
  override def typeOfLetting: TypeOfLetting = TypeOfLettingAdvertisingRight
}

object AdvertisingRightLetting {
  implicit val format: Format[AdvertisingRightLetting] = Json.format
}

case class OtherLetting(
  lettingType: Option[String],
  tenantName: Option[String],
  correspondenceAddress: Option[LettingAddress],
  rentalDetails: Option[RentDetails] = None,
  override val addAnotherLetting: Option[AnswersYesNo] = None
) extends LettingPartOfProperty {
  override def typeOfLetting: TypeOfLetting = TypeOfLettingOther
}

object OtherLetting {
  implicit val format: Format[OtherLetting] = Json.format
}
