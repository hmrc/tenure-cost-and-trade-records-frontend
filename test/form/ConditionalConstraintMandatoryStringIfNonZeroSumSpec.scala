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

package form

import form.ConditionalConstraintMappings.mandatoryStringIfNonZeroSum
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.data.Forms.{list, mapping, single, text}
import play.api.data.validation.Constraints.maxLength
import play.api.data.{Form, FormError, Mapping}
import util.NumberUtil.zeroBigDecimal

import scala.collection.immutable.ArraySeq
import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
class ConditionalConstraintMandatoryStringIfNonZeroSumSpec extends AnyFlatSpec with should.Matchers {

  private def columnMapping: Mapping[BigDecimal] = single(
    "otherIncome" -> text
      .verifying("error.otherIncome.range", s => Try(BigDecimal(s)).isSuccess)
      .transform[BigDecimal](BigDecimal(_), _.toString)
  )

  private val form: Form[NonZeroSumModel] = Form(
    mapping(
      "turnover"           -> list(columnMapping),
      "otherIncomeDetails" -> mandatoryStringIfNonZeroSum(
        ".otherIncome",
        "error.otherIncomeDetails.required"
      ).verifying(
        maxLength(2000, "error.otherIncomeDetails.maxLength")
      )
    )(NonZeroSumModel.apply)(NonZeroSumModel.unapply)
  )

  "mandatoryStringIfNonZeroSum" should "bind all mapped values" in {
    val data = Map(
      "turnover[0].otherIncome" -> "1000",
      "otherIncomeDetails"      -> "otherIncome details",
      "unknown"                 -> "value"
    )
    val res  = form.bind(data)

    res.errors shouldBe empty
    res.value  shouldBe Some(NonZeroSumModel(List(BigDecimal("1000")), "otherIncome details"))
  }

  it should "make mandatory `otherIncomeDetails` field if other income for any year is not 0" in {
    val data = Map(
      "turnover[0].otherIncome" -> "0",
      "turnover[1].otherIncome" -> "2000"
    )
    val res  = form.bind(data)

    res.errors shouldBe List(FormError("otherIncomeDetails", List("error.otherIncomeDetails.required"), List.empty))
    res.value  shouldBe None
  }

  it should "make optional `otherIncomeDetails` field if `otherIncome` fields sum is zero" in {
    val data = Map(
      "turnover[0].otherIncome" -> "0",
      "turnover[1].otherIncome" -> "0"
    )
    val res  = form.bind(data)

    res.errors shouldBe empty
    res.value  shouldBe Some(NonZeroSumModel(List(zeroBigDecimal, zeroBigDecimal), ""))
  }

  it should "make optional `otherIncomeDetails` field if no values for `otherIncome` fields are supplied" in {
    val res = form.bind(Map.empty[String, String])

    res.errors shouldBe empty
    res.value  shouldBe Some(NonZeroSumModel(List.empty, ""))
  }

  it should "bind value for optional `otherIncomeDetails` field if no values for `otherIncome` fields are supplied" in {
    val data = Map(
      "otherIncomeDetails" -> "No other income"
    )
    val res  = form.bind(data)

    res.errors shouldBe empty
    res.value  shouldBe Some(NonZeroSumModel(List.empty, "No other income"))
  }

  it should "return maxLength error for too big `otherIncomeDetails`" in {
    val data = Map[String, String](
      "otherIncomeDetails" -> ("Too big other income details." + "x" * 2000)
    )
    val res  = form.bind(data)

    res.errors shouldBe List(
      FormError("otherIncomeDetails", List("error.otherIncomeDetails.maxLength"), ArraySeq(2000))
    )
    res.value  shouldBe None
  }

  case class NonZeroSumModel(
    items: List[BigDecimal] = List.empty,
    description: String
  )

}
