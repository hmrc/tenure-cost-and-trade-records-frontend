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

import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.TotalSiteCapacity
import play.api.data.Forms.mapping
import play.api.data.Form

object TotalSiteCapacityForm {

  val totalSiteCapacityForm: Form[TotalSiteCapacity] = Form {
    mapping(
      "availableForLetting"     -> nonNegativeNumber("availableForLetting"),
      "occupiedBySeasonalStuff" -> nonNegativeNumber("occupiedBySeasonalStuff"),
      "privatelyOwned"          -> nonNegativeNumber("privatelyOwned")
    )(TotalSiteCapacity.apply)(o => Some(Tuple.fromProductTyped(o)))
  }

}
