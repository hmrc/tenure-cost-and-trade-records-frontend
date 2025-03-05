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

package models.submissions.aboutfranchisesorlettings

import models.submissions.common.AnswersYesNo
import play.api.libs.json.{JsError, JsObject, JsResult, JsValue, Json, OFormat}

sealed trait IncomeRecord {
  def sourceType: TypeOfIncome

  def addAnotherRecord: Option[AnswersYesNo]

}

object IncomeRecord {
  implicit val format: OFormat[IncomeRecord] = {
    val franchiseFormat  = Json.format[FranchiseIncomeRecord]
    val concessionFormat = Json.format[ConcessionIncomeRecord]
    val lettingFormat    = Json.format[LettingIncomeRecord]

    new OFormat[IncomeRecord] {

      def reads(json: JsValue): JsResult[IncomeRecord] =
        (json \ "sourceType").validate[String].flatMap {
          case "typeFranchise"  => franchiseFormat.reads(json)
          case "typeConcession" => concessionFormat.reads(json)
          case "typeLetting"    => lettingFormat.reads(json)
          case other            => JsError(s"Unknown type: $other")
        }

      def writes(record: IncomeRecord): JsObject =
        record match {
          case franchise: FranchiseIncomeRecord   => franchiseFormat.writes(franchise)
          case concession: ConcessionIncomeRecord => concessionFormat.writes(concession)
          case letting: LettingIncomeRecord       => lettingFormat.writes(letting)
        }
    }
  }
}

case class FranchiseIncomeRecord(
  sourceType: TypeOfIncome = TypeFranchise,
  businessDetails: Option[CateringOperationDetails] = None,
  rent: Option[LettingOtherPartOfPropertyRentDetails] = None,
  itemsIncluded: Option[List[String]] = None,
  addAnotherRecord: Option[AnswersYesNo] = None
) extends IncomeRecord

object FranchiseIncomeRecord {
  implicit val format: OFormat[FranchiseIncomeRecord] = Json.format
}

case class ConcessionIncomeRecord(
  sourceType: TypeOfIncome = TypeConcession,
  businessDetails: Option[CateringOperationBusinessDetails] = None,
  feeReceived: Option[FeeReceived] = None,
  addAnotherRecord: Option[AnswersYesNo] = None
) extends IncomeRecord

object ConcessionIncomeRecord {
  implicit val format: OFormat[ConcessionIncomeRecord] = Json.format
}

case class LettingIncomeRecord(
  sourceType: TypeOfIncome = TypeLetting,
  operatorDetails: Option[LettingOtherPartOfPropertyInformationDetails] = None,
  rent: Option[LettingOtherPartOfPropertyRentDetails] = None,
  itemsIncluded: Option[List[String]] = None,
  addAnotherRecord: Option[AnswersYesNo] = None
) extends IncomeRecord

object LettingIncomeRecord {
  implicit val format: OFormat[LettingIncomeRecord] = Json.format
}
