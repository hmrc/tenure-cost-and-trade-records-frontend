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

package form.aboutyouandtheproperty

import form.MappingSupport.createYesNoType
import models.submissions.aboutyouandtheproperty.TradingActivity
import models.submissions.common.AnswerYes
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.data.validation.Constraints.maxLength
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

object TradingActivityForm {

  val tradingActivityForm = Form(
    mapping(
      "tradingActivityQuestion" -> createYesNoType("error.tradingActivity.missing"),
      "tradingActivityDetails"  -> mandatoryIfEqual(
        "tradingActivityQuestion",
        AnswerYes.name,
        text
          .verifying("error.tradingActivity.details.missing", details => details.nonEmpty)
          .verifying(maxLength(500, "error.tradingActivity.maxLength"))
      )
    )(TradingActivity.apply)(TradingActivity.unapply)
  )
}
