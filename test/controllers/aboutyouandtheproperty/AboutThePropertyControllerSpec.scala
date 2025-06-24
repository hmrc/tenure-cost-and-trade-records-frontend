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
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import form.aboutyouandtheproperty.AboutThePropertyForm.aboutThePropertyForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.CurrentPropertyUsed.*
import play.api.test.FakeRequest

import scala.language.reflectiveCalls

class AboutThePropertyControllerSpec extends TestBaseSpec {

  import TestData._

  val mockAudit: Audit = mock[Audit]

  def aboutThePropertyController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new AboutThePropertyController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    aboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def aboutThePropertyControllerNo(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new AboutThePropertyController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    aboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def aboutThePropertyControllerNone() = new AboutThePropertyController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    aboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "About the property controller" should {
    "GET / return 200 about the property with yes in the session" in {
      val result = aboutThePropertyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
      )
    }

    "GET / return HTML" in {
      val result = aboutThePropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 about the property with no in the session" in {
      val result = aboutThePropertyControllerNo().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
      )
    }

    "GET / return 200 no about the property in the session" in {
      val result = aboutThePropertyControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutThePropertyController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutThePropertyController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = aboutThePropertyController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "propertyCurrentlyUsed" -> CurrentPropertyHotel.toString
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "About the property form" should {
    "error if propertyCurrentlyUsed is missing" in {
      val formData = baseFormData - errorKey.propertyCurrentlyUsed
      val form     = aboutThePropertyForm.bind(formData)

      mustContainError(errorKey.propertyCurrentlyUsed, "error.currentPropertyUse.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val propertyCurrentlyUsed: String = "propertyCurrentlyUsed"
    }

    val baseFormData: Map[String, String] =
      Map("propertyCurrentlyUsed" -> CurrentPropertyHotel.toString)
  }
}
