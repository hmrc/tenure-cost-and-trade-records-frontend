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

import form.ElectricVehicleChargingPointsMapping.validateSpacesOrBays
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class ElectricVehicleChargingPointsMappingSpec
    extends AnyWordSpecLike
    with should.Matchers
    with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("electricVehicleChargingPoints" -> validateSpacesOrBays))
  }

  "web address validation" should {

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("number", "validity"),
        ("123", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (electricVehicleChargingPoints, isValid) =>
        val res: Form[Int] = form.bind(Map("electricVehicleChargingPoints" -> electricVehicleChargingPoints))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.spacesOrBaysNumber.nonNumeric"
        }
      }
    }

    "catch negative number" in new Setup {
      val isInput = Table(
        ("number", "validity"),
        ("123", true),
        ("-1", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (electricVehicleChargingPoints, isValid) =>
        val res: Form[Int] = form.bind(Map("electricVehicleChargingPoints" -> electricVehicleChargingPoints))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.spacesOrBaysNumber.negative"
        }
      }
    }

    "catch over 999 number" in new Setup {
      val isInput = Table(
        ("number", "validity"),
        ("123", true),
        ("9999", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (electricVehicleChargingPoints, isValid) =>
        val res: Form[Int] = form.bind(Map("electricVehicleChargingPoints" -> electricVehicleChargingPoints))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.spacesOrBaysNumber.required"
        }
      }
    }
  }
}
