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
import models.submissions.aboutthetradinghistory.Caravans.CaravanHireType
import models.submissions.aboutthetradinghistory.Caravans.CaravanHireType.{FleetHire, PrivateSublet}
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType
import models.submissions.aboutthetradinghistory.{CaravansAge, CaravansPerAgeCategory}
import play.api.data.Form
import play.api.data.Forms.mapping

/**
  * @author Yuriy Tumakha
  */
object CaravansAgeCategoriesForm {

  def caravansAgeCategoriesForm(caravanUnitType: CaravanUnitType): Form[CaravansAge] =
    Form {
      mapping(
        caravansPerAgeCategoryMapping(caravanUnitType, FleetHire),
        caravansPerAgeCategoryMapping(caravanUnitType, PrivateSublet)
      )(CaravansAge.apply)(o => Some(Tuple.fromProductTyped(o)))
    }

  private def caravansPerAgeCategoryMapping(unitType: CaravanUnitType, hireType: CaravanHireType) = {

    def caravansAmountMapping(ageCategory: String) =
      ageCategory -> nonNegativeNumber(s"caravans.$unitType.$hireType.$ageCategory")

    hireType.toString -> mapping(
      caravansAmountMapping("years0_5"),
      caravansAmountMapping("years6_10"),
      caravansAmountMapping("years11_15"),
      caravansAmountMapping("years15plus")
    )(CaravansPerAgeCategory.apply)(o => Some(Tuple.fromProductTyped(o)))
  }

}
