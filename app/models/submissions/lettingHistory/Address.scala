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

case class Address(
  line1: String,
  line2: Option[String],
  town: String,
  county: Option[String],
  postcode: String
):
  override def equals(that: Any): Boolean = that match {
    case Address(line1, line2, town, county, postcode) =>
      this.line1.equalsIgnoreCase(line1) && this.line2.equalsIgnoreCase(line2) && this.town.equalsIgnoreCase(
        town
      ) && this.county.equalsIgnoreCase(county) && this.postcode.equalsIgnoreCase(postcode)
    case _                                             => false
  }

  extension (opt: Option[String])
    def equalsIgnoreCase(that: Option[String]): Boolean = (opt, that) match {
      case (Some(a), Some(b)) => a.equalsIgnoreCase(b)
      case (None, None)       => true
      case _                  => false
    }

object Address:
  def unapply(obj: Address): Option[(String, Option[String], String, Option[String], String)] =
    Some(obj.line1, obj.line2, obj.town, obj.county, obj.postcode)
  given Format[Address]                                                                       = Json.format
