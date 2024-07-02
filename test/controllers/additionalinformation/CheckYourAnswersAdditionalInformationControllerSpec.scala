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

package controllers.additionalinformation

import form.additionalinformation.CheckYourAnswersAdditionalInformationForm.checkYourAnswersAdditionalInformationForm
import models.submissions.additionalinformation.AdditionalInformation
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class CheckYourAnswersAdditionalInformationControllerSpec extends TestBaseSpec {

  import TestData._

  def checkYourAdditionalInformationController(
    additionalInformation: Option[AdditionalInformation] = Some(prefilledAdditionalInformation)
  ) = new CheckYourAnswersAdditionalInformationController(
    stubMessagesControllerComponents(),
    additionalInformationNavigator,
    checkYourAnswersAdditionalInformationView,
    preEnrichedActionRefiner(additionalInformation = additionalInformation),
    mockSessionRepo
  )

  def checkYourAdditionalInformationControllerEmpty(
    additionalInformation: Option[AdditionalInformation] = None
  ) = new CheckYourAnswersAdditionalInformationController(
    stubMessagesControllerComponents(),
    additionalInformationNavigator,
    checkYourAnswersAdditionalInformationView,
    preEnrichedActionRefiner(additionalInformation = additionalInformation),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = checkYourAdditionalInformationController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAdditionalInformationController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "GET / empty additional info" should {
    "return html with 200" in {
      val result = checkYourAdditionalInformationControllerEmpty().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = checkYourAdditionalInformationController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(result) shouldBe BAD_REQUEST
    }
  }

  "Check Your Answers additional information form" should {
    "error if checkYourAnswersAdditionalInformation is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersAdditionalInformation
      val form     = checkYourAnswersAdditionalInformationForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersAdditionalInformation, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val checkYourAnswersAdditionalInformation: String
    } = new {
      val checkYourAnswersAdditionalInformation: String =
        "checkYourAnswersAdditionalInformation"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersAdditionalInformation" -> "yes")
  }

}
