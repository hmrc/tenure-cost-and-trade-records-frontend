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

import form.connectiontoproperty.CheckYourAnswersConnectionToPropertyForm.checkYourAnswersConnectionToPropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.test.Helpers.stubMessagesControllerComponents
import utils.TestBaseSpec
import play.api.test.Helpers._
import play.api.http.Status
import play.api.test.FakeRequest
import utils.FormBindingTestAssertions.mustContainError

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
    "return 200" in {
      val result = checkYourAnswersConnectionToPropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersConnectionToPropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersConnectionToPropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Check Your Answers Connection To Property form" should {
    "error if checkYourAnswersConnectionToProperty is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersConnectionToProperty
      val form     = checkYourAnswersConnectionToPropertyForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersConnectionToProperty, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val checkYourAnswersConnectionToProperty: String
    } = new {
      val checkYourAnswersConnectionToProperty: String =
        "checkYourAnswersConnectionToProperty"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersConnectionToProperty" -> "yes")
  }
}
