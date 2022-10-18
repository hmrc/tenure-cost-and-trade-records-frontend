/*
 * Copyright 2022 HM Revenue & Customs
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

package models.submissions

import models.{NamedEnum, NamedEnumSupport}

sealed trait IncludeLicensees extends NamedEnum{
  val key = "includeLicensee"
}
object IncludeLicenseesYes extends IncludeLicensees {
  val name = "yes"
}
object IncludeLicenseesNo extends IncludeLicensees {
  val name = "no"
}

object IncludeLicensee extends NamedEnumSupport[IncludeLicensees] {
  val all = List(IncludeLicenseesYes,IncludeLicenseesNo)
}


sealed trait IncludeOtherProperties extends NamedEnum{
  val key = "includeOtherProperty"
}
object IncludeOtherPropertiesYes extends IncludeOtherProperties {
  val name = "yes"
}
object IncludeOtherPropertiesNo extends IncludeOtherProperties {
  val name = "no"
}

object IncludeOtherProperty extends NamedEnumSupport[IncludeOtherProperties] {
  val all = List(IncludeOtherPropertiesYes,IncludeOtherPropertiesNo)
}


sealed trait OnlyPartOfProperties extends NamedEnum{
  val key = "onlyPartOfProperty"
}
object OnlyPartOfPropertiesYes extends OnlyPartOfProperties {
  val name = "yes"
}
object OnlyPartOfPropertiesNo extends OnlyPartOfProperties {
  val name = "no"
}

object OnlyPartOfProperty extends NamedEnumSupport[OnlyPartOfProperties] {
  val all = List(OnlyPartOfPropertiesYes,OnlyPartOfPropertiesNo)
}


sealed trait OnlyToLands extends NamedEnum{
  val key = "onlyToLand"
}
object OnlyToLandsYes extends OnlyToLands {
  val name = "yes"
}
object OnlyToLandsNo extends OnlyToLands {
  val name = "no"
}

object OnlyToLand extends NamedEnumSupport[OnlyToLands] {
  val all = List(OnlyToLandsYes,OnlyToLandsNo)
}


sealed trait ShellUnits extends NamedEnum{
  val key = "shellUnit"
}
object ShellUnitsYes extends ShellUnits {
  val name = "yes"
}
object ShellUnitsNo extends ShellUnits {
  val name = "no"
}

object ShellUnit extends NamedEnumSupport[ShellUnits] {
  val all = List(ShellUnitsYes,ShellUnitsNo)
}