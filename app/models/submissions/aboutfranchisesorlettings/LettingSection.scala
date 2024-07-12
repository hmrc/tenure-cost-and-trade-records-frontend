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

package models.submissions.aboutfranchisesorlettings

import models.submissions.common.AnswersYesNo
import play.api.libs.json.{Json, OFormat}

case class LettingSection(
  lettingOtherPartOfPropertyInformationDetails: LettingOtherPartOfPropertyInformationDetails,
  lettingOtherPartOfPropertyRentDetails: Option[LettingOtherPartOfPropertyRentDetails] = None,
  lettingOtherPartOfPropertyRent6015Details: Option[LettingOtherPartOfPropertyRent6015Details] = None,
  addAnotherLettingToProperty: Option[AnswersYesNo] = None,
  itemsInRent: List[String] = List.empty
)

object LettingSection {
  implicit val format: OFormat[LettingSection] = Json.format
}
