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

package form.aboutthetradinghistory

import form.MappingSupport._
import play.api.data.Forms.{default, text, tuple}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import scala.util.Try

/**
  * 6076 Electricity generated form.
  *
  * @author Yuriy Tumakha
  */
object ElectricityGeneratedForm {

  private def weeksMapping(year: String, minWeeks: Int = 0)(using messages: Messages): Mapping[Option[Int]] =
    text
      .transform[Option[Int]](
        str => {
          val weeks = str.trim
          if (weeks.isEmpty) None
          else Try(Some(weeks.toInt)).getOrElse(None)
        },
        optInt => optInt.map(_.toString).getOrElse("")
      )
      .verifying(messages("error.weeksMapping.blank", year), optInt => optInt.isDefined)
      .verifying(
        messages("error.weeksMapping.invalid", year),
        optInt => optInt.forall(weeks => (minWeeks to 52).contains(weeks))
      )

  private def columnMapping(year: String)(using messages: Messages): Mapping[(Option[Int], String)] = tuple(
    "weeks"                -> weeksMapping(year),
    "electricityGenerated" -> default(text, "")
      .verifying(messages("error.turnover.6076.electricityGenerated.required", year), _.trim.nonEmpty)
  )

  def electricityGeneratedForm(years: Seq[String])(using messages: Messages): Form[Seq[(Option[Int], String)]] =
    Form {
      mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))
    }

}
