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

package form.aboutthetradinghistory

import form.MappingSupport.nonNegativeNumber
import models.submissions.aboutthetradinghistory.{CaravansAge, CaravansPerAgeCategory}
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}

/**
  * @author Yuriy Tumakha
  */
object CaravansAgeCategoriesForm {

  val caravansAgeCategoriesForm: Form[CaravansAge] =
    Form {
      mapping(
        "fleetHire"     -> caravansPerAgeCategoryMapping("fleetHire"),
        "privateSublet" -> caravansPerAgeCategoryMapping("privateSublet")
      )(CaravansAge.apply)(CaravansAge.unapply)
    }

  private def caravansPerAgeCategoryMapping(hireType: String): Mapping[CaravansPerAgeCategory] =
    mapping(
      "years0_5"    -> nonNegativeNumber(s"$hireType.years0_5"),
      "years6_10"   -> nonNegativeNumber(s"$hireType.years6_10"),
      "years11_15"  -> nonNegativeNumber(s"$hireType.years11_15"),
      "years15plus" -> nonNegativeNumber(s"$hireType.years15plus")
    )(CaravansPerAgeCategory.apply)(CaravansPerAgeCategory.unapply)

}
