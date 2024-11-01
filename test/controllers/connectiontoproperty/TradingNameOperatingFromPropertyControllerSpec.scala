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

import form.connectiontoproperty.TradingNameOperatingFromPropertyForm.tradingNameOperatingFromPropertyForm
import models.ForType
import models.ForType.*
import models.submissions.connectiontoproperty.*
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class TradingNameOperatingFromPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def tradingNameOperatingFromPropertyController(
    forType: ForType = FOR6010,
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = forType, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  def tradingNameOperatingFromProperty6076Controller(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = FOR6076, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  def tradingNameOperatingFromProperty6076ChangeAddressController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesEditCYA)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = FOR6076, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  def tradingNameOperatingFromProperty6076NoController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesNoCYA)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = FOR6076, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  def tradingNameOperatingFromPropertyControllerNoTradingName(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) =
    new TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200 when trading name present in session" in {
      val result = tradingNameOperatingFromPropertyController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 when trading name present in session change address" in {
      val result = tradingNameOperatingFromProperty6076ChangeAddressController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML change address" in {
      val result = tradingNameOperatingFromProperty6076ChangeAddressController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 when trading name present in session no" in {
      val result = tradingNameOperatingFromProperty6076NoController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML no" in {
      val result = tradingNameOperatingFromProperty6076NoController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 when trading name present in session 6076" in {
      val result = tradingNameOperatingFromProperty6076Controller().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 and HTML with trading name present in session for 6076" in {
      val controller = tradingNameOperatingFromPropertyController(forType = FOR6076)
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
        forType = FOR6076,
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
        forType = FOR6076,
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

  }

  "calculateBackLink" should {
    "return correct back link if query param from=TL is present" in {
      val result = tradingNameOperatingFromPropertyController().show(fakeRequestFromTL)
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct back link if query param from=CYA is present" in {
      val result = tradingNameOperatingFromPropertyController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }

    "return back link to AreYouStillConnectedController for 6076 and addressConnectionType is 'Yes'" in {
      val prefilledDetails: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        addressConnectionType = Some(AddressConnectionTypeYes)
      )

      val result =
        tradingNameOperatingFromProperty6076Controller(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return back link to EditAddressController for 6076 and addressConnectionType is 'YesChangeAddress'" in {
      val prefilledDetails: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        addressConnectionType = Some(AddressConnectionTypeYesChangeAddress)
      )

      val result =
        tradingNameOperatingFromProperty6076Controller(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.EditAddressController.show().url
      )
    }

    "return back link to TaskListController for 6076 and addressConnectionType is unknown" in {
      val prefilledDetails: StillConnectedDetails = prefilledStillConnectedDetailsYesToAll.copy(
        addressConnectionType = None
      )

      val result =
        tradingNameOperatingFromProperty6076Controller(stillConnectedDetails = Some(prefilledDetails)).show(fakeRequest)
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return back link to VacantPropertiesController when forType is not 6076" in {
      val result = tradingNameOperatingFromPropertyController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }
  }

  "TradingNameOperatingFromPropertyController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "tradingNameFromProperty" -> "Trading name"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tradingNameFromProperty" -> "Trading name"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "Bad request when string exceeds 50 char" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "capitalSumDescription" -> "X" * 51
        )
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
    val errorKey = new ErrorKey

    class ErrorKey {
      val tradingNameFromProperty: String = "tradingNameFromProperty"
    }

    val baseFormData: Map[String, String] = Map("tradingNameFromProperty" -> "TRADING NAME")
  }
}
