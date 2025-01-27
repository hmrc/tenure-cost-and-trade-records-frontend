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

package form.accommodation

import form.DateMappings.requiredDateMapping
import models.submissions.accommodation.HighSeasonTariff
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object HighSeasonTariff6048Form:

  def highSeasonTariff6048Form(implicit messages: Messages): Form[HighSeasonTariff] =
    Form {
      mapping(
        "fromDate" -> requiredDateMapping("accommodation.highSeasonTariff.fromDate", allowPastDates = true),
        "toDate"   -> requiredDateMapping("accommodation.highSeasonTariff.toDate", allowPastDates = true)
      )(HighSeasonTariff.apply)(o => Some(Tuple.fromProductTyped(o)))
    }
