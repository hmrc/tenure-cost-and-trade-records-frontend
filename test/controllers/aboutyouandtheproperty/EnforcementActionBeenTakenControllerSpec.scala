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

import connectors.Audit
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class EnforcementActionBeenTakenControllerSpec extends TestBaseSpec {

  import TestData._
  import form.aboutyouandtheproperty.EnforcementActionForm._
  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]

  def enforcementActionBeenTakenController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new EnforcementActionBeenTakenController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    enforcementActionsTakenView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def enforcementActionBeenTakenControllerNo(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ) = new EnforcementActionBeenTakenController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    enforcementActionsTakenView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def enforcementActionBeenTakenControllerNone() = new EnforcementActionBeenTakenController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    enforcementActionsTakenView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Enforcement action been taken controller" should {
    "GET / return 200 enforcement action taken with yes in the session" in {
      val result = enforcementActionBeenTakenController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "GET / return HTML" in {
      val result = enforcementActionBeenTakenController().show(fakeRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show().url
      )
    }

    "GET / return 200 enforcement action taken with no in the session" in {
      val result = enforcementActionBeenTakenControllerNo().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show().url
      )
    }

    "GET / return 200 no enforcement action taken in the session" in {
      val result = enforcementActionBeenTakenControllerNone().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = enforcementActionBeenTakenController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }

      "Redirect when form data submitted" in {
        val res = enforcementActionBeenTakenController().submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody("enforcementActionBeenTaken" -> "yes")
        )
        status(res) shouldBe SEE_OTHER
      }
    }
  }

  "Enforcement Action Taken form" should {
    "error if enforcementActionHasBeenTakenDetails is missing" in {
      val formData = baseFormData - errorKey.enforcementAction
      val form     = enforcementActionForm.bind(formData)

      mustContainError(errorKey.enforcementAction, "error.enforcementActionBeenTaken.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val enforcementAction: String = "enforcementActionBeenTaken"
    }

    val baseFormData: Map[String, String] = Map("enforcementActionBeenTaken" -> "yes")
  }
}
