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

import form.MappingSupport.mappingPerYear
import models.submissions.aboutyouandtheproperty.{CompletedLettings, LettingAvailability}
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages
import controllers.toOpt
import java.time.LocalDate
import scala.util.Try

object CompletedCommercialLettingsWelshForm {

  def completedCommercialLettingsWelshForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[CompletedLettings]] =
    Form {
      mappingPerYear(years, (year, idx) => "" -> completedMapping(year, idx))
    }

  private def completedMapping(year: String, idx: Int)(implicit
    messages: Messages
  ): Mapping[CompletedLettings] =
    mapping(
      "financial-year-end"      -> ignored(LocalDate.EPOCH),
      s"completedLettings-$idx" -> optional(text)
        .verifying(
          messages("error.completedCommercialLettings.welsh.required", year),
          _.nonEmpty
        )
        .verifying(
          messages("error.completedCommercialLettings.welsh.range", year),
          _.forall(s => Try(s.toInt).isSuccess && s.toInt >= 0 && s.toInt <= 365)
        )
        .transform[Int](
          _.getOrElse("0").toInt,
          _.toString
        )
    )(CompletedLettings.apply)(o => Some(Tuple.fromProductTyped(o)))

}
