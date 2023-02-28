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

package models.submissions.Form6010

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait VATs extends NamedEnum {
  override def key: String = "vat"
}
object VATsYes extends VATs {
  override def name: String = "yes"
}
object VATsNo extends VATs {
  override def name: String = "no"
}

object VATs extends NamedEnumSupport[VATs] {
  implicit val format: Format[VATs] = EnumFormat(VATs)

  val all = List(VATsYes, VATsNo)

  val key = all.head.key
}

sealed trait NonDomesticRates extends NamedEnum {
  override def key: String = "nondomesticRates"
}
object NonDomesticRatesYes extends NonDomesticRates {
  override def name: String = "yes"
}
object NonDomesticRatesNo extends NonDomesticRates {
  override def name: String = "no"
}

object NonDomesticRates extends NamedEnumSupport[NonDomesticRates] {
  implicit val format: Format[NonDomesticRates] = EnumFormat(NonDomesticRates)

  val all = List(NonDomesticRatesYes, NonDomesticRatesNo)

  val key = all.head.key
}

sealed trait WaterCharges extends NamedEnum {
  override def key: String = "waterCharges"
}
object WaterChargesYes extends WaterCharges {
  override def name: String = "yes"
}
object WaterChargesNo extends WaterCharges {
  override def name: String = "no"
}

object WaterCharges extends NamedEnumSupport[WaterCharges] {
  implicit val format: Format[WaterCharges] = EnumFormat(WaterCharges)

  val all = List(WaterChargesYes, WaterChargesNo)

  val key = all.head.key

}
