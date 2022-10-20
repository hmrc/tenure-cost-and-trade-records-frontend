/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.Form6010

import form.{AboutYouForm, Errors}
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.data.FormError
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatest.flatspec.AnyFlatSpec


class AboutYouControllerSpec extends AnyFlatSpec with should.Matchers with GuiceOneAppPerSuite {

  import TestData._
  import form.AboutYouForm._
  import utils.FormBindingTestAssertions._
//  import utils.MappingSpecs._

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[AboutYouController]

//  "GET /" should {
//    "return 200" in {
//      val result = controller.show(fakeRequest)
//      status(result) shouldBe Status.OK
//    }
//
//    "return HTML" in {
//      val result = controller.show(fakeRequest)
//      contentType(result) shouldBe Some("text/html")
//      charset(result) shouldBe Some("utf-8")
//    }
//  }

  "About you form" should "error if fullName is missing " in {
    val formData = baseFormData - errorKey.fullName
    val form = aboutYouForm.bind(formData)

    mustContainRequiredErrorFor(errorKey.fullName, form)
  }

  it should "error if userType is missing" in {
    val formData = baseFormData - errorKey.userType
    val form = aboutYouForm.bind(formData)

    mustContainError(errorKey.userType, Errors.userTypeRequired, form)
  }

  it should "error if phone is missing" in {
    val formData = baseFormData - errorKey.phone
    val form = aboutYouForm.bind(formData)

    mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
  }

  it should "error if email is missing" in {
    val formData = baseFormData - errorKey.email1
    val form = aboutYouForm.bind(formData)

    mustContainError(errorKey.email1, Errors.contactEmailRequired, form)
  }

  object TestData {
    val errorKey = new {
      val fullName: String = "fullName"
      val userType: String = "userType"
      val phone = "contactDetails.phone"
      val email1 = "contactDetails.email1"
      val email1TooLong = "contactDetails.email1.email.tooLong"
    }

    val formErrors = new {
      val required = new {
        val fullName = FormError(errorKey.fullName, Errors.required)
      }
    }

    val tooLongEmail = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "userType" -> "owner",
      "contactDetails.phone" -> "12345678901",
      "contactDetails.phone" -> "01234 123123",
      "contactDetails.email1" -> "blah.blah@test.com",
      "fullName" -> "Mr John Smith")

  }
}
