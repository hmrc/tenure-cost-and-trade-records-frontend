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

import form.ConditionalConstraintMappings.mandatoryStringIfNonZeroSum
import form.MappingSupport._
import play.api.data.Forms._
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages
import util.NumberUtil.zeroBigDecimal

/**
  * @author Yuriy Tumakha
  */
object OtherIncomeForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[BigDecimal] = single(
    "otherIncome" -> turnoverSalesMappingWithYear("turnover.6076.otherIncome", year)
      .transform[BigDecimal](_.getOrElse(zeroBigDecimal), Option(_))
  )

  private def otherIncomeSeq(years: Seq[String])(implicit messages: Messages): Mapping[Seq[BigDecimal]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"turnover[$idx]" -> columnMapping(year)
    }

    mappingPerYear match {
      case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
      case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
      case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
    }
  }

  def otherIncomeForm(years: Seq[String])(implicit messages: Messages): Form[(Seq[BigDecimal], String)] =
    Form {
      tuple(
        "otherIncomeSeq"     -> otherIncomeSeq(years),
        "otherIncomeDetails" -> mandatoryStringIfNonZeroSum(
          ".otherIncome",
          "error.turnover.6076.otherIncomeDetails.required"
        ).verifying(
          maxLength(2000, "error.turnover.6076.otherIncomeDetails.maxLength")
        )
      )
    }

}
