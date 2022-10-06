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

sealed trait RentIncludeFixturesAndFittings extends NamedEnum{
  val key = "rentIncludeFixturesAndFittings"
}
object RentIncludeFixturesAndFittingsYes extends RentIncludeFixturesAndFittings {
  val name = "yes"
}
object RentIncludeFixturesAndFittingsNo extends RentIncludeFixturesAndFittings {
  val name = "no"
}

object RentIncludeFixturesAndFitting extends NamedEnumSupport[RentIncludeFixturesAndFittings] {
  val all = List(RentIncludeFixturesAndFittingsYes,RentIncludeFixturesAndFittingsNo)
}