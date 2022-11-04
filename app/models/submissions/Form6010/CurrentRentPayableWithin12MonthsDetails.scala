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

package models.submissions.Form6010

import models.{NamedEnum, NamedEnumSupport}

sealed trait CurrentRentWithin12Months extends NamedEnum {
  val key = "rentPayable"
}
object CurrentRentWithin12MonthsYes extends CurrentRentWithin12Months {
  val name = "yes"
}
object CurrentRentWithin12MonthsNo extends CurrentRentWithin12Months {
  val name = "no"
}

object CurrentRentWithin12Month extends NamedEnumSupport[CurrentRentWithin12Months] {
  val all = List(CurrentRentWithin12MonthsYes, CurrentRentWithin12MonthsNo)
}
