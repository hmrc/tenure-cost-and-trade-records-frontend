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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class PremisesLicenseConditionsControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutyouandtheproperty.PremisesLicenseConditionsForm._
  import utils.FormBindingTestAssertions._

  def premisesLicenseController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new PremisesLicenseConditionsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicensableView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def premisesLicenseControllerNo(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new PremisesLicenseConditionsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicensableView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def premisesLicenseControllerNone() = new PremisesLicenseConditionsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicensableView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Premises licence conditions controller" should {
    "GET / return 200 license conditions yes in the session" in {
      val result = premisesLicenseController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = premisesLicenseController().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show().url
      )
    }

    "GET / return 200 license conditions no in the session" in {
      val result = premisesLicenseControllerNo().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show().url
      )
    }

    "GET / return 200 no license conditions in the session" in {
      val result = premisesLicenseControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = premisesLicenseController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Premises license conditions form" should {
    "error if premisesLicenseConditions is missing" in {
      val formData = baseFormData - errorKey.premisesLicenseConditions
      val form     = premisesLicenseConditionsForm.bind(formData)

      mustContainError(errorKey.premisesLicenseConditions, "error.premisesLicenseConditions.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val premisesLicenseConditions: String
    } = new {
      val premisesLicenseConditions: String = "premisesLicenseConditions"
    }

    val baseFormData: Map[String, String] = Map("premisesLicenseConditions" -> "yes")
  }
}
