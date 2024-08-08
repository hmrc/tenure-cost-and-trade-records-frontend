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
import models.submissions.aboutthetradinghistory.CaravansTotalSiteCapacity
import play.api.data.Form
import play.api.data.Forms.mapping

/**
  * @author Yuriy Tumakha
  */
object CaravansTotalSiteCapacityForm {

  val caravansTotalSiteCapacityForm: Form[CaravansTotalSiteCapacity] =
    Form {
      mapping(
        "ownedByOperatorForFleetHire"     -> nonNegativeNumber("caravans.ownedByOperatorForFleetHire"),
        "privatelyOwnedForOwnerAndFamily" -> nonNegativeNumber("caravans.privatelyOwnedForOwnerAndFamily"),
        "subletByOperator"                -> nonNegativeNumber("caravans.subletByOperator"),
        "subletByPrivateOwners"           -> nonNegativeNumber("caravans.subletByPrivateOwners"),
        "charitablePurposes"              -> nonNegativeNumber("caravans.charitablePurposes"),
        "seasonalStaff"                   -> nonNegativeNumber("caravans.seasonalStaff")
      )(CaravansTotalSiteCapacity.apply)(o => Some(Tuple.fromProductTyped(o)))
    }

}
