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
import models.submissions.aboutthetradinghistory.{VariableOperatingExpenses, VariableOperatingExpensesSections}
import play.api.data.Forms.{ignored, mapping, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.LocalDate

object VariableOperatingExpensesForm {

  private def columnMapping(year: String)(implicit messages: Messages): Mapping[VariableOperatingExpenses] = mapping(
    "financial-year-end"               -> ignored(LocalDate.EPOCH),
    "energy-and-utilities"             -> turnoverSalesMappingWithYear("variableExpenses.energyAndUtilities", year),
    "cleaning-and-laundry"             -> turnoverSalesMappingWithYear("variableExpenses.cleaningAndLaundry", year),
    "building-maintenance-and-repairs" -> turnoverSalesMappingWithYear(
      "variableExpenses.buildingMaintenanceAndRepairs",
      year
    ),
    "fixtures-fittings-and-equipment"  -> turnoverSalesMappingWithYear(
      "variableExpenses.fixturesFittingsAndEquipment",
      year
    ),
    "advertising-and-promotions"       -> turnoverSalesMappingWithYear("variableExpenses.advertisingAndPromotions", year),
    "administration-and-sundries"      -> turnoverSalesMappingWithYear("variableExpenses.administrationAndSundries", year),
    "entertainment"                    -> turnoverSalesMappingWithYear("variableExpenses.entertainment", year),
    "other"                            -> turnoverSalesMappingWithYear("variableExpenses.other", year)
  )(VariableOperatingExpenses.apply)(o => Some(Tuple.fromProductTyped(o)))

  private def variableOperatingExpensesSeq(
    years: Seq[String]
  )(implicit messages: Messages): Mapping[Seq[VariableOperatingExpenses]] =
    mappingPerYear(years, (year, idx) => s"year[$idx]" -> columnMapping(year))

  private def otherExpensesDetailsRequired: Constraint[VariableOperatingExpensesSections] =
    Constraint("constraints.otherExpensesDetailsRequired") { sections =>
      if sections.variableOperatingExpenses.exists(_.other.exists(_ > 0)) && sections.otherExpensesDetails.isEmpty then
        Invalid(Seq(ValidationError("error.variableExpenses.otherExpensesDetails.required")))
      else Valid
    }

  def variableOperatingExpensesForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[VariableOperatingExpensesSections] =
    Form {
      mapping(
        "variableOperatingExpenses" -> variableOperatingExpensesSeq(years),
        "otherExpensesDetails"      -> optional(text(maxLength = 2000))
      )(VariableOperatingExpensesSections.apply)(o => Some(Tuple.fromProductTyped(o)))
        .verifying(otherExpensesDetailsRequired)
    }

}
