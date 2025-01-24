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

package controllers.aboutyouandtheproperty

import connectors.Audit
import form.Errors
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AboutYouControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import form.aboutyouandtheproperty.AboutYouForm.aboutYouForm
  import utils.FormBindingTestAssertions.mustContainError

  val mockAudit: Audit = mock[Audit]

  def aboutYouController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new AboutYouController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    aboutYouView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def aboutYouControllerNone() = new AboutYouController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    aboutYouView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "About you controller" should {
    "GET / return 200 about you in the session" in {
      val result = aboutYouController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = aboutYouController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no about you in the session" in {
      val result = aboutYouControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = aboutYouController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "About you form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.fullName, "error.fullNameContactDetails.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneAboutYouRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData + (errorKey.email -> "")
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailAboutYouRequired, form)
    }

    "error if email is invalid format" in {
      val formData = baseFormData + (errorKey.email -> "invalid_email_address")
      val form     = aboutYouForm.bind(formData)

      mustContainError(errorKey.email, Errors.emailFormat, form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
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
