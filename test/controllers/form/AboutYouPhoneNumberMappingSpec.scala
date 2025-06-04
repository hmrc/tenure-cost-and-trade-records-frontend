/*
 * Copyright 2025 HM Revenue & Customs
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

import form.AboutYouPhoneNumberMapping.validateAboutYouPhoneNumber
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class AboutYouPhoneNumberMappingSpec extends AnyWordSpecLike with should.Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("phone" -> validateAboutYouPhoneNumber))
  }

  "phone number validation" should {

    "catch invalid length error" in new Setup {
      val lengths = Table(
        ("phone number", "validity"),
        ("1", false),
        ("12", false),
        ("012345678901234567890", false), // 21
        ("0123456789012345678901", false), // 22
        ("0123456789", true), // 10
        ("012345678901", true),
        ("+44 1234567890", true),
        ("+44 0808 157 0192", true),
        ("+447904567897", true),
        ("0790 305 1234", true),
        ("0790-678-8992", true),
        ("+44 790-590-5876", true)
      )

      TableDrivenPropertyChecks.forAll(lengths) { (phone, isValid) =>
        val res: Form[String] = form.bind(Map("phone" -> phone))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors.head.message shouldBe "error.contact.phone.invalidLength"
        }
      }
    }

    "catch invalid format error" in new Setup {
      private val invalidPhoneNumbers = Table(
        "phone number",
        "phonenumber123456",
        "phone number 1234",
        "00447!904098765"
      )

      TableDrivenPropertyChecks.forAll(invalidPhoneNumbers) { phone =>
        val res: Form[String] = form.bind(Map("phone" -> phone))
        res.errors.head.message shouldBe "error.invalid_phone"
      }
    }

    "catch mandatory condition" in new Setup {
      val isNumber = Table(
        ("phone number", "validity"),
        ("01234567890", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isNumber) { (phone, isValid) =>
        val res: Form[String] = form.bind(Map("phone" -> phone))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors.head.message shouldBe "error.contact.phoneAboutYou.required"
        }
      }
    }
  }

}
