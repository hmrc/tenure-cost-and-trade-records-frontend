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

package models.submissions.aboutYourLeaseOrTenure

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait CurrentRentWithin12Months extends NamedEnum {
  override def key: String = "rentPayable"
}
object CurrentRentWithin12MonthsYes extends CurrentRentWithin12Months {
  override def name: String = "yes"
}
object CurrentRentWithin12MonthsNo extends CurrentRentWithin12Months {
  override def name: String = "no"
}

object CurrentRentWithin12Months extends NamedEnumSupport[CurrentRentWithin12Months] {
  implicit val format: Format[CurrentRentWithin12Months] = EnumFormat(CurrentRentWithin12Months)

  val all: Seq[CurrentRentWithin12Months] = List(CurrentRentWithin12MonthsYes, CurrentRentWithin12MonthsNo)

  val key: String = all.head.key

}
