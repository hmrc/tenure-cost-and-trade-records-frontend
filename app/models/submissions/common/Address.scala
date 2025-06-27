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

package models.submissions.common

import models.submissions.PrintableAddress
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.json.{Reads, JsPath}
import play.api.libs.functional.syntax._

case class Address(
  buildingNameNumber: String,
  street1: Option[String],
  town: String,
  county: Option[String],
  postcode: String
) extends PrintableAddress

object Address:

  // NOTE that the reason for having custom Reads/Writes is to ensure that the `town` property gets
  // deserialized from `street2` in the JSON, as per the original structure of the Address model.

  // NOTE that this workaround would not be needed if the **backend** Address model were properly updated
  // to use `town` instead of `street2`
  // (see https://github.com/hmrc/tenure-cost-and-trade-records/blob/main/app/uk/gov/hmrc/tctr/backend/schema/Address.scala#L21)

  given Reads[Address] = (
    (JsPath \ "buildingNameNumber").read[String] and
      (JsPath \ "street1").readNullable[String] and
      (JsPath \ "street2").read[String] and
      (JsPath \ "county").readNullable[String] and
      (JsPath \ "postcode").read[String]
    )(Address.apply _)

  given Writes[Address] = (
    (JsPath \ "buildingNameNumber").write[String] and
      (JsPath \ "street1").writeNullable[String] and
      (JsPath \ "street2").write[String] and
      (JsPath \ "county").writeNullable[String] and
      (JsPath \ "postcode").write[String]
  )(address =>
    (address.buildingNameNumber, address.street1, address.town, address.county, address.postcode)
  )
