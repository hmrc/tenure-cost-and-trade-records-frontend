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

import form.CheckYourAnswersAndConfirmForm.theForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import utils.TestBaseSpec
import play.api.test.Helpers._
import play.api.http.Status
import play.api.test.FakeRequest
import utils.FormBindingTestAssertions.mustContainError
import scala.language.reflectiveCalls

class CheckYourAnswersConnectionToPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def checkYourAnswersConnectionToPropertyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ) =
    new CheckYourAnswersConnectionToPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      checkYourAnswersConnectionToProperty,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200 and HTML with Connection to property CYA in session" in {
      val result = checkYourAnswersConnectionToPropertyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url
      )
    }

    "return 200 and HTML with None in session" in {
      val result = checkYourAnswersConnectionToPropertyController(None).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouThirdPartyController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersConnectionToPropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = checkYourAnswersConnectionToPropertyController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "answersChecked"   -> "yes",
          "answersConfirmed" -> "true"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Check Your Answers Connection To Property form" should {
    "error if checkYourAnswersConnectionToProperty is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersConnectionToProperty
      val form     = theForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersConnectionToProperty, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val checkYourAnswersConnectionToProperty: String =
        "answersChecked"
    }

    val baseFormData: Map[String, String] = Map("answersChecked" -> "yes")
  }
}
