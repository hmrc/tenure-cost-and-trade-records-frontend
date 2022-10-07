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

sealed trait MethodToFixCurrentRents extends NamedEnum{
  val key = "methodUsedToFixCurrentRent"
}
object MethodToFixCurrentRentsAgreement extends MethodToFixCurrentRents {
  val name = "agreement"
}
object MethodToFixCurrentRentsArbitration extends MethodToFixCurrentRents {
  val name = "arbitration"
}

object MethodToFixCurrentRentIndependentExpert extends MethodToFixCurrentRents {
  val name = "independentExpert"
}
object MethodToFixCurrentRentsACourt extends MethodToFixCurrentRents {
  val name = "aCourt"
}

object MethodToFixCurrentRent extends NamedEnumSupport[MethodToFixCurrentRents] {
  val all = List(MethodToFixCurrentRentsAgreement,MethodToFixCurrentRentsArbitration,MethodToFixCurrentRentIndependentExpert,MethodToFixCurrentRentsACourt)
}