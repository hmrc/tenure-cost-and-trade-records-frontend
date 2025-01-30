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

package models.submissions.accommodation

import models.Scala3EnumFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum AccommodationTariffItem(item: String):
  override def toString: String = item

  case BedLinen extends AccommodationTariffItem("bedLinen")
  case Electricity extends AccommodationTariffItem("electricity")
  case Gas extends AccommodationTariffItem("gas")
  case Water extends AccommodationTariffItem("water")
  case RoomHeating extends AccommodationTariffItem("roomHeating")
  case None extends AccommodationTariffItem("none")
end AccommodationTariffItem

object AccommodationTariffItem:
  implicit val format: Format[AccommodationTariffItem] = Scala3EnumFormat.format

  val stringValues: Seq[String] = AccommodationTariffItem.values.toSeq.map(_.toString)

  def fromName(name: String): Option[AccommodationTariffItem] =
    AccommodationTariffItem.values.find(_.toString == name)
