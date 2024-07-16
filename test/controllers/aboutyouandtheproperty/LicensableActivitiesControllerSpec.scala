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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class LicensableActivitiesControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutyouandtheproperty.LicensableActivitiesForm._
  import utils.FormBindingTestAssertions._

  def licensableActivitiesController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new LicensableActivitiesController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    licensableActivitiesView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def licensableActivitiesControllerNone() = new LicensableActivitiesController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    licensableActivitiesView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "License Activities Controller" should {
    "GET / return 200 licensable activities in the session" in {
      val result = licensableActivitiesController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = licensableActivitiesController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no licensable activities in the session" in {
      val result = licensableActivitiesControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = licensableActivitiesController().submit(
          fakeRequest.withFormUrlEncodedBody(Seq.empty*)
        )
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Licensable activities form" should {
    "error if licensableActivities is missing" in {
      val formData = baseFormData - errorKey.licensableActivities
      val form     = licensableActivitiesForm.bind(formData)

      mustContainError(errorKey.licensableActivities, "error.licensableActivities.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val licensableActivities: String = "licensableActivities"
    }

    val baseFormData: Map[String, String] = Map("licensableActivities" -> "yes")
  }
}
