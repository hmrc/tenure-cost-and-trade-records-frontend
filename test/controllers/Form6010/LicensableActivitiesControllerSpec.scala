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

package controllers.Form6010

import form.Errors
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.form.{licensableActivities, licensableActivitiesDetails, premisesLicense}
import views.html.login

class LicensableActivitiesControllerSpec extends TestBaseSpec {

  import TestData._
  import form.Form6010.LicensableActivitiesForm._
  import utils.FormBindingTestAssertions._

  val mockLicensableActivitiesView = mock[licensableActivities]
  when(mockLicensableActivitiesView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val licensableActivitiesController = new LicensableActivitiesController(
    stubMessagesControllerComponents(),
    mock[login],
    mockLicensableActivitiesView,
    mock[licensableActivitiesDetails],
    mock[premisesLicense],
    preFilledSession,
    mockSessionRepo
  )

  "Controller" should {
    "return 200" in {
      val result = licensableActivitiesController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }
    "return HTML" in {
      val result = licensableActivitiesController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
    "error if licensableActivities is missing" in {
      val formData = baseFormData - errorKey.licensableActivities
      val form     = licensableActivitiesForm.bind(formData)

      mustContainError(errorKey.licensableActivities, Errors.booleanMissing, form)
    }
  }

  object TestData {
    val errorKey = new {
      val licensableActivities: String = "licensableActivities"
    }

    val baseFormData: Map[String, String] = Map("licensableActivities" -> "yes")
  }
}
