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

sealed trait CurrentRentFixed extends NamedEnum {
  val key = "outsideRepairs"
}
object CurrentRentFixedNewLeaseAgreement extends CurrentRentFixed {
  val name = "newLeaseAgreement"
}
object CurrentRentFixedInterimRent extends CurrentRentFixed {
  val name = "interimRent"
}
object CurrentRentFixedRentReview extends CurrentRentFixed {
  val name = "rentReview"
}
object CurrentRentFixedRenewalLeaseTenancy extends CurrentRentFixed {
  val name = "renewalLeaseTenancy"
}
object CurrentRentFixedSaleLeaseback extends CurrentRentFixed {
  val name = "saleLeaseback"
}

object CurrentRentFix extends NamedEnumSupport[CurrentRentFixed] {
  val all = List(
    CurrentRentFixedNewLeaseAgreement,
    CurrentRentFixedInterimRent,
    CurrentRentFixedRentReview,
    CurrentRentFixedRenewalLeaseTenancy,
    CurrentRentFixedSaleLeaseback)
}

