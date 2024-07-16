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
import models.submissions.aboutthetradinghistory.OperationalExpenses
import play.api.data.Forms._
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object OperationalExpensesForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[OperationalExpenses] =
    mapping(
      "advertising"    -> turnoverSalesMappingWithYear("turnover.6076.expenses.advertising", year),
      "administration" -> turnoverSalesMappingWithYear("turnover.6076.expenses.administration", year),
      "insurance"      -> turnoverSalesMappingWithYear("turnover.6076.expenses.insurance", year),
      "legalFees"      -> turnoverSalesMappingWithYear("turnover.6076.expenses.legalFees", year),
      "interest"       -> turnoverSalesMappingWithYear("turnover.6076.expenses.interest", year),
      "other"          -> turnoverSalesMappingWithYear("turnover.6076.expenses.other", year)
    )(OperationalExpenses.apply)(o => Some(Tuple.fromProductTyped(o)))

  private def operationalExpensesSeq(years: Seq[String])(implicit
    messages: Messages
  ): Mapping[Seq[OperationalExpenses]] =
    mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))

  def operationalExpensesForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[(Seq[OperationalExpenses], String)] =
    Form {
      tuple(
        "operationalExpensesSeq" -> operationalExpensesSeq(years),
        "otherExpensesDetails"   -> mandatoryStringIfNonZeroSum(
          ".other",
          "error.turnover.6076.otherExpensesDetails.required"
        ).verifying(
          maxLength(2000, "error.turnover.6076.otherExpensesDetails.maxLength")
        )
      )
    }

}
