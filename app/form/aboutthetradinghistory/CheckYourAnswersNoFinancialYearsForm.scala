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

package form.aboutthetradinghistory

import form.MappingSupport.createYesNoType
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}

object CheckYourAnswersNoFinancialYearsForm:

  val theForm: Form[AnswersYesNo] =
    Form(
      mapping(
        "correct"   ->
          optional(text)
            .transform[Boolean](_.contains("true"), b => Some(b.toString))
            .verifying("error.checkYourAnswers.givenInformation.isCorrect", _ == true),
        "completed" -> createYesNoType("error.checkYourAnswersRadio.required")
      )((_, completed) => completed)(completed => Some((true, completed)))
    )
