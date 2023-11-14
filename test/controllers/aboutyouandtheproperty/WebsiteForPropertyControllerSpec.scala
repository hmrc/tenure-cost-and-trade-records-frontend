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
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class WebsiteForPropertyControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutyouandtheproperty.WebsiteForPropertyForm._
  import utils.FormBindingTestAssertions._

  def websiteForPropertyController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new WebsiteForPropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    websiteForPropertyView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "WebsiteForProperty controller" should {
    "return 200" in {
      val result = websiteForPropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = websiteForPropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = websiteForPropertyController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Website for property form" should {
    "error if buildingOperatingHaveAWebsite is missing" in {
      val formData = baseFormData - errorKey.buildingOperatingHaveAWebsite
      val form     = websiteForPropertyForm.bind(formData)

      mustContainError(errorKey.buildingOperatingHaveAWebsite, Errors.buildingOperatingHaveAWebsite, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val buildingOperatingHaveAWebsite: String
    } = new {
      val buildingOperatingHaveAWebsite: String = "buildingOperatingHaveAWebsite"
    }

    val baseFormData: Map[String, String] = Map("buildingOperatingHaveAWebsite" -> "yes")
  }
}
