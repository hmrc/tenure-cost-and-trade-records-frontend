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

package models.submissions.lettingHistory

import play.api.libs.json.{Format, Json}

case class OccupierDetail(
  name: String,
  address: Option[OccupierAddress],
  rentalPeriod: Option[LocalPeriod]
):
  override def equals(that: Any): Boolean = that match {
    case OccupierDetail(name, address, None)               =>
      this.name.equalsIgnoreCase(name) && this.address == address && this.rentalPeriod.isEmpty
    case OccupierDetail(name, address, Some(rentalPeriod)) =>
      this.name.equalsIgnoreCase(
        name
      ) && this.address == address && this.rentalPeriod.isDefined && this.rentalPeriod.get.overlapsWith(rentalPeriod)
    case _                                                 => false
  }

object OccupierDetail:
  def unapply(obj: OccupierDetail): Option[(String, Option[OccupierAddress], Option[LocalPeriod])] =
    Some(obj.name, obj.address, obj.rentalPeriod)
  given Format[OccupierDetail]                                                                     = Json.format
