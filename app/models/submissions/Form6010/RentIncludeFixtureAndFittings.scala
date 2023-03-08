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

sealed trait RentIncludeFixturesAndFittings extends NamedEnum {
  override def key: String = "rentIncludeFixturesAndFittings"
}
object RentIncludeFixturesAndFittingsYes extends RentIncludeFixturesAndFittings {
  override def name: String = "yes"
}
object RentIncludeFixturesAndFittingsNo extends RentIncludeFixturesAndFittings {
  override def name: String = "no"
}

object RentIncludeFixturesAndFittings extends NamedEnumSupport[RentIncludeFixturesAndFittings] {
  implicit val format: Format[RentIncludeFixturesAndFittings] = EnumFormat(RentIncludeFixturesAndFittings)

  val all = List(RentIncludeFixturesAndFittingsYes, RentIncludeFixturesAndFittingsNo)

  val key = all.head.key
}
