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

package controllers.abouttheproperty

import form.Errors
import navigation.AboutThePropertyNavigator
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import views.html.abouttheproperty.aboutTheProperty
import form.abouttheproperty.AboutThePropertyForm.aboutThePropertyForm
import models.submissions.abouttheproperty.CurrentPropertyPublicHouse

class AboutThePropertyControllerSpec extends TestBaseSpec {

  import TestData._

  val mockAboutThePropertyNavigator              = mock[AboutThePropertyNavigator]
  val mockAboutThePropertyView: aboutTheProperty = mock[aboutTheProperty]
  when(mockAboutThePropertyView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val aboutThePropertyController = new AboutThePropertyController(
    stubMessagesControllerComponents(),
    mockAboutThePropertyNavigator,
    mockAboutThePropertyView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = aboutThePropertyController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutThePropertyController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

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
    val errorKey = new {
      val currentOccupierName: String   = "currentOccupierName"
      val propertyCurrentlyUsed: String = "propertyCurrentlyUsed"
    }

    val baseFormData: Map[String, String] =
      Map("currentOccupierName" -> "Tobermory", "propertyCurrentlyUsed" -> CurrentPropertyPublicHouse.name)
  }
}
