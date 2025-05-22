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

package controllers.guidance

import connectors.BackendConnector
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import utils.{JsoupHelpers, TestBaseSpec}
import views.html.referenceNumber as ReferenceNumberView

import scala.concurrent.Future.{failed, successful}

class GuidanceReferenceNumberControllerTest extends TestBaseSpec with JsoupHelpers:

  "the GuidanceReferenceNumber controller" when {
    "the user has not provided any reference number yet"   should {
      "be handling GET and reply 200 with an empty HTML from" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                shouldBe "referenceNumber.heading"
        page.backLink               shouldBe controllers.routes.Application.index.url
        page.input("referenceNumber") should beEmpty
      }
    }
    "the user has already provided their reference number" should {
      "be handling GET and reply 200 with prefilled HTML form" in new ControllerFixture {
        val result = controller.show(
          fakeGetRequest
            .withSession(
              "referenceNumber" -> "99996076012"
            )
        )
        status(result) shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                        shouldBe "referenceNumber.heading"
        page.backLink                       shouldBe controllers.routes.Application.index.url
        page.input("referenceNumber").value shouldBe "99996076012"
      }
    }
    "regardless of the user providing answers"             should {
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest
            .withFormUrlEncodedBody(
              "referenceNumber" -> "" // missing
            )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("referenceNumber") shouldBe "error.referenceNumber.required"
      }
      "be handling POST referenceNumber by replying 303 redirect to the 'GuidancePage' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest
            .withFormUrlEncodedBody(
              "referenceNumber" -> "99996076012"
            )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe controllers.guidance.routes.GuidancePageController.show("FOR6012").url
        val arg0 = captor[String]
        verify(connector, once).retrieveFORType(arg0, any[HeaderCarrier])
        arg0.getValue shouldBe "99996076012"
      }
      "be handling POST referenceNumber by replying 404 if it fails to retrieve FOR type" in new ControllerFixture {
        when(connector.retrieveFORType(anyString, any[HeaderCarrier]))
          .thenReturn(failed(new Exception("Failed to retrieve FOR type")))
        val result = controller.submit(
          fakePostRequest
            .withFormUrlEncodedBody(
              "referenceNumber" -> "b@d nUm83r" // invalid !!!
            )
        )
        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve FOR type"
        val arg0 = captor[String]
        verify(connector, once).retrieveFORType(arg0, any[HeaderCarrier])
        arg0.getValue shouldBe "b@d nUm83r"
      }
    }
  }

  trait ControllerFixture:
    val connector  = mock[BackendConnector]
    when(connector.retrieveFORType(anyString, any[HeaderCarrier])).thenReturn(successful("FOR6012"))
    val controller = new GuidanceReferenceNumberController(
      mcc = stubMessagesControllerComponents(),
      connector = connector,
      referenceNumberView = inject[ReferenceNumberView]
    )
