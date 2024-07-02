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

import controllers.toOpt
import form.MappingSupport.createYesNoType
import models.submissions.aboutthetradinghistory.TentingPitchesAllYear
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{default, mapping, nonEmptyText, number, optional, text}
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.Try


object TentingPitchesAllYearForm {

  def validateWeeksDuration =
    default(text, "")
      .verifying(weeksDurationConstraint())
      .transform[Int](
        str => Try(str.toInt).getOrElse(-1),
        int => int.toString
      )

  private def weeksDurationConstraint(): Constraint[String] =
    Constraint("constraints.nonNegative")(text =>
      Try(text.toDouble).toOption match {
        case Some(num) if num >= 0 && num <= 52 => Valid
        case _ => Invalid(ValidationError("test"))
      }
    )

    val tentingPitchesAllYearForm = Form(
      mapping(
        "tentingPitchesAllYear" -> createYesNoType("error.electricVehicleChargingPoints.required"),
        "weekOfPitchesUse" -> mandatoryIfEqual(
          "tentingPitchesAllYear",
          AnswerYes.name,
          validateWeeksDuration
        )
      )(TentingPitchesAllYear.apply)(TentingPitchesAllYear.unapply)
    )
}
