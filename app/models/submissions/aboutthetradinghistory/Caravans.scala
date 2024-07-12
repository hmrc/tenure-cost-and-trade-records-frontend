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

import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

/**
  * 6045/6046 Trading history - Static holiday or leisure caravans pages.
  *
  * @author Yuriy Tumakha
  */
case class Caravans(
  anyStaticLeisureCaravansOnSite: Option[AnswersYesNo] = None,
  openAllYear: Option[AnswersYesNo] = None,
  weeksPerYear: Option[Int] = None,
  singleCaravansAge: Option[CaravansAge] = None,
  twinUnitCaravansAge: Option[CaravansAge] = None
)

object Caravans {
  implicit val format: OFormat[Caravans] = Json.format
}
