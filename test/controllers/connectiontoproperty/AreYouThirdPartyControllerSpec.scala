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
import controllers.connectiontoproperty.TestData.{baseFormData, errorKey}
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import form.connectiontoproperty.AreYouThirdPartyForm.theForm
import models.submissions.common.{AnswerNo, AnswerYes}

import scala.language.reflectiveCalls

class AreYouThirdPartyControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def areYouThirdPartyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new AreYouThirdPartyController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      areYouThirdPartyView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "AreYouThirdPartyController GET /" should {

    "return 200 and HTML with are you third party in session" in {
      val result = areYouThirdPartyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      )
    }

    "return 200 and HTML no value own property value" in {
      val result = areYouThirdPartyController(Some(prefilledStillConnectedDetailsNoToAll)).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNamePayingRentController.show().url
      )
    }

    "return 200 and HTML none own property value" in {
      val result = areYouThirdPartyController(Some(prefilledStillConnectedDetailsNoneOwnProperty)).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.ConnectionToThePropertyController.show().url
      )
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = areYouThirdPartyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val res = areYouThirdPartyController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "areYouThirdParty" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = areYouThirdPartyController().submit()(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "areYouThirdParty" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Are you third party form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.areYouThirdParty
        val form     = theForm.bind(formData)

        mustContainError(errorKey.areYouThirdParty, "error.areYouThirdParty.missing", form)
      }
    }
  }

  "SUBMIT /" should {
    "Throw a bad request if an empty form is submitted" in {
      val result = areYouThirdPartyController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(result) shouldBe BAD_REQUEST
    }

    "Throw a bad request if not empty form submitted and save data in the session" in {
      val testData = Map("areYouThirdParty" -> "yes")
      val result   = areYouThirdPartyController().submit(FakeRequest().withFormUrlEncodedBody(testData.toSeq*))

      status(result) shouldBe BAD_REQUEST
    }

  }
  "getBackLink" should {
    "return back link to CYA page if query param present" in {
      val result = areYouThirdPartyController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )

    }

    "return back link to TradingNameOwnTheProperty page when tradingNameOwnTheProperty is 'yes'" in {
      val prefilledDetailsYes: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        tradingNameOwnTheProperty = Some(AnswerYes)
      )

      val result = areYouThirdPartyController(stillConnectedDetails = Some(prefilledDetailsYes)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      )
    }

    "return back link to TradingNamePayingRent page when tradingNameOwnTheProperty is 'no'" in {
      val prefilledDetailsNo: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        tradingNameOwnTheProperty = Some(AnswerNo)
      )

      val result = areYouThirdPartyController(stillConnectedDetails = Some(prefilledDetailsNo)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNamePayingRentController.show().url
      )
    }

    "return back link to ConnectionToThePropertyController if tradingNameOwnTheProperty is not set" in {
      val prefilledDetailsNone: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        tradingNameOwnTheProperty = None
      )

      val result = areYouThirdPartyController(stillConnectedDetails = Some(prefilledDetailsNone)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.ConnectionToThePropertyController.show().url
      )
    }
  }

}

object TestData {
  val errorKey = new ErrorKey

  class ErrorKey {
    val areYouThirdParty = "areYouThirdParty"
  }

  val baseFormData: Map[String, String] = Map(
    "areYouThirdParty" -> "yes"
  )
}
