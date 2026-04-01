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

package connectors

import models.submissions.{ConnectedSubmission, NotConnectedSubmission}
import org.scalatest.RecoverMethods.recoverToExceptionIf
import play.api.test.Helpers.*
import test.TCTRAppSpec
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}

import java.net.URL

class SubmissionConnectorSpec extends TCTRAppSpec:

  private val refNumber = "12345678"

  "SubmissionConnector on submitNotConnected" should {
    "make a PUT request and handle success response" in {
      val httpMock  = httpClientMock(PUT, responseBody = notConnectedSubmission, responseStatus = CREATED)
      val connector = HodSubmissionConnector(servicesConfig, appConfig, httpMock)

      val result = connector.submitNotConnected(refNumber, notConnectedSubmission).futureValue

      result.status                          shouldBe CREATED
      result.json.as[NotConnectedSubmission] shouldBe notConnectedSubmission

      verify(httpMock)
        .put(any[URL])(using any[HeaderCarrier])
    }

    "handle BadRequestException response" in {
      val httpMock  = httpClientMock(PUT, responseBody = "Bad Request", responseStatus = BAD_REQUEST)
      val connector = HodSubmissionConnector(servicesConfig, appConfig, httpMock)

      val exception = recoverToExceptionIf[BadRequestException] {
        connector.submitNotConnected(refNumber, notConnectedSubmission)
      }.futureValue

      println(exception.getMessage)

      exception.getMessage shouldBe """"Bad Request""""

      verify(httpMock)
        .put(any[URL])(using any[HeaderCarrier])
    }
  }

  "SubmissionConnector on submitConnected" should {
    "make a PUT request and handle success response" in {
      val httpMock  = httpClientMock(PUT, responseBody = connectedSubmission, responseStatus = CREATED)
      val connector = HodSubmissionConnector(servicesConfig, appConfig, httpMock)

      val result = connector.submitConnected(refNumber, connectedSubmission).futureValue

      result.status                       shouldBe CREATED
      result.json.as[ConnectedSubmission] shouldBe connectedSubmission

      verify(httpMock)
        .put(any[URL])(using any[HeaderCarrier])
    }
  }
