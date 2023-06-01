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

package controllers.aboutyouandtheproperty

import form.Errors
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import form.aboutyouandtheproperty.AboutThePropertyForm.aboutThePropertyForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, CurrentPropertyPublicHouse}
import play.api.test.FakeRequest

class AboutThePropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def aboutThePropertyController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new AboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    aboutThePropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "About the property controller" should {
    "return 200" in {
      val result = aboutThePropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutThePropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = aboutThePropertyController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "About the property form" should {
    "error if currentOccupierName is missing" in {
      val formData = baseFormData - errorKey.currentOccupierName
      val form     = aboutThePropertyForm.bind(formData)

      mustContainError(errorKey.currentOccupierName, Errors.required, form)
    }

    "error if propertyCurrentlyUsed is missing" in {
      val formData = baseFormData - errorKey.propertyCurrentlyUsed
      val form     = aboutThePropertyForm.bind(formData)

      mustContainError(errorKey.propertyCurrentlyUsed, Errors.currentOccupierName, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val currentOccupierName: String
      val propertyCurrentlyUsed: String
    } = new {
      val currentOccupierName: String   = "currentOccupierName"
      val propertyCurrentlyUsed: String = "propertyCurrentlyUsed"
    }

    val baseFormData: Map[String, String] =
      Map("currentOccupierName" -> "Tobermory", "propertyCurrentlyUsed[0]" -> CurrentPropertyPublicHouse.name)
  }
}
