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

sealed trait TypeOfLetting extends NamedEnum {
  val key = "typeOfLetting"
}

object TypeOfLettingAutomatedTellerMachine extends TypeOfLetting {
  val name = "automatedTellerMachine"
}

object TypeOfLettingTelecomMast extends TypeOfLetting {
  val name = "telecomMast"
}

object TypeOfLettingAdvertisingRight extends TypeOfLetting {
  val name = "advertisingRight"
}

object TypeOfLettingOther extends TypeOfLetting {
  val name = "other"
}

object TypeOfLetting extends NamedEnumSupport[TypeOfLetting] {
  implicit val format: Format[TypeOfLetting] = EnumFormat(TypeOfLetting)

  override def all: Seq[TypeOfLetting] = List(
    TypeOfLettingAutomatedTellerMachine,
    TypeOfLettingTelecomMast,
    TypeOfLettingAdvertisingRight,
    TypeOfLettingOther
  )

  val key = all.head.key
}
