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

package controllers.connectiontoproperty

import connectors.Audit
import form.Errors
import models.submissions.connectiontoproperty.{LettingPartOfPropertyDetails, StillConnectedDetails}
import form.connectiontoproperty.ProvideContactDetailsForm.provideContactDetailsForm
import models.submissions.common.{AnswerNo, AnswerYes}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class ProvideContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockAudit: Audit = mock[Audit]

  def provideContactDetailsController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) = new ProvideContactDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    connectedToPropertyNavigator,
    provideContactDetailsView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  "Provide contact details controller" should {
    "GET / return 200 and HTML with Contact Details in session" in {
      val result = provideContactDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
      )
    }

    "GET / return 200 and HTML no rent received and no lettings in session" in {
      val controller = provideContactDetailsController(
        stillConnectedDetails = Some(prefilledStillConnectedDetailsYesRentReceivedNoLettings)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
      )
    }

    "GET / return 200 and HTML with no rent received in session" in {
      val controller = provideContactDetailsController(
        stillConnectedDetails = Some(prefilledStillConnectedDetailsNoToAll)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )
    }

    "GET / return exception with none for rent received" in {
      val controller = provideContactDetailsController(stillConnectedDetails = None)
      val result     = controller.show(fakeRequest)
      result.failed.recover { case e: Exception =>
        e.getMessage shouldBe " Navigation for provide your contact details page reached with error Unknown connection to property back link"
      }
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = provideContactDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }

    "SUBMIT / Redirect when form data submitted without CYA param" in {
      val res = provideContactDetailsController().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "yourContactDetails.fullName"              -> "Full name",
          "yourContactDetails.contactDetails.phone"  -> "12345678902",
          "yourContactDetails.contactDetails.email"  -> "any.name@email.com",
          "yourContactDetails.additionalInformation" -> "Additional information"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "SUBMIT / Redirect when form data submitted with CYA param" in {
      val res = provideContactDetailsController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "yourContactDetails.fullName"              -> "Full name",
          "yourContactDetails.contactDetails.phone"  -> "12345678902",
          "yourContactDetails.contactDetails.email"  -> "any.name@email.com",
          "yourContactDetails.additionalInformation" -> "Additional information"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "getBackLink" should {

    "return back link to CYA vacant when from is 'CYA'" in {
      val result = provideContactDetailsController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }

    "return back link to AddAnotherLettingPartOfPropertyController when rent is received and no lettings" in {
      val prefilledDetails = prefilledStillConnectedDetailsYesToAll.copy(
        isAnyRentReceived = Some(AnswerYes),
        lettingPartOfPropertyDetails = IndexedSeq.empty[LettingPartOfPropertyDetails]
      )

      val result = provideContactDetailsController(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0).url
      )
    }

    "return back link to AddAnotherLettingPartOfPropertyController with correct index when rent is received and lettings exist" in {
      val prefilledDetails = prefilledStillConnectedDetailsYesToAll.copy(
        isAnyRentReceived = Some(AnswerYes),
        lettingPartOfPropertyDetailsIndex = 1
      )

      val result = provideContactDetailsController(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(1).url
      )
    }

    "return back link to IsRentReceivedFromLettingController when rent is not received" in {
      val prefilledDetails = prefilledStillConnectedDetailsYesToAll.copy(
        isAnyRentReceived = Some(AnswerNo)
      )

      val result = provideContactDetailsController(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show().url
      )

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
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
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
