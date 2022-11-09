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


import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.{Format, Json}

sealed trait AddressConnectionType extends NamedEnum {
  override def key: String = "addressConnectionType"
}

object AddressConnectionTypeYes extends AddressConnectionType {
  override def name: String = "yes"
}

object AddressConnectionTypeNo extends AddressConnectionType {
  override def name: String = "no"
}

object AddressConnectionTypeYesChangeAddress extends AddressConnectionType {
  override def name: String = "yes-change-address"
}

object AddressConnectionType extends NamedEnumSupport[AddressConnectionType] {
  implicit val format: Format[AddressConnectionType] = EnumFormat(AddressConnectionType)

  override def all = List(AddressConnectionTypeYes, AddressConnectionTypeNo, AddressConnectionTypeYesChangeAddress)

  val key = all.head.key


}