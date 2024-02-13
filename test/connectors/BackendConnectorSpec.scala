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

package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.scalatest.BeforeAndAfterAll
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, Upstream4xxResponse}
import utils.TestBaseSpec

class BackendConnectorSpec extends TestBaseSpec with BeforeAndAfterAll {

  //private val wireMockPort = 11111

  private val endpointBase = "/tenure-cost-and-trade-records/saveAsDraft/"

  // Inject the required dependencies
  val backendConnector: DefaultBackendConnector = app.injector.instanceOf[DefaultBackendConnector]

  protected def basicWireMockConfig(): WireMockConfiguration = wireMockConfig()

  protected implicit lazy val wireMockServer: WireMockServer = {
    val server = new WireMockServer(11111)
    server.start()
    server
  }

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"                                              -> false,
        "metrics.enabled"                                          -> false,
        "create-internal-auth-token-on-start"                      -> false,
        "microservice.services.tenure-cost-and-trade-records.port" -> 11111
      )
      .build()
  override def beforeAll(): Unit = {
    super.beforeAll()
    configureFor("localhost", 11111)
    wireMockServer.start()
  }

  override def afterEach(): Unit =
    wireMockServer.resetAll()
  override def afterAll(): Unit = {
    wireMockServer.stop()
    super.afterAll()
  }

  "BackendConnector" should {

    "Login successfully given correct reference number and postcode" in {
      val testID             = "99996010008"
      val responseJsonString =
        """
          |{"forAuthToken":"Basic OTk5OTYwMTAwMDE6U2Vuc2l0aXZlKC4uLik=","forType":"FOR6010","address":{"buildingNameNumber":"001","street1":"GORING ROAD","street2":"GORING-BY-SEA, WORTHING","postcode":"BN12 4AX"}}
          |""".stripMargin
      stubFor(
        post(urlEqualTo("/tenure-cost-and-trade-records/authenticate")).willReturn(
          aResponse().withStatus(200).withBody(responseJsonString)
        )
      )
      val result             = await(backendConnector.verifyCredentials(testID, "SomePostCode"))
      result.forType shouldBe "FOR6010"
    }

    "save SubmissionDraft successfully" in {
      val testId = "99996010004"
      stubFor(
        put(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(201))
      )

      val result = await(backendConnector.saveAsDraft(testId, submissionDraft, new HeaderCarrier()))

      result should be(()) // Assuming the method returns Unit on success
      wireMockServer.verify(putRequestedFor(urlEqualTo(endpointBase + testId)))
    }

    "throw BadRequestException exception on save with wrong id" in {
      val testId = "99997777111"
      stubFor(
        put(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(400))
      )

      intercept[BadRequestException] {
        await(backendConnector.saveAsDraft(testId, submissionDraft, new HeaderCarrier()))
      }
    }

    "load SubmissionDraft successfully" in {
      // Assuming a get method in connector, adjust accordingly
      val testId = "99996010006"
      stubFor(
        get(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(200).withBody(Json.stringify(Json.toJson(submissionDraft))))
      )

      val result = await(backendConnector.loadSubmissionDraft(testId, new HeaderCarrier()))

      // Adjust the assertion according to your expected result
      result.get shouldBe submissionDraft
    }

    "handle unknown id for load SubmissionDraft" in {
      // Adjust based on your expected behavior for unknown ids
      val testId = "unknownId_123"
      stubFor(
        get(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(404))
      )

      val result = await(backendConnector.loadSubmissionDraft(testId, new HeaderCarrier()))

      // Adjust the assertion based on your expected behavior
      result shouldBe None
    }

    "delete SubmissionDraft successfully" in {
      val testId = "99996010007"
      stubFor(
        delete(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(200).withBody(Json.stringify(Json.obj("deletedCount" -> 1))))
      )
      val result = await(backendConnector.deleteSubmissionDraft(testId, new HeaderCarrier()))

      result shouldBe 1
      wireMockServer.verify(deleteRequestedFor(urlEqualTo(endpointBase + testId)))
    }

    "throw Unauthorized exception on 403 response for delete" in {
      val testId = "99996010008"
      stubFor(
        delete(urlEqualTo(endpointBase + testId))
          .willReturn(aResponse().withStatus(403))
      )

      intercept[Upstream4xxResponse] {
        await(backendConnector.deleteSubmissionDraft(testId, new HeaderCarrier()))
      }
    }

  }
}
