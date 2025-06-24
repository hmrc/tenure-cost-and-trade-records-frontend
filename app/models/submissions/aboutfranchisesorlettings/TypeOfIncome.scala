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

package models.submissions.aboutfranchisesorlettings

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum TypeOfIncome(typeOfIncome: String):
  override def toString: String = typeOfIncome

  case TypeFranchise extends TypeOfIncome("typeFranchise")
  case TypeConcession6015 extends TypeOfIncome("typeConcession6015")
  case TypeConcession extends TypeOfIncome("typeConcession")
  case TypeLetting extends TypeOfIncome("typeLetting")
end TypeOfIncome

object TypeOfIncome:
  implicit val format: Format[TypeOfIncome] = Scala3EnumJsonFormat.format
