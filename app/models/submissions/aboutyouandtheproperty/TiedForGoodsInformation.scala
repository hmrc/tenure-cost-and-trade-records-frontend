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

package models.submissions.aboutyouandtheproperty

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait TiedForGoodsInformation extends NamedEnum {
  override def key = "tiedForGoodsDetails"
}
object TiedForGoodsInformationDetailsFullTie extends TiedForGoodsInformation {
  override def name = "fullTie"
}
object TiedForGoodsInformationDetailsBeerOnly extends TiedForGoodsInformation {
  override def name = "beerOnly"
}
object TiedForGoodsInformationDetailsPartialTie extends TiedForGoodsInformation {
  override def name = "partialTie"
}

object TiedForGoodsInformation extends NamedEnumSupport[TiedForGoodsInformation] {
  implicit val format: Format[TiedForGoodsInformation] = EnumFormat(TiedForGoodsInformation)

  val all = List(
    TiedForGoodsInformationDetailsFullTie,
    TiedForGoodsInformationDetailsBeerOnly,
    TiedForGoodsInformationDetailsPartialTie
  )

  val key = all.head.key
}
