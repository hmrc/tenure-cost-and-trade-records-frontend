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

sealed trait MethodToFixCurrentRents extends NamedEnum {
  override def key: String = "methodUsedToFixCurrentRent"
}
object MethodToFixCurrentRentsAgreement extends MethodToFixCurrentRents {
  override def name: String = "agreement"
}
object MethodToFixCurrentRentsArbitration extends MethodToFixCurrentRents {
  override def name: String = "arbitration"
}

object MethodToFixCurrentRentIndependentExpert extends MethodToFixCurrentRents {
  override def name: String = "independentExpert"
}
object MethodToFixCurrentRentsACourt extends MethodToFixCurrentRents {
  override def name: String = "aCourt"
}

object MethodToFixCurrentRents extends NamedEnumSupport[MethodToFixCurrentRents] {
  implicit val format: Format[MethodToFixCurrentRents] = EnumFormat(MethodToFixCurrentRents)

  val all = List(
    MethodToFixCurrentRentsAgreement,
    MethodToFixCurrentRentsArbitration,
    MethodToFixCurrentRentIndependentExpert,
    MethodToFixCurrentRentsACourt
  )
  val key = all.head.key
}
