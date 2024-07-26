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

import form.MappingSupport._
import models.submissions.common.{AnswerNo, AnswersYesNo}
import play.api.data.Form
import play.api.data.Forms.tuple
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

/**
  * 6045/6046 Trading history - Are your static caravans open all year.
  *
  * @author Yuriy Tumakha
  */
object CaravansOpenAllYearForm {

  val caravansOpenAllYearForm: Form[(AnswersYesNo, Option[Int])] =
    Form(
      tuple(
        "areCaravansOpenAllYear"  -> createYesNoType("error.caravans.openAllYear.required"),
        "weeksPerYear" -> mandatoryIfEqual(
          "openAllYear",
          AnswerNo.name,
          weeksMapping(
            "error.caravans.weeksPerYear.required",
            "error.caravans.weeksPerYear.invalid"
          )
        )
      )
    )

}
