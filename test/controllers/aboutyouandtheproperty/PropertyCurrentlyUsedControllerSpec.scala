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

import form.aboutyouandtheproperty.PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class PropertyCurrentlyUsedControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def propertyCurrentlyUsedController(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(
      prefilledAboutYouAndThePropertyPartTwo6045
    )
  ) =
    new PropertyCurrentlyUsedController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      propertyCurrentlyUsedView,
      preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
      mockSessionRepo
    )

  def propertyCurrentlyUsedControllerYes(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(
      prefilledAboutYouAndThePropertyPartTwo6045
    )
  ) =
    new PropertyCurrentlyUsedController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      propertyCurrentlyUsedView,
      preEnrichedActionRefiner(
        aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes),
        aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo
      ),
      mockSessionRepo
    )

  def propertyCurrentlyUsedControllerNone(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(
      prefilledAboutYouAndThePropertyPartTwo6045
    )
  ) =
    new PropertyCurrentlyUsedController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      propertyCurrentlyUsedView,
      preEnrichedActionRefiner(
        aboutYouAndTheProperty = None,
        aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo
      ),
      mockSessionRepo
    )
  " PropertyCurrentlyUsedController GET /" should {
    "return 200 with data in session" in {
      val result = propertyCurrentlyUsedController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML with data in session" in {
      val result = propertyCurrentlyUsedController().show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = propertyCurrentlyUsedController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = propertyCurrentlyUsedController().show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }

    "return HTML with data in session if the previous answer is Yes" in {
      val result = propertyCurrentlyUsedControllerYes().show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "GET / return 200 no about the property in the session" in {
      val result = propertyCurrentlyUsedControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = propertyCurrentlyUsedController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Trading activity form" should {
    "error if trading activity answer is missing" in {
      val formData = baseFormData - errorKey.propertyCurrentlyUsed
      val form     = propertyCurrentlyUsedForm.bind(formData)

      mustContainError(errorKey.propertyCurrentlyUsed, "error.propertyCurrentlyUsed.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val propertyCurrentlyUsed: String
    } = new {
      val propertyCurrentlyUsed: String = "propertyCurrentlyUsed"
    }

    val baseFormData: Map[String, String] = Map("propertyCurrentlyUsed" -> "yes")
  }
}
