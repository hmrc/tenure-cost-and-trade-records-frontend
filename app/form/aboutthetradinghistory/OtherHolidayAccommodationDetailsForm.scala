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

package form.aboutthetradinghistory

import form.MappingSupport.{createYesNoType, weeksInYearMapping}
import models.submissions.aboutthetradinghistory.OtherHolidayAccommodationDetails
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

object OtherHolidayAccommodationDetailsForm {
  def otherHolidayAccommodationDetailsForm(implicit messages: Messages): Form[OtherHolidayAccommodationDetails] = Form(
    mapping(
      "otherHolidayAccommodationOpenAllYear" -> createYesNoType("error.otherHolidayAccommodationOpenAllYear.required"),
      "weeksOpen"                            -> mandatoryIfEqual("otherHolidayAccommodationOpenAllYear", "no", weeksInYearMapping)
    )(OtherHolidayAccommodationDetails.apply)(OtherHolidayAccommodationDetails.unapply)
  )
}
