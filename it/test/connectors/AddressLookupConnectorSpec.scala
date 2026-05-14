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

import actions.SessionRequest
import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.addressLookup.*
import models.ForType.FOR6048
import models.Session
import models.submissions.common.Address
import play.api.Configuration
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.TCTRServerSpec

class AddressLookupConnectorSpec extends TCTRServerSpec:

  override def fakeAppConfiguration: Configuration =
    Configuration(
      "microservice.services.address-lookup-frontend.port" -> wireMockServer.port
    ).withFallback(super.fakeAppConfiguration)

  given SessionRequest[AnyContent] = SessionRequest(
    Session(
      referenceNumber = "99996010004",
      forType = FOR6048,
      address = Address("001", Some("GORING ROAD"), "GORING-BY-SEA, LONDON", Some("WEST SUSSEX"), "BN12 4AX"),
      token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false
    ),
    FakeRequest("GET", "/path/to/controller")
  )

  private val addressLookupConfig = AddressLookupConfig(
    "lookupPageHeadingKey",
    "selectPageHeadingKey",
    "lookupPageKey",
    offRampCall = Call("GET", "https://frontend.gov.net:8888/tctr/off-ramp")
  )

  private val initPath  = "/api/v2/init"
  private val connector = inject[AddressLookupConnector]

  "AddressLookupConnector on initializing the lookup journey" should {
    "succeed with the on-ramp location if the service provides it" in {
      wireMockServer.stubFor(
        post(urlEqualTo(initPath))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withHeader("Location", "/on-ramp")
          )
      )

      connector.initJourney(addressLookupConfig).futureValue shouldBe Some("/on-ramp")

      wireMockServer.verify(postRequestedFor(urlEqualTo(initPath)))
    }

    "succeed with none if the service does not provide the on-ramp location" in {
      wireMockServer.stubFor(
        post(urlEqualTo(initPath))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              // DO NOT .withHeader("Location", "/on-ramp")
          )
      )

      connector.initJourney(addressLookupConfig).futureValue shouldBe None

      wireMockServer.verify(postRequestedFor(urlEqualTo(initPath)))
    }

    "succeed with none if the service replies with HTTP status code different than 202" in {
      wireMockServer.stubFor(
        post(urlEqualTo(initPath))
          .willReturn(
            aResponse()
              .withStatus(HTTP_VERSION_NOT_SUPPORTED)
          )
      )

      connector.initJourney(addressLookupConfig).futureValue shouldBe None

      wireMockServer.verify(postRequestedFor(urlEqualTo(initPath)))
    }
  }

  "AddressLookupConnector on retrieving the confirmed address" should {
    "succeed with the confirmed address" in {
      wireMockServer.stubFor(
        get(urlEqualTo("/api/confirmed?id=123456789"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody("""
                          |{
                          |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                          |    "id" : "GB990091234524",
                          |    "address" : {
                          |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                          |        "postcode" : "ZZ1 1ZZ",
                          |        "country" : {
                          |            "code" : "GB",
                          |            "name" : "United Kingdom"
                          |        }
                          |    }
                          |}
                          |""".stripMargin)
          )
      )

      val confirmedAddress = connector.getConfirmedAddress(id = "123456789").futureValue

      confirmedAddress.auditRef shouldBe "bed4bd24-72da-42a7-9338-f43431b7ed72"
      confirmedAddress.id.get   shouldBe "GB990091234524"
      confirmedAddress.address  shouldBe AddressLookupAddress(
        Some(List("10 Other Place", "Some District", "Anytown")),
        Some("ZZ1 1ZZ"),
        Some(AddressLookupCountry(Some("United Kingdom"), Some("GB")))
      )

      wireMockServer.verify(getRequestedFor(urlEqualTo("/api/confirmed?id=123456789")))
    }
  }
