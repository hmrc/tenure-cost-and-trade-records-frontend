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

import form.aboutyouandtheproperty.AboutThePropertyStringForm.aboutThePropertyStringForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class AboutThePropertyStringControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def aboutThePropertyStringController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYesString)
  ) = new AboutThePropertyStringController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    aboutThePropertyStringView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def aboutThePropertyStringControllerNo(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNoString)
  ) = new AboutThePropertyStringController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    aboutThePropertyStringView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def aboutThePropertyStringControllerNone() = new AboutThePropertyStringController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    aboutThePropertyStringView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "About the property controller" should {
    "GET / return 200 about the property string with yes in the session" in {
      val result = aboutThePropertyStringController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = aboutThePropertyStringController().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
      )
    }

    "GET / return 200 about the property string with no in the session" in {
      val result = aboutThePropertyStringControllerNo().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
      )
    }

    "GET / return 200 no about the property string in the session" in {
      val result = aboutThePropertyStringControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutThePropertyStringController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutThePropertyStringController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = aboutThePropertyStringController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "propertyCurrentlyUsedString" -> "Test content"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

  }

  "property currently used string form" should {
    "error if choice is missing " in {
      val formData = baseFormData - errorKey.propertyCurrentlyUsedString
      val form     = aboutThePropertyStringForm.bind(formData)

      mustContainError(errorKey.propertyCurrentlyUsedString, "error.propertyCurrentlyUsedString.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val propertyCurrentlyUsedString: String = "propertyCurrentlyUsedString"
    }

    val baseFormData: Map[String, String] = Map(
      "propertyCurrentlyUsedString" -> "Test content"
    )
  }
}
