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

sealed trait TenantsAdditionsDisregarded extends NamedEnum {
  val key = "tenantsAdditionsDisregarded"
}
object TenantsAdditionsDisregardedYes extends TenantsAdditionsDisregarded {
  val name = "yes"
}
object TenantsAdditionsDisregardedNo extends TenantsAdditionsDisregarded {
  val name = "no"
}

object TenantsAdditionsDisregard extends NamedEnumSupport[TenantsAdditionsDisregarded] {
  val all = List(TenantsAdditionsDisregardedYes, TenantsAdditionsDisregardedNo)
}

sealed trait LegalPlanningRestrictions extends NamedEnum {
  val key = "legalPlanningRestrictions"
}
object LegalPlanningRestrictionsYes extends LegalPlanningRestrictions {
  val name = "yes"
}
object LegalPlanningRestrictionsNo extends LegalPlanningRestrictions {
  val name = "no"
}

object LegalPlanningRestriction extends NamedEnumSupport[LegalPlanningRestrictions] {
  val all = List(LegalPlanningRestrictionsYes, LegalPlanningRestrictionsNo)
}
