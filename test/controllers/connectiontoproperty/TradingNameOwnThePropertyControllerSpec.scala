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

import form.connectiontoproperty.TradingNameOwnThePropertyForm.tradingNameOwnThePropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
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

  def tradingNameOwnThePropertyControllerNoOwner(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) =
    new TradingNameOwnThePropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOwnThePropertyView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "AreYouThirdPartyController GET /" should {

    "return 200 with owner of the property present in session" in {
      val result = tradingNameOwnThePropertyController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tradingNameOwnThePropertyController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 with owner of the property is not present" in {
      val result = tradingNameOwnThePropertyControllerNoOwner().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNameOwnThePropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Trading Name own the property form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.tradingNameOwnTheProperty
        val form     = tradingNameOwnThePropertyForm.bind(formData)

        mustContainError(errorKey.tradingNameOwnTheProperty, "error.tradingNameOwnTheProperty.missing", form)
      }
    }
  }

  object TestData {
    val errorKey = new {
      val tradingNameOwnTheProperty = "tradingNameOwnTheProperty"
    }

    val baseFormData: Map[String, String] = Map(
      "tradingNameOwnTheProperty" -> "yes"
    )
  }
}
