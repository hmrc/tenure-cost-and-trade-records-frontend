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

import form.EmailMapping.validateEmail
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single

class EmailMappingSpec extends AnyWordSpecLike with should.Matchers with TableDrivenPropertyChecks {

  trait Setup {
    val form = Form(single("emailFormat" -> validateEmail))
  }

  "email address validation" should {

    "catch mandatory condition" in new Setup {
      val isInput = Table(
        ("web address", "validity"),
        ("test@test.com", true),
        ("more.tests@test.com", true),
        ("test@test.co.uk", true),
        ("more.tests@tests.co.uk", true),
        ("", false)
      )

      TableDrivenPropertyChecks.forAll(isInput) { (emailFormat, isValid) =>
        val res: Form[String] = form.bind(Map("emailFormat" -> emailFormat))

        if (isValid) {
          res.hasErrors shouldBe false
        } else {
          res.errors(0).message shouldBe "error.contact.email.required"
        }
      }
    }
  }
}
