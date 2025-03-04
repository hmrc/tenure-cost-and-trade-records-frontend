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

package config

import models.Done
import play.api.http.Status.{CREATED, OK}
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.HeaderNames.authorisation
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

abstract class InternalAuthTokenInitialiser {
  val initialised: Future[Done]
}

@Singleton
class NoOpInternalAuthTokenInitialiser @Inject() () extends InternalAuthTokenInitialiser {
  override val initialised: Future[Done] = Future.successful(Done)
}

@Singleton
class InternalAuthTokenInitialiserImpl @Inject() (
  configuration: Configuration,
  httpClientV2: HttpClientV2
)(implicit ec: ExecutionContext)
    extends InternalAuthTokenInitialiser
    with Logging {

  private val internalAuthService: Service = configuration.get[Service]("microservice.services.internal-auth")
  private val internalAuthToken            = configuration.get[String]("internalAuthToken")
  private val appName                      = configuration.get[String]("appName")
  private val tokenURL                     = url"${internalAuthService.baseUrl}/test-only/token"
  private val emptyHeaderCarrier           = HeaderCarrier()

  override val initialised: Future[Done] = ensureAuthToken()

  Await.result(initialised, 30.seconds)

  private def ensureAuthToken(): Future[Done] =
    authTokenIsValid().flatMap { isValid =>
      if (isValid) {
        logger.info("Auth token is already valid")
        Future.successful(Done)
      } else {
        createClientAuthToken()
      }
    }

  private def createClientAuthToken(): Future[Done] = {
    logger.info("Initialising auth token")

    httpClientV2
      .post(tokenURL)(emptyHeaderCarrier)
      .withBody(
        Json.obj(
          "token"       -> internalAuthToken,
          "principal"   -> appName,
          "permissions" -> Seq(
            Json.obj(
              "resourceType"     -> "tenure-cost-and-trade-records",
              "resourceLocation" -> "*",
              "actions"          -> List("*")
            )
          )
        )
      )
      .execute[HttpResponse]
      .flatMap { response =>
        if (response.status == CREATED) {
          logger.info("Auth token initialised")
          Future.successful(Done)
        } else {
          Future.failed(new RuntimeException("Unable to initialise internal-auth token"))
        }
      }
  }

  private def authTokenIsValid(): Future[Boolean] =
    httpClientV2
      .get(tokenURL)(emptyHeaderCarrier)
      .setHeader(authorisation -> internalAuthToken)
      .execute[HttpResponse]
      .map(_.status == OK)

}
