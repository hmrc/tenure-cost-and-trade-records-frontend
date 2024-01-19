/*
 * Copyright 2023 HM Revenue & Customs
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

package form

import form.MappingSupport.createYesNoType
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

object confirmableActionForm {

  val confirmableActionMapping = mapping(
    "genericRemoveConfirmation" -> optional(
      createYesNoType("error.confirmableAction.required")
    )
      .verifying("error.confirmableAction.required", _.nonEmpty)
      .transform[AnswersYesNo](_.get, Some(_))
  )(x => x)(b => Some(b))

  val confirmableActionForm = Form(confirmableActionMapping)

}
