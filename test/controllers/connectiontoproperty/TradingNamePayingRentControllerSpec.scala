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
import form.connectiontoproperty.TradingNamePayingRentForm.tradingNamePayingRentForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

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

    "return 200" in {
      val result = tradingNamePayingRentController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tradingNamePayingRentController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "AreYouThirdPartyController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNamePayingRentController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Trading Name paying rent form" should {
      "error if areYouThirdParty is missing" in {
        val formData = baseFormData - errorKey.tradingNamePayingRent
        val form     = tradingNamePayingRentForm.bind(formData)

        mustContainError(errorKey.tradingNamePayingRent, Errors.booleanMissing, form)
      }
    }
  }

  object TestData {
    val errorKey = new {
      val tradingNamePayingRent = "tradingNamePayingRent"
    }

    val baseFormData: Map[String, String] = Map(
      "tradingNamePayingRent" -> "yes"
    )
  }
}
