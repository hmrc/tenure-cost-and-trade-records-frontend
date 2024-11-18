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
import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.{OperationalCosts6048, OperationalExpenses}
import play.api.data.Forms.{mapping, tuple}
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

/**
  * @author Yuriy Tumakha
  */
object OperationalCosts6048Form {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[OperationalCosts6048] =
    mapping(
      "energyBills"                -> turnoverSalesMappingWithYear("turnover.6048.operationalCosts.energyBills", year),
      "laundryCleaning"            -> turnoverSalesMappingWithYear("turnover.6048.operationalCosts.laundryCleaning", year),
      "repairsRenewalsMaintenance" -> turnoverSalesMappingWithYear(
        "turnover.6048.operationalCosts.repairsRenewalsMaintenance",
        year
      ),
      "tvLicences"                 -> turnoverSalesMappingWithYear("turnover.6048.operationalCosts.tvLicences", year),
      "travellingAndMotorExpenses" -> turnoverSalesMappingWithYear(
        "turnover.6048.operationalCosts.travellingAndMotorExpenses",
        year
      ),
      "other"                      -> turnoverSalesMappingWithYear("turnover.6048.operationalCosts.other", year)
    )(OperationalCosts6048.apply)(o => Some(Tuple.fromProductTyped(o)))

  private def operationalCostsSeq(years: Seq[String])(implicit
    messages: Messages
  ): Mapping[Seq[OperationalCosts6048]] =
    mappingPerYear(years, (year, idx) => s"turnover[$idx]" -> columnMapping(year))

  def operationalCosts6048Form(
    years: Seq[String]
  )(implicit messages: Messages): Form[(Seq[OperationalCosts6048], String)] =
    Form {
      tuple(
        "operationalCostsSeq"  -> operationalCostsSeq(years),
        "otherExpensesDetails" -> mandatoryStringIfNonZeroSum(
          ".other",
          "error.turnover.6048.otherExpensesDetails.required"
        ).verifying(
          maxLength(2000, "error.turnover.6048.otherExpensesDetails.maxLength")
        )
      )
    }

}
