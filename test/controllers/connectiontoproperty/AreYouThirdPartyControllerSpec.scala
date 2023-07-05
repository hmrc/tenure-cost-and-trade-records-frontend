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


import controllers.connectiontoproperty.TestData.{baseFormData, errorKey}
import form.Errors
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import form.connectiontoproperty.AreYouThirdPartyForm.areYouThirdPartyForm

class AreYouThirdPartyControllerSpec extends TestBaseSpec {

  def areYouThirdPartyController(stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)) =
    new AreYouThirdPartyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      areYouThirdPartyView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "AreYouThirdPartyController GET /" should {

    "return 200" in {
      val result = areYouThirdPartyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = areYouThirdPartyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = areYouThirdPartyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Are you third party form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.areYouThirdParty
        val form = areYouThirdPartyForm.bind(formData)

        mustContainError(errorKey.areYouThirdParty, Errors.booleanMissing, form)
      }
    }
  }
}

object TestData {
  val errorKey = new {
    val areYouThirdParty = "areYouThirdParty"
  }

  val baseFormData: Map[String, String] = Map(
    "areYouThirdParty" -> "yes"
  )
}