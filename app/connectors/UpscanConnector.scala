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

import models.admin.UpScanRequests.{InitiateRequest, InitiateResponse}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpscanConnector @Inject() (http: HttpClient, val configuration: Configuration, val servicesConfig: ServicesConfig)
    extends Logging {

  val upscanUrl = servicesConfig.baseUrl("upscan")

  val initiateUrl = s"$upscanUrl/upscan/v2/initiate"

  val callBackUrl     = configuration.get[String]("microservice.services.upscan.callback-url")
  val maxFileSize     = configuration.get[Int]("microservice.services.upscan.max-file-size")
  val successRedirect =
    s"http://localhost:9526${configuration.get[String]("app.rootContext")}${controllers.admin.routes.FileUploadController.showResult.url}"

  def initiate()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[InitiateResponse] = {
    val request = InitiateRequest(callbackUrl = callBackUrl, Some(successRedirect), maxFileSize = maxFileSize)
    http
      .POST[InitiateRequest, InitiateResponse](initiateUrl, request)
      .transform { initiateResponse =>
        logger.debug(s"Upscan initiate response : $initiateResponse")
        initiateResponse
      }
  }
}
