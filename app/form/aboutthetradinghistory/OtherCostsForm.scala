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

import models.submissions.aboutthetradinghistory.{OtherCost, OtherCosts}
import form.MappingSupport.otherCostValueMapping
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import java.time.LocalDate

object OtherCostsForm {

  val otherCostDetailsRequired: Constraint[OtherCosts] = Constraint("constraints.otherCostDetailsRequired") {
    otherCosts =>
      if (!otherCosts.otherCostDetails.isDefined && otherCosts.otherCosts.exists(_.otherCosts.exists(_ > 0))) {
        Invalid(Seq(ValidationError("error.otherCostDetails.required")))
      } else {
        Valid
      }
  }

  val otherCostMapping: Mapping[OtherCost] = mapping(
    "financialYearEnd"          -> ignored(LocalDate.EPOCH),
    "contributionsToHeadOffice" -> otherCostValueMapping("otherCosts.contributionsToHeadOffice"),
    "otherCosts"                -> otherCostValueMapping("otherCosts.otherCosts")
  )(OtherCost.apply)(OtherCost.unapply)

  val otherCostsMapping: Mapping[OtherCosts] = mapping(
    "otherCosts"       -> seq(otherCostMapping),
    "otherCostDetails" -> optional(text(maxLength = 2000))
  )(OtherCosts.apply)(OtherCosts.unapply)

  val form: Form[OtherCosts] = Form(
    otherCostsMapping.verifying(otherCostDetailsRequired)
  )

}
