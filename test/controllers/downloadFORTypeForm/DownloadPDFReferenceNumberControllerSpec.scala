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

package controllers.downloadFORTypeForm

import connectors.BackendConnector
import play.api.http.Status
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future.{failed, successful}
import scala.language.reflectiveCalls

class DownloadPDFReferenceNumberControllerSpec extends TestBaseSpec:

  "the DownloadPDFReferenceNumber controller" when {
    "handling GET /"  should {
      "reply 200 with the HTML form" in new ControllerAndConnectorFixture {
        val result  = controller.show(fakeRequest)
        val content = contentAsString(result)
        status(result)            shouldBe Status.OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("referenceNumber.heading")
      }
    }
    "handling POST /" should {
      "reply 400 with error message when downloadPdfReferenceNumber is missing" in new ControllerAndConnectorFixture {
        val result = controller.submit(fakePostRequest)
        status(result)        shouldBe BAD_REQUEST
        contentAsString(result) should include("error.referenceNumber.required")
      }
      "reply 303 redirect to the 'Download PDF' page when downloadPdfReferenceNumber is unknown" in new ControllerAndConnectorFixture {
        when(connector.retrieveFORType(any[String], any[HeaderCarrier]))
          .thenReturn(failed(new Exception("cannot determine forType")))
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "referenceNumber" -> "unknown"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.DownloadPDFController.show("invalidType").url
        verify(connector, once).retrieveFORType(givenReferenceNumber.capture(), any[HeaderCarrier])
        givenReferenceNumber.getValue  shouldBe "unknown"
      }
      "reply 303 redirect to the 'Download PDF' page when downloadPdfReferenceNumber is good" in new ControllerAndConnectorFixture {
        when(connector.retrieveFORType(any[String], any[HeaderCarrier])).thenReturn(successful("FOR6010"))
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "referenceNumber" -> "99996010004" // this resembles a good one!
          )
        )
        status(result) shouldBe SEE_OTHER
        session(result).get("referenceNumber").value shouldBe "99996010004"
        redirectLocation(result).value               shouldBe routes.DownloadPDFController.show("FOR6010").url
        verify(connector, once).retrieveFORType(givenReferenceNumber.capture(), any[HeaderCarrier])
        givenReferenceNumber.getValue                shouldBe "99996010004"
      }
    }
  }

  trait ControllerAndConnectorFixture:
    val connector            = mock[BackendConnector]
    val givenReferenceNumber = captor[String]
    val controller           = new DownloadPDFReferenceNumberController(
      stubMessagesControllerComponents(),
      connector,
      referenceNumberView
    )
