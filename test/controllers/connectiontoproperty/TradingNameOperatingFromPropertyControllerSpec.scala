/*
 * Copyright 2026 HM Revenue & Customs
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
import form.connectiontoproperty.TradingNameOperatingFromPropertyForm.tradingNameOperatingFromPropertyForm
import models.ForType
import models.ForType.*
import models.submissions.connectiontoproperty.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import test.ControllerSpec

import scala.language.reflectiveCalls

class TradingNameOperatingFromPropertyControllerSpec extends ControllerSpec:

  import TestData.*

  val mockAudit: Audit = mock[Audit]

  def tradingNameOperatingFromPropertyController(
    forType: ForType = FOR6010,
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ): TradingNameOperatingFromPropertyController =
    TradingNameOperatingFromPropertyController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      tradingNameOperatingFromProperty,
      preEnrichedActionRefiner(forType = forType, stillConnectedDetails = stillConnectedDetails),
      mockSessionRepository
    )

  "GET /" should {
    "return 200 when trading name present in session" in {
      val result = tradingNameOperatingFromPropertyController().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 when trading name present in session for 6048" in {
      val result = tradingNameOperatingFromPropertyController(forType = FOR6048).show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 when none in session for 6048" in {
      val result = tradingNameOperatingFromPropertyController(
        forType = FOR6048,
        stillConnectedDetails = None
      ).show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesController.show().url
      )
    }

    "return 200 and HTML with trading name present in session for 6076" in {
      val controller = tradingNameOperatingFromPropertyController(
        forType = FOR6076,
        stillConnectedDetails = toOpt(prefilledStillConnectedDetailsYesToAll)
      )
      val result     = controller.show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }

    "return 200 when trading name present is not session for 6076" in {
      val controller = tradingNameOperatingFromPropertyController(
        forType = FOR6076,
        stillConnectedDetails = Some(prefilledStillConnectedDetailsEdit)
      )
      val result     = controller.show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.EditAddressController.show().url
      )
    }

    "return 200 when trading name present is not session" in {
      val controller = tradingNameOperatingFromPropertyController(
        forType = FOR6076,
        stillConnectedDetails = None
      )
      val result     = controller.show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.routes.TaskListController.show.url
      )
    }
  }

  "calculateBackLink" should {
    "return correct back link if query param from=TL is present" in {
      val result = tradingNameOperatingFromPropertyController().show(getRequestFromTL)
      contentAsString(result) should include(controllers.routes.TaskListController.show.url)
    }

    "return correct back link if query param from=CYA is present" in {
      val result = tradingNameOperatingFromPropertyController().show(getRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }

    "return back link to TaskListController for 6076 and addressConnectionType is unknown" in {
      val result =
        tradingNameOperatingFromPropertyController(forType = FOR6076, stillConnectedDetails = None).show(getRequest)
      contentAsString(result) should include(
        controllers.routes.TaskListController.show.url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if an empty form is submitted 6048" in {
      val res = tradingNameOperatingFromPropertyController(forType = FOR6048).submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "redirect when form data submitted without CYA param" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "tradingNameFromProperty" -> "Trading name"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect when form data submitted without CYA param for 6048" in {
      val res = tradingNameOperatingFromPropertyController(forType = FOR6048).submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "tradingNameFromProperty" -> "Trading name"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "redirect when form data submitted with CYA param" in {
      val res = tradingNameOperatingFromPropertyController().submit(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody(
          "tradingNameFromProperty" -> "Trading name"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "bad request when string exceeds 50 char" in {
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

  object TestData:
    val errorKey = new ErrorKey

    class ErrorKey:
      val tradingNameFromProperty: String = "tradingNameFromProperty"

    val baseFormData: Map[String, String] = Map("tradingNameFromProperty" -> "TRADING NAME")
