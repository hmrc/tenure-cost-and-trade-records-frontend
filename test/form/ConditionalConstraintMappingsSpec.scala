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

import form.ConditionalConstraintMappings.mandatoryStringIfExists
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.data.{Form, FormError}
import play.api.data.Forms.{list, mapping, text}
import play.api.data.validation.Constraints.maxLength

import scala.collection.compat.immutable.ArraySeq

/**
  * @author Yuriy Tumakha
  */
class ConditionalConstraintMappingsSpec extends AnyFlatSpec with should.Matchers {

  private val form: Form[Model] = Form(
    mapping(
      "items"       -> list(text),
      "description" -> mandatoryStringIfExists(
        "items[0]",
        "error.itemsDescription.required"
      ).verifying(
        maxLength(2000, "error.itemsDescription.maxLength")
      )
    )(Model.apply)(Model.unapply)
  )

  "mandatoryStringIfExists" should "bind all mapped values" in {
    val data = Map("items[0]" -> "landOnly", "description" -> "Selected items details", "unknown" -> "value")
    val res  = form.bind(data)

    res.errors shouldBe empty
    res.value  shouldBe Some(Model(List("landOnly"), "Selected items details"))
  }

  it should "make mandatory `description` field if any value selected for `items` field" in {
    val data = Map("items[0]" -> "property")
    val res  = form.bind(data)

    res.errors shouldBe List(FormError("description", List("error.itemsDescription.required"), List.empty))
    res.value  shouldBe None
  }

  it should "make optional `description` field if no value for `items` field is supplied" in {
    val res = form.bind(Map.empty[String, String])

    res.errors shouldBe empty
    res.value  shouldBe Some(Model(List.empty, ""))
  }

  it should "bind value for optional `description` field if no value for `items` field is supplied" in {
    val data = Map("description" -> "Selected items details")
    val res  = form.bind(data)

    res.errors shouldBe empty
    res.value  shouldBe Some(Model(List.empty, "Selected items details"))
  }

  it should "return maxLength error for too big `description`" in {
    val data = Map[String, String](
      "description" -> ("Too big selected items details." + "x" * 2000)
    )
    val res  = form.bind(data)

    res.errors shouldBe List(FormError("description", List("error.itemsDescription.maxLength"), ArraySeq(2000)))
    res.value  shouldBe None
  }

  case class Model(
    items: List[String] = List.empty,
    description: String
  )

}
