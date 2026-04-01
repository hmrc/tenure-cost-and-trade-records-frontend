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

import com.github.tomakehurst.wiremock.client.WireMock.*
import models.ForType.*
import play.api.libs.json.Json
import play.api.test.Helpers.*
import play.api.Configuration
import test.TCTRServerSpec
import uk.gov.hmrc.http.{BadRequestException, UpstreamErrorResponse}

class BackendConnectorSpec extends TCTRServerSpec:

  override def fakeAppConfiguration: Configuration =
    Configuration(
      "microservice.services.tenure-cost-and-trade-records.port" -> wireMockServer.port
    ).withFallback(super.fakeAppConfiguration)

  private val authenticatePath = "/tenure-cost-and-trade-records/authenticate"
  private val saveAsDraftPath  = "/tenure-cost-and-trade-records/saveAsDraft/"

  private val backendConnector: BackendConnector = inject[BackendConnector]

  "BackendConnector" should {
    "Login successfully given correct reference number and postcode" in {
      val testID             = "99996010008"
      val responseJsonString =
        """{"forAuthToken":"Basic OTk5OTYwMTAwMDE6U2Vuc2l0aXZlKC4uLik=","forType":"FOR6010",
          |"address":{"buildingNameNumber":"001","street1":"GORING ROAD","town":"GORING-BY-SEA,LONDON","postcode":"BN12 4AX"},"isWelsh":false}""".stripMargin
      wireMockServer.stubFor(
        post(urlEqualTo(authenticatePath)).willReturn(
          aResponse().withStatus(OK).withBody(responseJsonString)
        )
      )

      val result = backendConnector.verifyCredentials(testID, "SomePostCode").futureValue
      result.forType shouldBe FOR6010.toString

      wireMockServer.verify(postRequestedFor(urlEqualTo(authenticatePath)))
    }

    "save SubmissionDraft successfully" in {
      val testId = "99996010004"
      wireMockServer.stubFor(
        put(urlEqualTo(s"$saveAsDraftPath$testId"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      await(backendConnector.saveAsDraft(testId, submissionDraft, hc))

      wireMockServer.verify(putRequestedFor(urlEqualTo(s"$saveAsDraftPath$testId")))
    }

    "throw BadRequestException exception on save with wrong id" in {
      val testId = "99997777111"
      wireMockServer.stubFor(
        put(urlEqualTo(s"$saveAsDraftPath$testId"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      intercept[BadRequestException] {
        await(backendConnector.saveAsDraft(testId, submissionDraft, hc))
      }

      wireMockServer.verify(putRequestedFor(urlEqualTo(s"$saveAsDraftPath$testId")))
    }

    "load SubmissionDraft successfully" in {
      val testId = "99996010006"
      wireMockServer.stubFor(
        get(urlEqualTo(s"$saveAsDraftPath$testId"))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.toJson(submissionDraft))))
      )

      val result = backendConnector.loadSubmissionDraft(testId, hc).futureValue
      result.get shouldBe submissionDraft

      wireMockServer.verify(getRequestedFor(urlEqualTo(s"$saveAsDraftPath$testId")))
    }

    "return None for unknown id on load SubmissionDraft" in {
      val testId    = "unknownId_123"
      val cleanedId = "123"
      wireMockServer.stubFor(
        get(urlEqualTo(s"$saveAsDraftPath$cleanedId"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      val result = backendConnector.loadSubmissionDraft(testId, hc).futureValue
      result shouldBe None

      wireMockServer.verify(getRequestedFor(urlEqualTo(s"$saveAsDraftPath$cleanedId")))
    }

    "delete SubmissionDraft successfully" in {
      val testId = "99996010007"
      wireMockServer.stubFor(
        delete(urlEqualTo(s"$saveAsDraftPath$testId"))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj("deletedCount" -> 1))))
      )

      val result = backendConnector.deleteSubmissionDraft(testId, hc).futureValue
      result shouldBe 1

      wireMockServer.verify(deleteRequestedFor(urlEqualTo(s"$saveAsDraftPath$testId")))
    }

    "throw Unauthorized exception on 403 response for delete" in {
      val testId = "99996010008"
      wireMockServer.stubFor(
        delete(urlEqualTo(s"$saveAsDraftPath$testId"))
          .willReturn(aResponse().withStatus(FORBIDDEN))
      )

      intercept[UpstreamErrorResponse] {
        await(backendConnector.deleteSubmissionDraft(testId, hc))
      }

      wireMockServer.verify(deleteRequestedFor(urlEqualTo(s"$saveAsDraftPath$testId")))
    }

  }
