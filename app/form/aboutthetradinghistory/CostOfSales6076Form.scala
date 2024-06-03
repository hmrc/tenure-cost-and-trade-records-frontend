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

import form.MappingSupport.{EnrichedSeq, costOfSalesMapping}
import models.submissions.aboutthetradinghistory.{CostOfSales6076, CostOfSales6076Sum}
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

object CostOfSales6076Form {

  private def sumMapping(year: String)(implicit messages: Messages): Mapping[CostOfSales6076Sum] = mapping(
    "fuelOrFeedstock" -> costOfSalesMapping("costOfSales6076.fuelOrFeedstock", year),
    "importedPower"   -> costOfSalesMapping("costOfSales6076.importedPower", year),
    "TNuoS"           -> costOfSalesMapping("costOfSales6076.TNuoS", year),
    "BSuoS"           -> costOfSalesMapping("costOfSales6076.BSuoS", year),
    "otherSales"      -> costOfSalesMapping("costOfSales6076.otherSales", year)
  )(CostOfSales6076Sum.apply)(CostOfSales6076Sum.unapply)

  def costOfSales6076Mapping(years: Seq[String])(implicit messages: Messages): Mapping[Seq[CostOfSales6076Sum]] = {
    val mappingPerYear = years.take(3).zipWithIndex.map { case (year, idx) =>
      s"[$idx]" -> sumMapping(year);
    }
    mappingPerYear match {
      case Seq(a)       => mapping(a)(Seq(_))(_.headOption)
      case Seq(a, b)    => mapping(a, b)(Seq(_, _))(_.toTuple2)
      case Seq(a, b, c) => mapping(a, b, c)(Seq(_, _, _))(_.toTuple3)
    }
  }

  private def otherCostsDetailsRequired: Constraint[CostOfSales6076] =
    Constraint("constraints.otherCostsDetailsRequired") { cOs =>
      if (cOs.costOfSales6076Sum.flatMap(_.other).exists(_ > 0) && cOs.otherSalesDetails.isEmpty) {
        Invalid(Seq(ValidationError("error.costOfSales6076.details.required")))
      } else {
        Valid
      }
    }

  def costOfSales6076Form(years: Seq[String])(implicit messages: Messages): Form[CostOfSales6076] =
    Form {
      mapping(
        "costOfSales6076"   -> costOfSales6076Mapping(years),
        "otherSalesDetails" -> optional(text(maxLength = 2000))
      )(CostOfSales6076.apply)(CostOfSales6076.unapply)
        .verifying(otherCostsDetailsRequired)
    }
}
