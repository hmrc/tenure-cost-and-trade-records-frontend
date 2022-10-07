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

sealed trait RentIncludeTradeServices extends NamedEnum{
  val key = "rentIncludeTradeServices"
}
object RentIncludeTradeServicesYes extends RentIncludeTradeServices {
  val name = "yes"
}
object RentIncludeTradeServicesNo extends RentIncludeTradeServices {
  val name = "no"
}

object RentIncludeTradeService extends NamedEnumSupport[RentIncludeTradeServices] {
  val all = List(RentIncludeTradeServicesYes,RentIncludeTradeServicesNo)
}
