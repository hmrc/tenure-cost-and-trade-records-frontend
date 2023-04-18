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

package controllers.aboutyouandtheproperty

import form.Errors
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class AboutYouControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import form.aboutyouandtheproperty.AboutYouForm.aboutYouForm
  import utils.FormBindingTestAssertions.{mustContainError, mustContainRequiredErrorFor}

  def aboutYouController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new AboutYouController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    aboutYouView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "About you controller" should {
    "return 200" in {
      val result = aboutYouController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYouController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = aboutYouController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "About you form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = aboutYouForm.bind(formData)

      mustContainRequiredErrorFor(errorKey.fullName, form)
    }

    "error if phone is missing" in {
      val formData = baseFormData - errorKey.phone
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }

    "error if email is missing" in {
      val formData = baseFormData - errorKey.email
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val fullName: String
      val phone: String
      val email: String
      val email1TooLong: String
    } = new {
      val fullName: String = "fullName"
      val phone            = "contactDetails.phone"
      val email            = "contactDetails.email"
      val email1TooLong    = "contactDetails.email.email.tooLong"
    }

    val tooLongEmail                      = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"  -> "12345678901",
      "contactDetails.phone"  -> "01234 123123",
      "contactDetails.email1" -> "blah.blah@test.com",
      "fullName"              -> "Mr John Smith"
    )

  }
}
