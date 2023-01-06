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

package models.submissions.abouttheproperty

import play.api.libs.json.Json

//TODO - this does not seem to have been set up to store the Partial tie text field, nor is there seemingly any validation to ensure the user has to set this.

case class TiedForGoodsInformationDetails(tiedGoodsDetails: TiedForGoodsInformation)

object TiedForGoodsInformationDetails {
  implicit val format = Json.format[TiedForGoodsInformationDetails]

}
