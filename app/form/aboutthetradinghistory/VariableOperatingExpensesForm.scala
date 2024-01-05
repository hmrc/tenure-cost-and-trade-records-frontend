/*
 * Copyright 2023 HM Revenue & Customs
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
  )(VariableOperatingExpenses.apply)(VariableOperatingExpenses.unapply)

  private def variableOperatingExpensesSeq(
    years: Seq[String]
  )(implicit messages: Messages): Mapping[Seq[VariableOperatingExpenses]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"year[$idx]" -> columnMapping(year)
    }

    mappingPerYear match {
      case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
      case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
      case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
    }
  }

  def variableOperatingExpensesForm(
    years: Seq[String]
  )(implicit messages: Messages): Form[VariableOperatingExpensesSections] =
    Form {
      mapping(
        "variableOperatingExpenses" -> variableOperatingExpensesSeq(years),
        "otherExpensesDetails"      -> optional(text(maxLength = 2000))
      )(VariableOperatingExpensesSections.apply)(VariableOperatingExpensesSections.unapply)
    }

}
