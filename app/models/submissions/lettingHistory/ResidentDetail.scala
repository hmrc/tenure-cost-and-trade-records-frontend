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

case class ResidentDetail(
  name: String,
  address: String
):
  override def equals(that: Any): Boolean = that match {
    case ResidentDetail(name, address) => this.name.equalsIgnoreCase(name) && this.address.equalsIgnoreCase(address)
    case _                             => false
  }

object ResidentDetail:
  def unapply(obj: ResidentDetail): Option[(String, String)] = Some(obj.name, obj.address)
  given Format[ResidentDetail]                               = Json.format
