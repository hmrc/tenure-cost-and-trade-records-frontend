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
import utils.TestBaseSpec
import views.html.abouttheproperty.websiteForProperty

class WebsiteForPropertyControllerSpec extends TestBaseSpec {

  import TestData._
  import form.abouttheproperty.WebsiteForPropertyForm._
  import utils.FormBindingTestAssertions._

  val mockAboutThePropertyNavigator                  = mock[AboutThePropertyNavigator]
  val mockWebsiteForPropertyView: websiteForProperty = mock[websiteForProperty]
  when(mockWebsiteForPropertyView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val websiteForPropertyController = new WebsiteForPropertyController(
    stubMessagesControllerComponents(),
    mockAboutThePropertyNavigator,
    mockWebsiteForPropertyView,
    preFilledSession,
    mockSessionRepo
  )

  "WebsiteForProperty controller" should {
    "return 200" in {
      val result = websiteForPropertyController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = websiteForPropertyController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "error if buildingOperatingHaveAWebsite is missing" in {
      val formData = baseFormData - errorKey.buildingOperatingHaveAWebsite
      val form     = websiteForPropertyForm.bind(formData)

      mustContainError(errorKey.buildingOperatingHaveAWebsite, Errors.booleanMissing, form)
    }
  }

  object TestData {
    val errorKey = new {
      val buildingOperatingHaveAWebsite: String = "buildingOperatingHaveAWebsite"
    }

    val baseFormData: Map[String, String] = Map("buildingOperatingHaveAWebsite" -> "yes")
  }
}
