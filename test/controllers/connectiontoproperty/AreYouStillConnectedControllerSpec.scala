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

package controllers.connectiontoproperty

import connectors.Audit
import form.Errors
import form.connectiontoproperty.AreYouStillConnectedForm.theForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AreYouStillConnectedControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockAudit: Audit = mock[Audit]

  def areYouStillConnectedController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new AreYouStillConnectedController(
    stubMessagesControllerComponents(),
    mockAudit,
    connectedToPropertyNavigator,
    areYouStillConnectedView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200 and HTML with are you still connected in session" in {
      val result = areYouStillConnectedController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.routes.LoginController.show.url
      )
    }
  }

  "calculateBackLink" should {

    "return back link to CYA page when 'from=CYA' query param is present and user is connected to the property" in {
      val result = areYouStillConnectedController(
        stillConnectedDetails = Some(prefilledStillConnectedDetailsYes)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }

    "return back link to NotConnected CYA page when 'from=CYA' query param is present and user is not connected to the property" in {
      val result = areYouStillConnectedController(
        stillConnectedDetails = Some(prefilledStillConnectedDetailsNo)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url
      )
    }

    "return back link to Task List when 'from=TL' query param is present" in {
      val result = areYouStillConnectedController().show(fakeRequestFromTL)

      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return back link to Login page when no 'from' query param is present" in {
      val result = areYouStillConnectedController().show(fakeRequest)

      contentAsString(result) should include(controllers.routes.LoginController.show.url)
    }
  }

  "return 200 for empty session" in {
    val result = areYouStillConnectedController(None).show(fakeRequest)
    status(result)      shouldBe Status.OK
    contentType(result) shouldBe Some("text/html")
    charset(result)     shouldBe Some(UTF8)
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = areYouStillConnectedController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data yes submitted without CYA param" in {
      val res = areYouStillConnectedController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "isRelated" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data no submitted without CYA param" in {
      val res = areYouStillConnectedController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "isRelated" -> "no"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = areYouStillConnectedController().submit()(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "isRelated" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Are you still connected form" should {
    "error if isRelated is missing" in {
      val formData = baseFormData - errorKey.isRelated
      val form     = theForm.bind(formData)

      mustContainError(errorKey.isRelated, Errors.isConnectedError, form)
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val isRelated = "isRelated"
    }

    val baseFormData: Map[String, String] = Map(
      "isRelated" -> "yes-change-address"
    )
  }
}
