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

package models.submissions.common

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait OutsideRepairs extends NamedEnum {
  val key: String = "outsideRepairs"
}
object OutsideRepairsLandlord extends OutsideRepairs {
  override def name: String = "landlord"
}
object OutsideRepairsTenant extends OutsideRepairs {
  override def name: String = "tenant"
}
object OutsideRepairsBoth extends OutsideRepairs {
  override def name: String = "both"
}

object OutsideRepairs extends NamedEnumSupport[OutsideRepairs] {

  implicit val format: Format[OutsideRepairs] = EnumFormat(
    OutsideRepairs
  )

  val all = List(OutsideRepairsLandlord, OutsideRepairsTenant, OutsideRepairsBoth)

  val key = all.head.key
}

sealed trait InsideRepairs extends NamedEnum {
  val key: String = "outsideRepairs"
}
object InsideRepairsLandlord extends InsideRepairs {
  override def name: String = "landlord"
}
object InsideRepairsTenant extends InsideRepairs {
  override def name: String = "tenant"
}
object InsideRepairsBoth extends InsideRepairs {
  override def name: String = "both"
}

object InsideRepairs extends NamedEnumSupport[InsideRepairs] {

  implicit val format: Format[InsideRepairs] = EnumFormat(
    InsideRepairs
  )

  val all = List(InsideRepairsLandlord, InsideRepairsTenant, InsideRepairsBoth)

  val key = all.head.key
}

sealed trait BuildingInsurance extends NamedEnum {
  val key: String = "buildingInsurance"
}
object BuildingInsuranceLandlord extends BuildingInsurance {
  override def name: String = "landlord"
}
object BuildingInsuranceTenant extends BuildingInsurance {
  override def name: String = "tenant"
}
object BuildingInsuranceBoth extends BuildingInsurance {
  override def name: String = "both"
}

object BuildingInsurance extends NamedEnumSupport[BuildingInsurance] {

  implicit val format: Format[BuildingInsurance] = EnumFormat(
    BuildingInsurance
  )

  val all = List(BuildingInsuranceLandlord, BuildingInsuranceTenant, BuildingInsuranceBoth)

  val key = all.head.key
}
