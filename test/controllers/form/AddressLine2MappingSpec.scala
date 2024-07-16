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

import form.AddressLine2Mapping.validateAddressLineTwo
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.data.Form
import play.api.data.Forms.single

class AddressLine2MappingSpec extends AnyWordSpecLike with Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("line2" -> validateAddressLineTwo))
  }

  "address line 2 mapping" should {

    "catch invalid length error" in new Setup {
      val lengths = Table(
        ("address", "validity"),
        ("TooLongAddressLineTwoThatExceedsMaximumLengthOf50Characters1", false), // 51
        ("Valid Address Line Two", true),
        ("Apt 45, Building XYZ", true)
      )

      TableDrivenPropertyChecks.forAll(lengths) { (line2, isValid) =>
        val res: Form[String] = form.bind(Map("line2" -> line2))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.addressLineTwo.maxLength"
        }
      }
    }

    "catch invalid format error" in new Setup {
      val validFormat = Table(
        ("address", "validity"),
        ("T£$%^^%$", false),
        ("Valid Address Line Two", true)
      )

      TableDrivenPropertyChecks.forAll(validFormat) { (line2, isValid) =>
        val res: Form[String] = form.bind(Map("line2" -> line2))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.invalidCharAddress2"
        }
      }
    }

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("line2", "validity"),
        ("Valid Address Line Two", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (line2, isValid) =>
        val res: Form[String] = form.bind(Map("line2" -> line2))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.buildingNameNumber.required"
        }
      }
    }
  }
}
