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

package connectors

import models.submissions.{ConnectedSubmission, NotConnectedSubmission}
import org.scalatest.RecoverMethods.recoverToExceptionIf
import play.api.libs.json.Format
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import utils.TestBaseSpec

import scala.concurrent.{ExecutionContext, Future}

class SubmissionConnectorSpec extends TestBaseSpec {

  val mockHttpClient = mock[HttpClient]
  val refNumber      = "12345678"

  val connector = new HodSubmissionConnector(servicesConfig, frontendAppConfig, mockHttpClient)

  "SubmissionConnector" when {

    "submitNotConnected is called" should {

      "make a PUT request and handle success response" in {
        when(
          mockHttpClient.PUT[NotConnectedSubmission, HttpResponse](
            startsWith(s"${connector.serviceUrl}/tenure-cost-and-trade-records/submissions/notConnected/"),
            argThat[NotConnectedSubmission](_ => true), // Matching any NotConnectedSubmission here
            any[Seq[(String, String)]]
          )(
            any[Format[NotConnectedSubmission]],
            any[HttpReads[HttpResponse]],
            any[HeaderCarrier],
            any[ExecutionContext]
          )
        )
          .thenReturn(Future.successful(HttpResponse(201, "")))

        val result = connector.submitNotConnected(refNumber, notConnectedSubmission)

        await(result) shouldBe ()
      }

      "handle BadRequestException response" in {
        when(
          mockHttpClient.PUT[NotConnectedSubmission, HttpResponse](
            startsWith(s"${connector.serviceUrl}/tenure-cost-and-trade-records/submissions/notConnected/"),
            argThat[NotConnectedSubmission](_ => true), // Matching any NotConnectedSubmission here
            any[Seq[(String, String)]]
          )(
            any[Format[NotConnectedSubmission]],
            any[HttpReads[HttpResponse]],
            any[HeaderCarrier],
            any[ExecutionContext]
          )
        )
          .thenReturn(Future.successful(HttpResponse(400, "Bad Request")))

        recoverToExceptionIf[BadRequestException] {
          connector.submitNotConnected(refNumber, notConnectedSubmission)
        } map { exception =>
          exception shouldBe a[BadRequestException]
          // exception.getMessage should include("Bad Request")
        }
      }

    }

    "submitConnected is called" should {

      "make a PUT request and handle success response" in {
        when(
          mockHttpClient.PUT[ConnectedSubmission, HttpResponse](
            startsWith(s"${connector.serviceUrl}/tenure-cost-and-trade-records/submissions/connected/"),
            argThat[ConnectedSubmission](_ => true), // Matching any ConnectedSubmission here
            any[Seq[(String, String)]]
          )(any[Format[ConnectedSubmission]], any[HttpReads[HttpResponse]], any[HeaderCarrier], any[ExecutionContext])
        )
          .thenReturn(Future.successful(HttpResponse(201, "")))

        val result = connector.submitConnected(refNumber, connectedSubmission)

        await(result) shouldBe ()
      }

    }

  }
}
