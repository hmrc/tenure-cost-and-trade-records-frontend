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

sealed trait OutsideRepairs extends NamedEnum {
  override def key: String = "outsideRepairs"
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
  implicit val format: Format[OutsideRepairs] = EnumFormat(OutsideRepairs)

  val all = List(OutsideRepairsLandlord, OutsideRepairsTenant, OutsideRepairsBoth)

  val key = all.head.key
}

sealed trait InsideRepairs extends NamedEnum {
  override def key: String = "insideRepairs"
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
  implicit val format: Format[InsideRepairs] = EnumFormat(InsideRepairs)

  val all = List(InsideRepairsLandlord, InsideRepairsTenant, InsideRepairsBoth)

  val key = all.head.key
}

sealed trait BuildingInsurances extends NamedEnum {
  override def key: String = "buildingInsurance"
}
object BuildingInsurancesLandlord extends BuildingInsurances {
  override def name: String = "landlord"
}
object BuildingInsurancesTenant extends BuildingInsurances {
  override def name: String = "tenant"
}
object BuildingInsurancesBoth extends BuildingInsurances {
  override def name: String = "both"
}

object BuildingInsurances extends NamedEnumSupport[BuildingInsurances] {
  implicit val format: Format[BuildingInsurances] = EnumFormat(BuildingInsurances)

  val all = List(BuildingInsurancesLandlord, BuildingInsurancesTenant, BuildingInsurancesBoth)

  val key = all.head.key
}
