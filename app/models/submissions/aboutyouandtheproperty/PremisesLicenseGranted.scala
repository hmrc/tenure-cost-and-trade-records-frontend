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

package models.submissions.aboutyouandtheproperty

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait PremisesLicenseGranted extends NamedEnum {
  override def key = "premisesLicenseGranted"
}
object PremisesLicenseGrantedYes extends PremisesLicenseGranted {
  override def name = "yes"
}
object PremisesLicenseGrantedNo extends PremisesLicenseGranted {
  override def name = "no"
}

object PremisesLicenseGranted extends NamedEnumSupport[PremisesLicenseGranted] {

  implicit val format: Format[PremisesLicenseGranted] = EnumFormat(PremisesLicenseGranted)

  override def all = List(PremisesLicenseGrantedYes, PremisesLicenseGrantedNo)

  val key = all.head.key
}
