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

package models.submissions.aboutyouandtheproperty

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait RenewablesPlantDetails extends NamedEnum {
  override def key: String = "renewablesPlantDetails"
}
object Intermittent extends RenewablesPlantDetails {
  override def name: String = "intermittent"
}
object BaseLoad extends RenewablesPlantDetails {
  override def name: String = "baseload"
}

object RenewablesPlantDetails extends NamedEnumSupport[RenewablesPlantDetails] {
  implicit val format: Format[RenewablesPlantDetails] = EnumFormat(RenewablesPlantDetails)

  val all: Seq[RenewablesPlantDetails] = List(
    Intermittent,
    BaseLoad
  )
  val key: String                      = all.head.key
}
