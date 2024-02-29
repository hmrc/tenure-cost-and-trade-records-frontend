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

package models.submissions.connectiontoproperty

import models.submissions.MaxOfLettings
import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

case class LettingPartOfPropertyDetails(
  tenantDetails: TenantDetails,
  lettingPartOfPropertyRentDetails: Option[LettingPartOfPropertyRentDetails] = None,
  itemsIncludedInRent: List[String] = List.empty,
  addAnotherLettingToProperty: Option[AnswersYesNo] = None,
  maxOfLettings: Option[MaxOfLettings] = None
)

object LettingPartOfPropertyDetails {
  implicit val format: OFormat[LettingPartOfPropertyDetails] = Json.format[LettingPartOfPropertyDetails]
}
