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

package controllers.connectiontoproperty

import form.Errors
import models.submissions.connectiontoproperty.StillConnectedDetails
import form.connectiontoproperty.ProvideContactDetailsForm.provideContactDetailsForm
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class ProvideContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def provideContactDetailsController(
    provideContactDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = provideContactDetails),
    mockSessionRepo
  )

  def provideContactDetailsControllerYesRentReceivedNoLettings(
    provideContactDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesRentReceivedNoLettings)
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = provideContactDetails),
    mockSessionRepo
  )

  def provideContactDetailsControllerNoToAll(
    provideContactDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsNoToAll)
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = provideContactDetails),
    mockSessionRepo
  )

  def provideContactDetailsControllerNone(
    provideContactDetails: Option[StillConnectedDetails] = None
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = provideContactDetails),
    mockSessionRepo
  )

  "Provide contact details controller" should {
    "GET / return 200" in {
      val result = provideContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = provideContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 with no rent received and no lettings" in {
      val result = provideContactDetailsControllerYesRentReceivedNoLettings().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
      )
    }

    "GET / return 200 with no rent received" in {
      val result = provideContactDetailsControllerNoToAll().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "GET / return exception with none for rent received" in {
      val result = provideContactDetailsControllerNone().show(fakeRequest)
      result.failed.recover { case e: Exception =>
        e.getMessage shouldBe " Navigation for provide your contact details page reached with error Unknown connection to property back link"
      }
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = provideContactDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Provide contact details form" should {
    "error if fullName is missing " in {
      val formData = baseFormData - errorKey.fullName
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.fullName, "error.fullName.required", form)
    }

    "error if phone is missing" in {
      val formData = baseFormData + (errorKey.phone -> "")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneRequired, form)
    }
    "error if phone number is too short" in {
      val formData = baseFormData + (errorKey.phone -> "12345")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.contactPhoneLength, form)
    }

    "error if phone number is invalid format" in {
      val formData = baseFormData + (errorKey.phone -> "invalid_phone_number")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.phone, Errors.invalidPhone, form)
    }

    "error if email is missing" in {
      val formData = baseFormData + (errorKey.email -> "")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.contactEmailRequired, form)
    }

    "error if email is invalid format" in {
      val formData = baseFormData + (errorKey.email -> "invalid_email_address")
      val form     = provideContactDetailsForm.bind(formData)

      mustContainError(errorKey.email, Errors.emailFormat, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val fullName: String
      val phone: String
      val email: String
      val email1TooLong: String
    } = new {
      val fullName: String = "yourContactDetails.fullName"
      val phone            = "yourContactDetails.contactDetails.phone"
      val email            = "yourContactDetails.contactDetails.email"
      val email1TooLong    = "yourContactDetails.contactDetails.email.email.tooLong"
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
