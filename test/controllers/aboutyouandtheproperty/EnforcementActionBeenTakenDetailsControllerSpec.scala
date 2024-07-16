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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import form.aboutyouandtheproperty.EnforcementActionDetailsForm.enforcementActionDetailsForm
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError
import scala.language.reflectiveCalls

class EnforcementActionBeenTakenDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def enforcementActionBeenTakenDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new EnforcementActionBeenTakenDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    enforcemenntActionBeenTakenDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def enforcementActionBeenTakenDetailsControllerNone() = new EnforcementActionBeenTakenDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    enforcemenntActionBeenTakenDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Enforcement action been taken details controller" should {
    "GET / return 200 c" in {
      val result = enforcementActionBeenTakenDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = enforcementActionBeenTakenDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no enforcement action details in the session" in {
      val result = enforcementActionBeenTakenDetailsControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res =
          enforcementActionBeenTakenDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Enforcement action been taken details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.enforcementActionHasBeenTakenDetails
      val form     = enforcementActionDetailsForm.bind(formData)

      mustContainError(
        errorKey.enforcementActionHasBeenTakenDetails,
        "error.enforcementActionHasBeenTakenDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val enforcementActionHasBeenTakenDetails: String = "enforcementActionHasBeenTakenDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "enforcementActionHasBeenTakenDetails" -> "Test content"
    )
  }
}
