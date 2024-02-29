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

package controllers.form

import form.BuildingNameNumberMapping.validateBuildingNameNumber
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class BuildingNameNumberMappingSpec extends AnyWordSpecLike with should.Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("buildingNameNumber" -> validateBuildingNameNumber))
  }

  "buildingNameNumber validation" should {

    "catch invalid length error" in new Setup {
      val lengths = Table(
        ("buildingNameNumber", "validity"),
        ("ICantBelieveTheNameForThisTestIsMoreThanFiftyCharactersLong", false), //59
        ("ICantBelieveTheNameForThisTestIsEvenMoreThanFiftyCharactersLong", false), //63
        ("Test Business", true),
        ("Business", true)
      )

      TableDrivenPropertyChecks.forAll(lengths) { (buildingNameNumber, isValid) =>
        val res: Form[String] = form.bind(Map("buildingNameNumber" -> buildingNameNumber))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.buildingNameNumber.maxLength"
        }
      }
    }

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("buildingNameNumber", "validity"),
        ("001", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (buildingNameNumber, isValid) =>
        val res: Form[String] = form.bind(Map("buildingNameNumber" -> buildingNameNumber))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.buildingNameNumber.required"
        }
      }
    }
  }

}
