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

import akka.actor.ActorSystem
import config.AppConfig
import play.api.libs.json.Json
import play.api.{BuiltInComponentsFromContext, Configuration}
import play.api.mvc.Results.Ok
import play.api.routing.Router
import utils.TestBaseSpec
import play.core.server.Server
import play.filters.HttpFiltersComponents
import play.api.routing.sird._
import play.api.test.WsTestClient
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
class UpscanConnectorSpec extends TestBaseSpec {

  private val config    = inject[Configuration]
  private val appConfig = inject[AppConfig]
  private val actorSystem = inject[ActorSystem]
  private val httpClient = app.injector.instanceOf[HttpClient]
  def withUpscanConnector[T](block: UpscanConnector => T): T =
    Server.withApplicationFromContext() { context =>
      new BuiltInComponentsFromContext(context) with HttpFiltersComponents {
        override def router: Router = Router.from {
          case POST(p"/upscan/v2/initiate") => Action { req =>
            Ok(Json.obj(
              "reference" -> "someRef",
              "uploadRequest" -> Json.obj(
                "href" -> "someUrl",
                "fields" -> Json.obj()
              )
            ))
          }
        }
      }.application
    } { implicit port =>
      WsTestClient.withClient { wsClient =>
        block(
          new UpscanConnector(
            httpClient,
            Configuration("microservice.services.upscan.port" -> port.value)
              .withFallback(config),
            new ServicesConfig(Configuration("microservice.services.upscan.port" -> port.value)
              .withFallback(config))
          )
        )
      }
    }

  "UpscanConnector" should {
    "initiate upscan successfully" in {
      withUpscanConnector { upscanConnector =>
        val response = await(upscanConnector.initiate())
        response.reference shouldBe "someRef"
      }
    }
  }

}
