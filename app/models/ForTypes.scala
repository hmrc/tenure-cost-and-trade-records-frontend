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

package models

object ForTypes {

  val for6010 = "FOR6010"
  val for6011 = "FOR6011"
  val for6015 = "FOR6015"
  val for6016 = "FOR6016"
  val for6020 = "FOR6020"
  val for6030 = "FOR6030"
  val for6045 = "FOR6045"
  val for6046 = "FOR6046"
  val for6048 = "FOR6048"
  val for6076 = "FOR6076"

  def find(forType: String) = forTypes.get(forType.toUpperCase)

  val forTypes = Map(
    "FOR6010" -> "Public Houses",
    "FOR6011" -> "Pubs, licensed restaurants and wine bars",
    "FOR6015" -> "Hotels",
    "FOR6016" -> "Hotels and guest houses",
    "FOR6020" -> "Petrol filling stations (occupier)",
    "FOR6030" -> "Petrol filling stations (trade)",
    "FOR6045" -> "Caravan sites – receipts",
    "FOR6046" -> "Caravan sites – rental",
    "FOR6048" -> "Self catering holiday homes",
    "FOR6076" -> "Renewable Energy"
  )
}
