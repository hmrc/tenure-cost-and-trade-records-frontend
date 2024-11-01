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

package controllers.connectiontoproperty

import form.connectiontoproperty.TradingNamePayingRentForm.tradingNamePayingRentForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class TradingNamePayingRentControllerSpec extends TestBaseSpec {
  import TestData._
  def tradingNamePayingRentController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new TradingNamePayingRentController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNamePayRentView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "AreYouThirdPartyController GET /" should {
    "return 200 and HTML with trading name paying rent present in session" in {
      val result = tradingNamePayingRentController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      )
    }

    "return 200 and HTML with trading name paying rent is not present in session" in {
      val controller =
        tradingNamePayingRentController(stillConnectedDetails = Some(prefilledStillConnectedDetailsNoneOwnProperty))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      )
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNamePayingRentController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val res = tradingNamePayingRentController().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "tradingNamePayingRent" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = tradingNamePayingRentController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tradingNamePayingRent" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Trading Name paying rent form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.tradingNamePayingRent
        val form     = tradingNamePayingRentForm.bind(formData)

        mustContainError(errorKey.tradingNamePayingRent, "error.tradingNamePayingRent.missing", form)
      }
    }
  }

  "getBackLink" should {
    "return back link to CYA page if query param present" in {
      val result = tradingNamePayingRentController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }
    "return back link to does (...) own the property page if 'from' query param is not present" in {
      val result = tradingNamePayingRentController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show().url
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val tradingNamePayingRent = "tradingNamePayingRent"
    }

    val baseFormData: Map[String, String] = Map(
      "tradingNamePayingRent" -> "yes"
    )
  }
}
