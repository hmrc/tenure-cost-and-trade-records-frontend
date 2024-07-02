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

package controllers.aboutyouandtheproperty

import form.aboutyouandtheproperty.TradingActivityForm.tradingActivityForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class TradingActivityControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def tradingActivityController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new TradingActivityController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tradingActivityView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def tradingActivityControllerNone() = new TradingActivityController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    tradingActivityView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "TradingActivityController controller" should {
    "return 200 trading activity in the session" in {
      val result = tradingActivityController().show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = tradingActivityController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 no trading activity in the session" in {
      val result = tradingActivityControllerNone().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = tradingActivityController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
    }
  }

  "Trading activity form" should {
    "error if trading activity answer is missing" in {
      val formData = baseFormData - errorKey.tradingActivity
      val form     = tradingActivityForm.bind(formData)

      mustContainError(errorKey.tradingActivity, "error.tradingActivity.missing", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val tradingActivity: String
    } = new {
      val tradingActivity: String = "tradingActivityQuestion"
    }

    val baseFormData: Map[String, String] = Map("tradingActivityQuestion" -> "yes")
  }
}
