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

import form.connectiontoproperty.TradingNameOwnThePropertyForm.tradingNameOwnThePropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class TradingNameOwnThePropertyControllerSpec extends TestBaseSpec {
  import TestData._
  def tradingNameOwnThePropertyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new TradingNameOwnThePropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOwnThePropertyView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "AreYouThirdPartyController GET /" should {
    "return 200 and HTML with owner of the property present in session" in {
      val result = tradingNameOwnThePropertyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
      )
    }

    "return 200 and HTML with owner of the property is not present" in {
      val controller =
        tradingNameOwnThePropertyController(stillConnectedDetails = Some(prefilledStillConnectedDetailsYes))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
      )
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNameOwnThePropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = tradingNameOwnThePropertyController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tradingNameOwnTheProperty" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Trading Name own the property form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.tradingNameOwnTheProperty
        val form     = tradingNameOwnThePropertyForm.bind(formData)

        mustContainError(errorKey.tradingNameOwnTheProperty, "error.tradingNameOwnTheProperty.missing", form)
      }
    }
  }

  "getBackLink" should {
    "return back link to CYA page if query param present" in {
      val result = tradingNameOwnThePropertyController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }
    "return back link to trading name of the business page if 'from' query param is not present" in {
      val result = tradingNameOwnThePropertyController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
      )
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val tradingNameOwnTheProperty = "tradingNameOwnTheProperty"
    }

    val baseFormData: Map[String, String] = Map(
      "tradingNameOwnTheProperty" -> "yes"
    )
  }
}
