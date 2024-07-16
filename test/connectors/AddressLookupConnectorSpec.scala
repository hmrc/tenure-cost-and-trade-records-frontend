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

import play.api.Application
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import utils.{TestBaseSpec, WireMockHelper}
import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import play.api.mvc.Call
class AddressLookupConnectorSpec extends TestBaseSpec with WireMockHelper {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"                                        -> false,
        "metrics.enabled"                                    -> false,
        "create-internal-auth-token-on-start"                -> false,
        "urls.tctrFrontend"                                  -> "someUrl",
        "microservice.services.address-lookup-frontend.port" -> 11111
      )
      .build()

  private lazy val connector: AddressLookupConnector = inject[AddressLookupConnector]
  private implicit val language: Lang                = Lang("en")
  val call: Call                                     = Call("GET", "/callback-url")
  val testResponseAddress: JsValue                   =
    Json.parse(input =
      "{\n\"auditRef\":\"e9e2fb3f-268f-4c4c-b928-3dc0b17259f2\",\n\"address\":{\n\"lines\":[\n\"Line1\",\n\"Line2\",\n\"Line3\",\n\"Line4\"\n],\n \"postcode\":\"NE1 1LX\",\n\"country\":{\n\"code\":\"GB\",\n\"name\":\"United Kingdom\"\n}\n}\n}"
    )

  "AddressLookupConnector" should {

    "return a location when addressLookup.initialise" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(202)
              .withHeader("Location", "/api/v2/location")
          )
      )

      val result: Option[String] = Await.result(connector.initialise(call), 500.millisecond)
      result shouldBe Some("/api/v2/location")
    }

    "return error when there is no Location" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(202)
              .withBody("")
              .withHeader("notLocation", "")
          )
      )

      val result: Option[String] = Await.result(connector.initialise(call), 500.millisecond)
      result shouldBe Some(
        s"[AddressLookupConnector][initialise] - Failed to obtain location from http://localhost:${server.port}/api/v2/init"
      )
    }

    "return error when status is other than 202" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(204)
          )
      )

      val result: Option[String] = Await.result(connector.initialise(call), 500.millisecond)
      result shouldBe None
    }

    "get None when there is an error" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
          )
      )

      val result: Future[Option[String]] = connector.initialise(call)
      whenReady(result) { res =>
        res shouldBe None
      }

    }

    "return None when HTTP call fails" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(400)
          )
      )

      val result: Future[Option[String]] = connector.initialise(call)
      whenReady(result) { res =>
        res shouldBe None
      }
    }

    "return cacheMap when called with ID" in {
      server.stubFor(
        get(urlEqualTo("/api/confirmed?id=123456789"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBody(testResponseAddress.toString)
          )
      )

      val result = Await.result(connector.getAddress(id = "123456789"), 5.second)
      result.auditRef.map(_ shouldBe "e9e2fb3f-268f-4c4c-b928-3dc0b17259f2")
    }
  }
}
