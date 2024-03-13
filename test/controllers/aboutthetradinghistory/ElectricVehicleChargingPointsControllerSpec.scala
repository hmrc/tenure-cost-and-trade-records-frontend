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

package controllers.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class ElectricVehicleChargingPointsControllerSpec extends TestBaseSpec {

  def electricVehicleChargingPointsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutYourTradingHistory6020)
  ) = new ElectricVehicleChargingPointsController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    electricVehicleChargingPointsView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "ElectricVehicleChargingPoints controller" should {
    "return 200" in {
      val result = electricVehicleChargingPointsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = electricVehicleChargingPointsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = electricVehicleChargingPointsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }
}