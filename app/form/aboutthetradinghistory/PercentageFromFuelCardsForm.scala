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

import form.MappingSupport.EnrichedSeq
import models.submissions.aboutthetradinghistory.PercentageFromFuelCards
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

object PercentageFromFuelCardsForm {

  def percentageFromFuelCardsForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[Seq[PercentageFromFuelCards]] = {
    val mappingPerYear =
      years.take(3).zipWithIndex.map { case (year, idx) => "" -> percentageFromFuelCardsMapping(year, idx) }

    Form(
      mappingPerYear match {
        case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
        case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
        case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
      }
    )
  }

  private def percentageFromFuelCardsMapping(year: String, idx: Int)(implicit
    messages: Messages
  ): Mapping[PercentageFromFuelCards] =
    mapping(
      "financial-year-end"            -> ignored(LocalDate.EPOCH),
      s"percentageFromFuelCards-$idx" -> optional(
        text
          .verifying(messages("error.percentageFromFuelCards.range", year), s => Try(BigDecimal(s)).isSuccess)
          .transform[BigDecimal](
            s => BigDecimal(s),
            _.toString
          )
          .verifying(messages("error.percentageFromFuelCards.range", year), _ >= 0)
          .verifying(messages("error.percentage", year), _ <= 100)
      ).verifying(messages("error.percentageFromFuelCards.required", year), _.isDefined)
    )(PercentageFromFuelCards.apply)(PercentageFromFuelCards.unapply)
}
