/*
 * Copyright 2022 HM Revenue & Customs
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

package models.submissions

import models.{NamedEnum, NamedEnumSupport}

sealed trait TiedForGoodsInformationDetail extends NamedEnum {
  val key = "tiedForGoodsDetails"
}
object TiedForGoodsInformationDetailsFullTie extends TiedForGoodsInformationDetail {
  val name = "fullTie"
}
object TiedForGoodsInformationDetailsBeerOnly extends TiedForGoodsInformationDetail {
  val name = "beerOnly"
}
object TiedForGoodsInformationDetailsPartialTie extends TiedForGoodsInformationDetail {
  val name = "partialTie"
}

object TiedForGoodsInformation extends NamedEnumSupport[TiedForGoodsInformationDetail] {
  val all = List(
    TiedForGoodsInformationDetailsFullTie,
    TiedForGoodsInformationDetailsBeerOnly,
    TiedForGoodsInformationDetailsPartialTie
  )
}
