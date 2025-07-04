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

package form.aboutyouandtheproperty

import form.Errors
import form.MappingSupport.createYesNoType
import form.WebsiteMapping.validateWebaddress
import models.submissions.aboutyouandtheproperty.WebsiteForPropertyDetails
import models.submissions.common.AnswersYesNo.AnswerYes
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

object WebsiteForPropertyForm:

  val websiteForPropertyForm: Form[WebsiteForPropertyDetails] = Form(
    mapping(
      "buildingOperatingHaveAWebsite" -> createYesNoType(Errors.buildingOperatingHaveAWebsite),
      "websiteAddressForProperty"     -> mandatoryIfEqual(
        "buildingOperatingHaveAWebsite",
        AnswerYes.toString,
        validateWebaddress
      )
    )(WebsiteForPropertyDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
