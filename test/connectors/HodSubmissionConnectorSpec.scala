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

import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import play.api.mvc.Results.{BadRequest, Created}
import play.api.routing.Router
import play.api.routing.sird._
import play.api.test._
import play.api.{BuiltInComponentsFromContext, Configuration}
import play.core.server.Server
import play.filters.HttpFiltersComponents
import uk.gov.hmrc.http.{BadRequestException, HttpClient}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.TestBaseSpec

class SubmissionConnectorSpec extends TestBaseSpec {

  private val config     = inject[Configuration]
  private val httpClient = inject[HttpClient]

  def withSubmissionConnector[T](block: SubmissionConnector => T): T =
    Server.withApplicationFromContext() { context =>
      new BuiltInComponentsFromContext(context) with HttpFiltersComponents {
        override def router: Router = Router.from {
          case PUT(p"/tenure-cost-and-trade-records/submissions/connected/99996010004") =>
            Action { req =>
              Created("")
            }
          case PUT(p"/tenure-cost-and-trade-records/submissions/connected/WRONG_ID")    =>
            Action { req =>
              BadRequest(Json.obj("statusCode" -> BAD_REQUEST, "message" -> "Wrong ID"))
            }
        }
      }.application
    } { implicit port =>
      WsTestClient.withClient { wsClient =>
        block(
          new HodSubmissionConnector(
            new ServicesConfig(
              Configuration("microservice.services.tenure-cost-and-trade-records.port" -> port.value)
                .withFallback(config)
            ),
            httpClient
          )
        )
      }
    }

  "SubmissionConnector" should {
    "submit ConnectedSubmission" in {
      withSubmissionConnector { submissionConnector =>
        await(submissionConnector.submitConnected("99996010004", connectedSubmission))
      }
    }

    "return BadRequestException on save with wrong id" in {
      withSubmissionConnector { submissionConnector =>
        intercept[BadRequestException] {
          await(submissionConnector.submitConnected("WRONG_ID", connectedSubmission))
        }
      }
    }
  }

}
