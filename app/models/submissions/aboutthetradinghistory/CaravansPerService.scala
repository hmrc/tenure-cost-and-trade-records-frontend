/*
 * Copyright 2024 HM Revenue & Customs
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

package models.submissions.aboutthetradinghistory

import play.api.libs.json.{Json, OFormat}

/**
  * Number of caravans per provided service.
  *
  * @author Yuriy Tumakha
  */
case class CaravansPerService(
  fleetWaterElectricityDrainage: Int = 0,
  fleetElectricityOnly: Int = 0,
  privateWaterElectricityDrainage: Int = 0,
  privateElectricityOnly: Int = 0
)

object CaravansPerService {
  implicit val format: OFormat[CaravansPerService] = Json.format
}
