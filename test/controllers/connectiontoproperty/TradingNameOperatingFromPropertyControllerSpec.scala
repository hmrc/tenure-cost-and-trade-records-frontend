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

import form.connectiontoproperty.TradingNameOperatingFromPropertyForm.{tradingNameOperatingFromProperty => tradingNameOperatingFromPropertyForm}
import models.ForTypes
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class TradingNameOperatingFromPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def tradingNameOperatingFromPropertyController(
    forType: String = ForTypes.for6010,
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = forType, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "TradingNameOperatingFromPropertyController GET /" should {
    "return 200 and HTML with trading name present in session" in {
      val result = tradingNameOperatingFromPropertyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 and HTML with trading name present in session for 6076" in {
      val controller = tradingNameOperatingFromPropertyController(forType = ForTypes.for6076)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 when trading name present is not session for 6076" in {
      val controller = tradingNameOperatingFromPropertyController(
        forType = ForTypes.for6076,
        stillConnectedDetails = Some(prefilledStillConnectedDetailsEdit)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.EditAddressController.show().url
      )
    }

    "return 200 when trading name present is not session" in {
      val controller = tradingNameOperatingFromPropertyController(
        forType = ForTypes.for6076,
        stillConnectedDetails = None
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return correct back link if query param from=TL is present" in {
      val result = tradingNameOperatingFromPropertyController().show(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "TradingNameOperatingFromPropertyController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Trading Name Operating From Property form" should {
    "error if trading name is missing" in {
      val formData = baseFormData - errorKey.tradingNameFromProperty
      val form     = tradingNameOperatingFromPropertyForm.bind(formData)

      mustContainError(errorKey.tradingNameFromProperty, "error.tradingNameFromProperty.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val tradingNameFromProperty: String = "tradingNameFromProperty"
    }

    val baseFormData: Map[String, String] = Map("tradingNameFromProperty" -> "TRADING NAME")
  }
}
