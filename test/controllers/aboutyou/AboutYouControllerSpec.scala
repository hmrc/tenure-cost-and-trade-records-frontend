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

package controllers.aboutyou

import form.Errors
import navigation.AboutYouNavigator
import play.api.data.FormError
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutyouandtheproperty.aboutYou

class AboutYouControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import form.aboutyou.AboutYouForm.aboutYouForm
  import utils.FormBindingTestAssertions.{mustContainError, mustContainRequiredErrorFor}

  val mockAboutYouNavigator = mock[AboutYouNavigator]
  val mockAboutYouView      = mock[aboutYou]
  when(mockAboutYouView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val aboutYouController = new AboutYouController(
    stubMessagesControllerComponents(),
    mockAboutYouNavigator,
    mockAboutYouView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = aboutYouController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYouController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
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
    val errorKey = new {
      val fullName: String = "fullName"
      val phone            = "contactDetails.phone"
      val email            = "contactDetails.email"
      val email1TooLong    = "contactDetails.email.email.tooLong"
    }

    val formErrors = new {
      val required = new {
        val fullName = FormError(errorKey.fullName, Errors.required)
      }
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
