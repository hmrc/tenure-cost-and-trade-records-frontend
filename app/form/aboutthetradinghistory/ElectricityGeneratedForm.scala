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

import form.MappingSupport._
import models.submissions.aboutthetradinghistory.TurnoverSection6076
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate
import scala.util.Try

/**
  * 6076 Electricity generated form.
  *
  * @author Yuriy Tumakha
  */
object ElectricityGeneratedForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[TurnoverSection6076] = mapping(
    "weeks"                -> default(text, "")
      .verifying(messages("error.weeksMapping.blank", year), _.trim.nonEmpty)
      .transform[Int](
        str => Try(str.toInt).getOrElse(-1),
        _.toString
      )
      .verifying(messages("error.weeksMapping.invalid", year), (0 to 52).contains(_)),
    "electricityGenerated" -> optional(text)
      .verifying(messages("error.turnover.6076.electricityGenerated.required", year), _.exists(_.trim.nonEmpty))
  )((weeks, electricityGenerated) =>
    TurnoverSection6076(LocalDate.EPOCH, weeks, electricityGenerated = electricityGenerated)
  )(turnover => Some(turnover.tradingPeriod, turnover.electricityGenerated))

  def electricityGeneratedForm(years: Seq[String])(implicit messages: Messages): Form[Seq[TurnoverSection6076]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"turnover[$idx]" -> columnMapping(year)
    }

    Form(
      mappingPerYear match {
        case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
        case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
        case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
      }
    )
  }

}
