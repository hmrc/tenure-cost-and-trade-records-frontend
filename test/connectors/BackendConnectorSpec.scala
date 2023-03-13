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
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import play.api.mvc.Results.{BadRequest, Created, InternalServerError, NotFound, Ok}
import play.api.routing.Router
import play.api.routing.sird._
import play.api.test._
import play.api.{BuiltInComponentsFromContext, Configuration}
import play.core.server.Server
import play.filters.HttpFiltersComponents
import uk.gov.hmrc.http.{BadRequestException, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class BackendConnectorSpec extends TestBaseSpec with Injecting {

  private val config      = inject[Configuration]
  private val appConfig   = inject[AppConfig]
  private val actorSystem = inject[ActorSystem]

  def withBackendConnector[T](block: BackendConnector => T): T =
    Server.withApplicationFromContext() { context =>
      new BuiltInComponentsFromContext(context) with HttpFiltersComponents {
        override def router: Router = Router.from {
          case GET(p"/tenure-cost-and-trade-records/saveAsDraft/$refNum")        =>
            Action { req =>
              refNum match {
                case "99996010004"         => Ok(Json.toJson(submissionDraft))
                case "InternalServerError" => InternalServerError("")
                case _                     => NotFound(Json.obj("status" -> "NotFound"))
              }
            }
          case PUT(p"/tenure-cost-and-trade-records/saveAsDraft/99996010004")    =>
            Action { req =>
              Created("")
            }
          case PUT(p"/tenure-cost-and-trade-records/saveAsDraft/WRONG_ID")       =>
            Action { req =>
              BadRequest(Json.obj("statusCode" -> BAD_REQUEST, "message" -> "Wrong ID"))
            }
          case DELETE(p"/tenure-cost-and-trade-records/saveAsDraft/99996010004") =>
            Action { req =>
              Ok(Json.obj("deletedCount" -> 1))
            }
        }
      }.application
    } { implicit port =>
      WsTestClient.withClient { wsClient =>
        block(
          new DefaultBackendConnector(
            new ServicesConfig(
              Configuration("microservice.services.tenure-cost-and-trade-records.port" -> port.value)
                .withFallback(config)
            ),
            new ForHttpClient(config, appConfig, actorSystem, wsClient)
          )
        )
      }
    }

  "BackendConnector" should {
    "save SubmissionDraft" in {
      withBackendConnector { backendConnector =>
        await(backendConnector.saveAsDraft("99996010004", submissionDraft))
      }
    }

    "return BadRequestException on save with wrong id" in {
      withBackendConnector { backendConnector =>
        intercept[BadRequestException] {
          await(backendConnector.saveAsDraft("WRONG_ID", submissionDraft))
        }
      }
    }

    "load SubmissionDraft" in {
      withBackendConnector { backendConnector =>
        val res = await(backendConnector.loadSubmissionDraft("99996010004"))
        res shouldBe Some(submissionDraft)
      }
    }

    "return None on load SubmissionDraft by unknown id" in {
      withBackendConnector { backendConnector =>
        val res = await(backendConnector.loadSubmissionDraft("UNKNOWN_ID"))
        res shouldBe None
      }
    }

    "throw UpstreamErrorResponse when get InternalServerError on load SubmissionDraft" in {
      withBackendConnector { backendConnector =>
        intercept[UpstreamErrorResponse] {
          await(backendConnector.loadSubmissionDraft("InternalServerError"))
        }
      }
    }

    "delete SubmissionDraft" in {
      withBackendConnector { backendConnector =>
        await(backendConnector.deleteSubmissionDraft("99996010004")) shouldBe 1
      }
    }
  }

}
