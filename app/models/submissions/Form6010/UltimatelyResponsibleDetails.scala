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

import models.{NamedEnum, NamedEnumSupport}

sealed trait OutsideRepairs extends NamedEnum {
  val key = "outsideRepairs"
}
object OutsideRepairsLandlord extends OutsideRepairs {
  val name = "landlord"
}
object OutsideRepairsTenant extends OutsideRepairs {
  val name = "tenant"
}
object OutsideRepairsBoth extends OutsideRepairs {
  val name = "both"
}

object OutsideRepair extends NamedEnumSupport[OutsideRepairs] {
  val all = List(OutsideRepairsLandlord, OutsideRepairsTenant, OutsideRepairsBoth)
}

sealed trait InsideRepairs extends NamedEnum {
  val key = "insideRepairs"
}
object InsideRepairsLandlord extends InsideRepairs {
  val name = "landlord"
}
object InsideRepairsTenant extends InsideRepairs {
  val name = "tenant"
}
object InsideRepairsBoth extends InsideRepairs {
  val name = "both"
}

object InsideRepair extends NamedEnumSupport[InsideRepairs] {
  val all = List(InsideRepairsLandlord, InsideRepairsTenant, InsideRepairsBoth)
}

sealed trait BuildingInsurances extends NamedEnum {
  val key = "buildingInsurance"
}
object BuildingInsurancesLandlord extends BuildingInsurances {
  val name = "landlord"
}
object BuildingInsurancesTenant extends BuildingInsurances {
  val name = "tenant"
}
object BuildingInsurancesBoth extends BuildingInsurances {
  val name = "both"
}

object BuildingInsurance extends NamedEnumSupport[BuildingInsurances] {
  val all = List(BuildingInsurancesLandlord, BuildingInsurancesTenant, BuildingInsurancesBoth)
}
