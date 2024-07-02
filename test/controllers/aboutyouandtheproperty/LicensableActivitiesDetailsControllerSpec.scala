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

import form.aboutyouandtheproperty.LicensableActivitiesInformationForm.licensableActivitiesDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class LicensableActivitiesDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def licensableActivitiesDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new LicensableActivitiesDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    licensableActivitiesDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def licensableActivitiesDetailsControllerNone() = new LicensableActivitiesDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    licensableActivitiesDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Licensable activities details controller" should {
    "GET / return 200 licensable activities details in the session" in {
      val result = licensableActivitiesDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = licensableActivitiesDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "GET / return 200 no licensable activities details in the session" in {
      val result = licensableActivitiesDetailsControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = licensableActivitiesDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Licensable activities details form" should {
    "error if choice is missing " in {
      val formData = baseFormData - errorKey.licensableActivitiesDetails
      val form     = licensableActivitiesDetailsForm.bind(formData)

      mustContainError(errorKey.licensableActivitiesDetails, "error.licensableActivitiesDetails.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val licensableActivitiesDetails: String
    } = new {
      val licensableActivitiesDetails: String = "licensableActivitiesDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "licensableActivitiesDetails" -> "Test content"
    )
  }
}
