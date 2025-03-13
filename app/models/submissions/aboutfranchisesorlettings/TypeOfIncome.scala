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

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait TypeOfIncome extends NamedEnum {
  override def key: String = "typeOfIncome"
}

object TypeFranchise extends TypeOfIncome {
  override def name: String = "typeFranchise"
}

object TypeConcession6015 extends TypeOfIncome {
  override def name: String = "typeConcession6015"
}

object TypeConcession extends TypeOfIncome {
  override def name: String = "typeConcession"
}

object TypeLetting extends TypeOfIncome {
  override def name: String = "typeLetting"
}

object TypeOfIncome extends NamedEnumSupport[TypeOfIncome] {
  implicit val format: Format[TypeOfIncome] = EnumFormat(TypeOfIncome)

  override def all: Seq[TypeOfIncome] = List(
    TypeFranchise,
    TypeConcession6015,
    TypeConcession,
    TypeLetting
  )

  val key: String = all.head.key
}
