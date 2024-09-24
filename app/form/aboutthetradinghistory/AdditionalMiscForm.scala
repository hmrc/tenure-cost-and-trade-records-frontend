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
import form.MappingSupport.{mappingPerYear, nonNegativeNumberWithYear, tradingPeriodWeeks, turnoverSalesMappingWithYear}
import models.submissions.aboutthetradinghistory.{AdditionalMisc, AdditionalMiscDetails}
import play.api.data.Forms.{mapping, tuple}
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object AdditionalMiscForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[AdditionalMisc] =
    mapping(
      "tradingPeriod"           -> tradingPeriodWeeks(year),
      "leisureReceipts"         -> turnoverSalesMappingWithYear("additionalMisc.leisureReceipts", year),
      "winterStorageReceipts"   -> turnoverSalesMappingWithYear("additionalMisc.winterStorageReceipts", year),
      "numberOfVans"            -> nonNegativeNumberWithYear("additionalMisc.numberOfVans", year),
      "otherActivitiesReceipts" -> turnoverSalesMappingWithYear("additionalMisc.otherActivitiesReceipts", year),
      "otherServicesReceipts"   -> turnoverSalesMappingWithYear("additionalMisc.otherServicesReceipts", year),
      "bottledGasReceipts"      -> turnoverSalesMappingWithYear("additionalMisc.bottledGasReceipts", year)
    )(AdditionalMisc.apply)(o => Some(Tuple.fromProductTyped(o)))

  private def additionalMiscMapping(years: Seq[String])(implicit messages: Messages): Mapping[Seq[AdditionalMisc]] =
    mappingPerYear(years, (year, idx) => s"[$idx]" -> columnMapping(year))

  def additionalMiscForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[(Seq[AdditionalMisc], AdditionalMiscDetails)] =
    Form {
      tuple(
        "additionalMisc" -> additionalMiscMapping(years),
        "details"        -> mapping(
          "leisureReceiptsDetails"         -> mandatoryStringIfNonZeroSum(
            ".leisureReceipts",
            "error.additionalMisc.leisureReceipts.details.required"
          ).verifying(
            maxLength(1000, "error.additionalMisc.leisureReceipts.details.maxLength")
          ).transform[Option[String]](Some(_), _.getOrElse("")),
          "otherActivitiesReceiptsDetails" -> mandatoryStringIfNonZeroSum(
            ".otherActivitiesReceipts",
            "error.additionalMisc.otherActivitiesReceipts.details.required"
          ).verifying(
            maxLength(1000, "error.additionalMisc.otherActivitiesReceipts.details.maxLength")
          ).transform[Option[String]](Some(_), _.getOrElse(""))
        )(AdditionalMiscDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
      )
    }

}
