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

package connectors

import org.scalatest.RecoverMethods.recoverToExceptionIf
import play.api.http.Status.{BAD_REQUEST, CREATED}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import utils.TestBaseSpec

import java.net.URL

class SubmissionConnectorSpec extends TestBaseSpec {

  val refNumber = "12345678"

  private def httpPutMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.put(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus)))
    httpClientV2Mock

  "SubmissionConnector" when {

    "submitNotConnected is called" should {

      "make a PUT request and handle success response" in {
        val httpMock  = httpPutMock(CREATED)
        val connector = new HodSubmissionConnector(servicesConfig, frontendAppConfig, httpMock)

        val result = connector.submitNotConnected(refNumber, notConnectedSubmission).futureValue

        result.status shouldBe CREATED
        result.body     should include(""""previouslyConnected":false,""")

        verify(httpMock)
          .put(any[URL])(using any[HeaderCarrier])
      }

      "handle BadRequestException response" in {
        val httpMock  = httpPutMock(BAD_REQUEST)
        val connector = new HodSubmissionConnector(servicesConfig, frontendAppConfig, httpMock)

        val exception = recoverToExceptionIf[BadRequestException] {
          connector.submitNotConnected(refNumber, notConnectedSubmission)
        }.futureValue

        exception.getMessage should include(""""previouslyConnected":false,""")

        verify(httpMock)
          .put(any[URL])(using any[HeaderCarrier])
      }
    }

    "submitConnected is called" should {

      "make a PUT request and handle success response" in {
        val httpMock  = httpPutMock(CREATED)
        val connector = new HodSubmissionConnector(servicesConfig, frontendAppConfig, httpMock)

        val result = connector.submitConnected(refNumber, connectedSubmission).futureValue

        result.status shouldBe CREATED
        result.body     should include(
          """"stillConnectedDetails":{"tradingNameOperatingFromProperty":"ABC LTD","""
        )

        verify(httpMock)
          .put(any[URL])(using any[HeaderCarrier])
      }

    }

  }

}
