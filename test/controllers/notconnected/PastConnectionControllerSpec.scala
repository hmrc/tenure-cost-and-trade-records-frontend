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

package controllers.notconnected

import form.Errors
import models.submissions.notconnected.RemoveConnectionDetails
import form.notconnected.PastConnectionForm._
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PastConnectionControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def pastConnectionController(
    removeConnectionDetails: Option[RemoveConnectionDetails] = Some(prefilledNotConnectedYes)
  ) = new PastConnectionController(
    stubMessagesControllerComponents(),
    removeConnectionNavigator,
    pastConnectionView,
    preEnrichedActionRefiner(removeConnectionDetails = removeConnectionDetails),
    mockSessionRepo
  )

  "Premises licence conditions controller" should {
    "return 200 and HTML with Past Connections with yes in the session" in {
      val result = pastConnectionController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 and HTML with Past Connections with none in the session" in {
      val controller = pastConnectionController(Some(prefilledNotConnectedNone))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 and HTML Past Connections with none in the session" in {
      val controller = pastConnectionController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "SUBMIT /" should {
      "return 303 with location pointing to next page" in {
        val request = FakeRequest(POST, "/").withFormUrlEncodedBody("pastConnectionType" -> "yes")
        val result  = pastConnectionController().submit(request)
        status(result)                   shouldBe Status.SEE_OTHER
        header("Location", result).value shouldBe controllers.notconnected.routes.RemoveConnectionController.show().url
      }
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = pastConnectionController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "calculateBackLink" should {
    "return back link to NotConnected CYA page when 'from=CYA' query param is present and user is not connected to the property" in {
      val result = pastConnectionController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url
      )
    }

    "return back link to Are You Still Connected page if 'from' query param is not present" in {
      val result = pastConnectionController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }
  }

  "Premises license conditions form" should {
    "error if premisesLicenseConditions is missing" in {
      val formData = baseFormData - errorKey.pastConnectionType
      val form     = pastConnectionForm.bind(formData)

      mustContainError(errorKey.pastConnectionType, Errors.isPastConnected, form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val pastConnectionType: String = "pastConnectionType"
    }

    val baseFormData: Map[String, String] = Map("premisesLicenseConditions" -> "yes")
  }
}
