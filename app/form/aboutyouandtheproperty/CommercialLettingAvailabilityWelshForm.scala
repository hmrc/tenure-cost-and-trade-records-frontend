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

import controllers.toOpt
import form.MappingSupport.mappingPerYear
import models.submissions.aboutyouandtheproperty.LettingAvailability
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

object CommercialLettingAvailabilityWelshForm {

  def commercialLettingAvailabilityWelshForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[LettingAvailability]] =
    Form {
      mappingPerYear(years, (year, idx) => "" -> lettingAvailAbilityMapping(year, idx))
    }

  private def lettingAvailAbilityMapping(year: String, idx: Int)(implicit
    messages: Messages
  ): Mapping[LettingAvailability] =
    mapping(
      "financial-year-end"        -> ignored(LocalDate.EPOCH),
      s"lettingAvailAbility-$idx" -> optional(text)
        .verifying(
          messages("error.commercialLettingAvailability.welsh.required", year),
          _.nonEmpty
        )
        .verifying(
          messages("error.commercialLettingAvailability.welsh.range", year),
          _.forall(s => Try(s.toInt).isSuccess && s.toInt >= 0 && s.toInt <= 365)
        )
        .transform[Int](
          _.getOrElse("0").toInt,
          _.toString
        )
    )(LettingAvailability.apply)(o => Some(Tuple.fromProductTyped(o)))

}
