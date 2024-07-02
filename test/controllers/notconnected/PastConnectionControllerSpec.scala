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

  def pastConnectionControllerEmpty(
    removeConnectionDetails: Option[RemoveConnectionDetails] = None
  ) = new PastConnectionController(
    stubMessagesControllerComponents(),
    removeConnectionNavigator,
    pastConnectionView,
    preEnrichedActionRefiner(removeConnectionDetails = removeConnectionDetails),
    mockSessionRepo
  )

  "Premises licence conditions controller" should {
    "return 200" in {
      val result = pastConnectionController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = pastConnectionController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 for empty session" in {
      val result = pastConnectionControllerEmpty().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = pastConnectionController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
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
    val errorKey: Object {
      val pastConnectionType: String
    } = new {
      val pastConnectionType: String = "pastConnectionType"
    }

    val baseFormData: Map[String, String] = Map("premisesLicenseConditions" -> "yes")
  }
}
