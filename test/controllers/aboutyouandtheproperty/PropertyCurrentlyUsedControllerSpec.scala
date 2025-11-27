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

package controllers.aboutyouandtheproperty

import connectors.Audit
import form.aboutyouandtheproperty.PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{GET, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class PropertyCurrentlyUsedControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]
  def propertyCurrentlyUsedController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes),
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(prefilledAboutYouAndThePropertyPartTwo)
  )                    = new PropertyCurrentlyUsedController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    propertyCurrentlyUsedView,
    preEnrichedActionRefiner(
      aboutYouAndTheProperty = aboutYouAndTheProperty,
      aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo
    ),
    mockSessionRepo
  )

  "PropertyCurrentlyUsedController GET /" should {
    "return 200 and HTML with Property Currently Used with yes in the session" in {
      val result = propertyCurrentlyUsedController().show(fakeRequest)
      status(result)          shouldBe Status.OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
      contentAsString(result)   should include(
        controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
      )
    }

    "return 200 and HTML with Property Currently Used with no in the session" in {
      val controller = propertyCurrentlyUsedController(aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyNo))
      val result     = controller.show(fakeRequest)
      status(result)          shouldBe Status.OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
      contentAsString(result)   should include(
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
      )
    }

    "return 200 and HTML Property Currently Used with none in the session" in {
      val controller = propertyCurrentlyUsedController(
        aboutYouAndTheProperty = None,
        aboutYouAndThePropertyPartTwo = None
      )
      val result     = controller.show(fakeRequest)
      status(result)          shouldBe Status.OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
      contentAsString(result)   should include(
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
      )
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
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = propertyCurrentlyUsedController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
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
    val errorKey = new ErrorKey

    class ErrorKey {
      val propertyCurrentlyUsed: String = "propertyCurrentlyUsed"
    }

    val baseFormData: Map[String, String] = Map("propertyCurrentlyUsed" -> "yes")
  }
}
