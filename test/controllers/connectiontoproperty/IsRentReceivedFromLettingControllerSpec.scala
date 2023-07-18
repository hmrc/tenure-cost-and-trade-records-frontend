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

package controllers.connectiontoproperty

import form.Errors
import form.connectiontoproperty.isRentReceivedFromLettingForm.isRentReceivedFromLettingForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class IsRentReceivedFromLettingControllerSpec extends TestBaseSpec {
  import TestData._
  def isRentReceivedFromLettingController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new IsRentReceivedFromLettingController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      isRentReceivedFromLettingView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "IsRentReceivedFromLettingController GET /" should {

    "return 200" in {
      val result = isRentReceivedFromLettingController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = isRentReceivedFromLettingController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "IsRentReceivedFromLettingController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = isRentReceivedFromLettingController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Is rent received from letting form" should {
      "error if isRentReceivedFromLetting is missing" in {
        val formData = baseFormData - errorKey.isRentReceivedFromLetting
        val form     = isRentReceivedFromLettingForm.bind(formData)

        mustContainError(errorKey.isRentReceivedFromLetting, Errors.booleanMissing, form)
      }
    }
  }

  object TestData {
    val errorKey = new {
      val isRentReceivedFromLetting = "isRentReceivedFromLetting"
    }

    val baseFormData: Map[String, String] = Map(
      "isRentReceivedFromLetting" -> "yes"
    )
  }
}
