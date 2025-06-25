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

package models.submissions.connectiontoproperty

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum ConnectionToProperty(connectionType: String):
  override def toString: String = connectionType

  case ConnectionToThePropertyOccupierTrustee extends ConnectionToProperty("occupierTrustee")
  case ConnectionToThePropertyOwnerTrustee extends ConnectionToProperty("ownerTrustee")
  case ConnectionToThePropertyOccupierAgent extends ConnectionToProperty("occupierAgent")
  case ConnectionToThePropertyOwnerAgent extends ConnectionToProperty("ownerAgent")
end ConnectionToProperty

object ConnectionToProperty:
  implicit val format: Format[ConnectionToProperty] = Scala3EnumJsonFormat.format
