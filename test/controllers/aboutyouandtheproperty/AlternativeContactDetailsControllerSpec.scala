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
import form.aboutyouandtheproperty.AlternativeContactDetailsForm.alternativeContactDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AlternativeContactDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockAudit: Audit = mock[Audit]
  def alternativeContactDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new AlternativeContactDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    alternativeContactDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def alternativeContactDetailsControllerNone() = new AlternativeContactDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    alternativeContactDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "GET / AlternativeContactDetailsController" should {
    "GET / return 200 about you in the session" in {
      val result = alternativeContactDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = alternativeContactDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no about you in the session" in {
      val result = alternativeContactDetailsControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT / AlternativeContactDetailsController" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = alternativeContactDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(result) shouldBe BAD_REQUEST
    }
  }

  "Additional information form" should {
    "error if buildingNameNumber is missing" in {
      val formDataWithEmptyBuildingNameNumber = baseFormData.updated(TestData.errorKey.buildingNameNumber, "")
      val form                                = alternativeContactDetailsForm.bind(formDataWithEmptyBuildingNameNumber)

      mustContainError(errorKey.buildingNameNumber, "error.buildingNameNumber.required", form)
    }

    "error if town is missing" in {
      val formDataWithEmptyTown = baseFormData.updated(TestData.errorKey.town, "")
      val form                  = alternativeContactDetailsForm.bind(formDataWithEmptyTown)

      mustContainError(errorKey.town, "error.townCity.required", form)
    }

    "error if postcode is missing" in {
      val formData = baseFormData - errorKey.postcode
      val form     = alternativeContactDetailsForm.bind(formData)

      mustContainError(errorKey.postcode, "error.postcodeAlternativeContact.required", form)
    }

  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val fullName: String   = "alternativeContactFullName"
      val phone              = "alternativeContactDetails.phone"
      val email              = "alternativeContactDetails.email"
      val email1TooLong      = "contactDetails.email.email.tooLong"
      val buildingNameNumber = "alternativeContactAddress.buildingNameNumber"
      val town               = "alternativeContactAddress.town"
      val postcode           = "alternativeContactAddress.postcode"
    }

    val tooLongEmail                      = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "contactDetails.phone"               -> "12345678901",
      "contactDetails.phone"               -> "01234 123123",
      "contactDetails.email1"              -> "blah.blah@test.com",
      "alternativeContactFullName"         -> "Mr John Smith",
      "alternativeContactAddress.postcode" -> "BN12 4AX"
    )
  }

}
