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

package models

import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum ForType:
  case FOR6010, // Public Houses
    FOR6011, // Pubs, licensed restaurants and wine bars
    FOR6015, // Hotels
    FOR6016, // Hotels and guest houses
    FOR6020, // Petrol filling stations (occupier)
    FOR6030, // Petrol filling stations (trade)
    FOR6045, // Caravan sites – receipts
    FOR6046, // Caravan sites – rental
    FOR6048, // Self-catering holiday homes
    FOR6076 // Renewable Energy
end ForType

object ForType:
  implicit val format: Format[ForType] = Scala3EnumJsonFormat.format

  def find(forType: String): Option[ForType] = values.find(_.toString == forType.toUpperCase)
