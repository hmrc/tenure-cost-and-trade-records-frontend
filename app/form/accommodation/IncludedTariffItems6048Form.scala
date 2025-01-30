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

package form.accommodation

import form.MappingSupport.{enumMappingSeq, nonEmptySeq, noneCantBeSelectedWithOtherSeq}
import models.submissions.accommodation.AccommodationTariffItem
import play.api.data.Form
import play.api.data.Forms.*

/**
  * @author Yuriy Tumakha
  */
object IncludedTariffItems6048Form:

  val includedTariffItems6048Form: Form[Seq[AccommodationTariffItem]] =
    Form {
      single(
        "includedTariffItems" -> enumMappingSeq(AccommodationTariffItem.fromName, _.toString)
          .verifying(
            nonEmptySeq("error.includedTariffItems.required"),
            noneCantBeSelectedWithOtherSeq(
              "none",
              "error.includedTariffItems.noneSelectedWithOther"
            )
          )
      )
    }
