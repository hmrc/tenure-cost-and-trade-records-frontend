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

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait BuildingOperationHaveAWebsite extends NamedEnum {
  val key = "buildingOperatingHaveAWebsite"
}
object BuildingOperationHaveAWebsiteYes extends BuildingOperationHaveAWebsite {
  val name = "yes"
}
object BuildingOperationHaveAWebsiteNo extends BuildingOperationHaveAWebsite {
  val name = "no"
}

object BuildingOperationHaveAWebsite extends NamedEnumSupport[BuildingOperationHaveAWebsite] {

  implicit val format: Format[BuildingOperationHaveAWebsite] = EnumFormat(BuildingOperationHaveAWebsite)

  val all = List(BuildingOperationHaveAWebsiteYes, BuildingOperationHaveAWebsiteNo)

  val key = all.head.key

}
