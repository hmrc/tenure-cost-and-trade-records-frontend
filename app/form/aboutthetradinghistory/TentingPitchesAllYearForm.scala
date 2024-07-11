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

import form.MappingSupport.{createYesNoType, weeksMapping}
import models.submissions.aboutthetradinghistory.TentingPitchesAllYear
import models.submissions.common.AnswerNo
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

object TentingPitchesAllYearForm {

  val tentingPitchesAllYearForm: Form[TentingPitchesAllYear] = Form(
    mapping(
      "tentingPitchesAllYear" -> createYesNoType("error.areYourPitchesOpen.missing"),
      "weekOfPitchesUse"      -> mandatoryIfEqual(
        "tentingPitchesAllYear",
        AnswerNo.name,
        weeksMapping(
          "error.areYourPitchesOpen.conditional.value.missing",
          "error.areYourPitchesOpen.conditional.value.invalid"
        )
      )
    )(TentingPitchesAllYear.apply)(TentingPitchesAllYear.unapply)
  )
}
