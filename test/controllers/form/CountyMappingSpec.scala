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

package controllers.form

import form.CountyMapping.validateCounty
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class CountyMappingSpec extends AnyWordSpecLike with should.Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("county" -> validateCounty))
  }

  "county validation" should {

    "catch invalid length error" in new Setup {
      val lengths = Table(
        ("county", "validity"),
        ("ICantBelieveTheNameForThisTestIsMoreThanFiftyCharactersLong", false), // 59
        ("ICantBelieveTheNameForThisTestIsEvenMoreThanFiftyCharactersLong", false), // 63
        ("Test County", true),
        ("County", true)
      )

      TableDrivenPropertyChecks.forAll(lengths) { (county, isValid) =>
        val res: Form[String] = form.bind(Map("county" -> county))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.county.maxLength"
        }
      }
    }

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("county", "validity"),
        ("Lancashire", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (county, isValid) =>
        val res: Form[String] = form.bind(Map("county" -> county))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.county.required"
        }
      }
    }
  }

}
