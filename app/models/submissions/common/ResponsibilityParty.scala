/*
 * Copyright 2025 HM Revenue & Customs
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

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * Responsibility party - landlord, tenant, both.
  *
  * @author Yuriy Tumakha
  */
object ResponsibilityParty:

  // Outside Repairs

  enum OutsideRepairs(outsideRepairs: String):
    override def toString: String = outsideRepairs

    case OutsideRepairsLandlord extends OutsideRepairs("landlord")
    case OutsideRepairsTenant extends OutsideRepairs("tenant")
    case OutsideRepairsBoth extends OutsideRepairs("both")
  end OutsideRepairs

  object OutsideRepairs:
    implicit val format: Format[OutsideRepairs] = Scala3EnumJsonFormat.format

  // Inside Repairs

  enum InsideRepairs(insideRepairs: String):
    override def toString: String = insideRepairs

    case InsideRepairsLandlord extends InsideRepairs("landlord")
    case InsideRepairsTenant extends InsideRepairs("tenant")
    case InsideRepairsBoth extends InsideRepairs("both")
  end InsideRepairs

  object InsideRepairs:
    implicit val format: Format[InsideRepairs] = Scala3EnumJsonFormat.format

  // Building Insurance

  enum BuildingInsurance(buildingInsurance: String):
    override def toString: String = buildingInsurance

    case BuildingInsuranceLandlord extends BuildingInsurance("landlord")
    case BuildingInsuranceTenant extends BuildingInsurance("tenant")
    case BuildingInsuranceBoth extends BuildingInsurance("both")
  end BuildingInsurance

  object BuildingInsurance:
    implicit val format: Format[BuildingInsurance] = Scala3EnumJsonFormat.format
