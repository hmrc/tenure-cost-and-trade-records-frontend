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

sealed trait FranchiseOrLettingsTiedToProperty extends NamedEnum {
  override def key: String = "franchiseOrLettingsTiedToProperty"
}
object FranchiseOrLettingsTiedToPropertiesYes extends FranchiseOrLettingsTiedToProperty {
  override def name: String = "yes"
}
object FranchiseOrLettingsTiedToPropertiesNo extends FranchiseOrLettingsTiedToProperty {
  override def name: String = "no"
}

object FranchiseOrLettingsTiedToProperty extends NamedEnumSupport[FranchiseOrLettingsTiedToProperty] {
  implicit val format: Format[FranchiseOrLettingsTiedToProperty] = EnumFormat(FranchiseOrLettingsTiedToProperty)

  val all = List(FranchiseOrLettingsTiedToPropertiesYes, FranchiseOrLettingsTiedToPropertiesNo)

  val key = all.head.key
}
