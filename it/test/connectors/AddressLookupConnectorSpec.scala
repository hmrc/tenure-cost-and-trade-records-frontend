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

import actions.SessionRequest
import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.addressLookup.*
import models.ForType.FOR6048
import models.Session
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{OptionValues, Suite, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, Call}
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.http.HeaderCarrier

import scala.collection.immutable

class AddressLookupConnectorSpec extends TestSuite with GuiceOneAppPerSuite with Injecting:

  val tctrFrontendBaseUrl = "https://frontend.gov.net:9999/tctr"

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"                                        -> false,
        "metrics.enabled"                                    -> false,
        "create-internal-auth-token-on-start"                -> false,
        "urls.tctrFrontend"                                  -> tctrFrontendBaseUrl,
        "microservice.services.address-lookup-frontend.host" -> "localhost",
        "microservice.services.address-lookup-frontend.port" -> 11111
      )
      .build()

  // WARNING
  // The following nestedSuite is just a workaround for an open issue:
  // https://github.com/playframework/scalatestplus-play/issues/112
  //
  val nestedSuite = new AsyncWordSpec with Matchers with OptionValues with AddressLookupMockServer(port = 11111) {

    given SessionRequest[AnyContent] = SessionRequest(
      Session(
        referenceNumber = "99996010004",
        forType = FOR6048,
        address = models.submissions.common
          .Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
        token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
        isWelsh = false
      ),
      FakeRequest("GET", "/path/to/controller")
    )

    given HeaderCarrier = HeaderCarrier()

    val config = AddressLookupConfig(
      "lookupPageHeadingKey",
      "selectPageHeadingKey",
      "lookupPageKey",
      offRampCall = Call("GET", s"$tctrFrontendBaseUrl/off-ramp")
    )

    "the AddressLookupConnector" when {
      "initializing the lookup journey"  should {
        "succeed with the on-ramp location if the service provides it" in {
          service.stubFor(
            post(urlEqualTo("/api/v2/init"))
              .willReturn(
                aResponse()
                  .withStatus(202)
                  .withHeader("Location", "/on-ramp")
              )
          )
          val connector = inject[AddressLookupConnector]
          for result <- connector.initJourney(config) yield result.value mustBe "/on-ramp"
        }
        "succeed with none if the service does not provide the on-ramp location" in {
          service.stubFor(
            post(urlEqualTo("/api/v2/init"))
              .willReturn(
                aResponse()
                  .withStatus(202)
                  // DO NOT .withHeader("Location", "/on-ramp")
              )
          )
          val connector = inject[AddressLookupConnector]
          for result <- connector.initJourney(config) yield result mustBe None
        }
        "succeed with none if the service replies with HTTP status code different than 202" in {
          service.stubFor(
            post(urlEqualTo("/api/v2/init"))
              .willReturn(
                aResponse()
                  .withStatus(505)
              )
          )
          val connector = inject[AddressLookupConnector]
          for result <- connector.initJourney(config) yield result mustBe None
        }
      }
      "retrieving the confirmed address" should {
        "succeed with the confirmed address" in {
          service.stubFor(
            get(urlEqualTo("/api/confirmed?id=123456789"))
              .willReturn(
                aResponse()
                  .withStatus(200)
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

          val connector = inject[AddressLookupConnector]
          for result <- connector.getConfirmedAddress(id = "123456789")
          yield {
            result.auditRef mustBe "bed4bd24-72da-42a7-9338-f43431b7ed72"
            result.id.value mustBe "GB990091234524"
            result.address mustBe AddressLookupAddress(
              Some(List("10 Other Place", "Some District", "Anytown")),
              Some("ZZ1 1ZZ"),
              Some(AddressLookupCountry(Some("United Kingdom"), Some("GB")))
            )
          }
        }
      }
    }
  }

  override def nestedSuites: immutable.IndexedSeq[Suite] = Vector(nestedSuite)
