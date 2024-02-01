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

import form.WebsiteMapping.validateWebaddress
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class WebsiteMappingSpec extends AnyWordSpecLike with should.Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("websiteAddressForProperty" -> validateWebaddress))
  }

  "web address validation" should {

    "catch invalid length error" in new Setup {
      val lengths = Table(
        ("websiteAddressForProperty", "validity"),
        ("www", false),
        ("www 23", false),
        ("www.test.com", true),
        ("www.test.co.uk", true)
      )

      TableDrivenPropertyChecks.forAll(lengths) { (websiteAddressForProperty, isValid) =>
        val res: Form[String] = form.bind(Map("websiteAddressForProperty" -> websiteAddressForProperty))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.webaddressFormat.required"
        }
      }
    }

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("web address", "validity"),
        ("www.test.com", true),
        ("https://www.test.test.com", true),
        ("http://www.test.com", true),
        ("www.test@test.com", true),
        ("https://www.test.com/", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (websiteAddressForProperty, isValid) =>
        val res: Form[String] = form.bind(Map("websiteAddressForProperty" -> websiteAddressForProperty))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.websiteAddressForProperty.required"
        }
      }
    }
  }
}
