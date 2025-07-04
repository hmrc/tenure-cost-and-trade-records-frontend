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

package form.aboutYourLeaseOrTenure

import form.MappingSupport.outsideRepairsType
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsibleOutsideRepairs
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

object UltimatelyResponsibleOutsideRepairsForm:

  val ultimatelyResponsibleOutsideRepairsForm: Form[UltimatelyResponsibleOutsideRepairs] = Form(
    mapping(
      "outsideRepairs"           -> outsideRepairsType,
      "sharedResponsibilitiesOR" -> mandatoryIfEqual(
        "outsideRepairs",
        OutsideRepairsBoth.toString,
        default(text, "").verifying(
          nonEmpty(errorMessage = "error.sharedResponsibilitiesOR.required"),
          maxLength(500, "error.sharedResponsibilitiesOR.maxLength")
        )
      )
    )(UltimatelyResponsibleOutsideRepairs.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
