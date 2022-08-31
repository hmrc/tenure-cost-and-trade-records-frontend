/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.voafor

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient

class HealthEndpointIntegrationSpec
  extends AnyWordSpec
     with Matchers
     with ScalaFutures
     with IntegrationPatience
     with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl  = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .build()

  "service health endpoint" should {
    "respond with 200 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/ping/ping")
          .get()
          .futureValue

      response.status shouldBe 200
    }
  }
}
