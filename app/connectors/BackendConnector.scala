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

import com.google.inject.ImplementedBy
import config.AppConfig
import models.{Credentials, FORLoginResponse, SubmissionDraft}
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, BadRequestException, HeaderCarrier, HttpReads, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultBackendConnector @Inject() (
  servicesConfig: ServicesConfig,
  appConfig: AppConfig,
  httpClientV2: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends BackendConnector:

  private val serviceUrl        = servicesConfig.baseUrl("tenure-cost-and-trade-records")
  private val backendBaseUrl    = s"$serviceUrl/tenure-cost-and-trade-records"
  private val internalAuthToken = Some(Authorization(appConfig.internalAuthToken))

  private def cleanedRefNumber(refNumber: String) = refNumber.replaceAll("[^0-9]", "")

  private val authenticateURL: URL = url"$backendBaseUrl/authenticate"

  private def getFORTypeURL(refNumber: String): URL = url"$backendBaseUrl/${cleanedRefNumber(refNumber)}/forType"

  private def saveAsDraftURL(refNumber: String): URL = url"$backendBaseUrl/saveAsDraft/${cleanedRefNumber(refNumber)}"

  override def verifyCredentials(refNumber: String, postcode: String)(implicit
    hc: HeaderCarrier
  ): Future[FORLoginResponse] =
    val credentials = Credentials(cleanedRefNumber(refNumber), postcode)

    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .post(authenticateURL)(headerCarrier)
      .withBody(Json.toJson(credentials))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case 200    => Future.successful(Json.parse(response.body).as[FORLoginResponse])
          case 400    => Future.failed(new BadRequestException(response.body))
          case status => Future.failed(throw UpstreamErrorResponse(response.body, status, status))
        }
      }

  override def retrieveFORType(referenceNumber: String, hc: HeaderCarrier): Future[String] =
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .get(getFORTypeURL(referenceNumber))(headerCarrier)
      .execute[HttpResponse]
      .map(res => (res.json \ "FORType").as[String])

  override def saveAsDraft(
    referenceNumber: String,
    submissionDraft: SubmissionDraft,
    hc: HeaderCarrier
  ): Future[Unit] =
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .put(saveAsDraftURL(referenceNumber))(headerCarrier)
      .withBody(Json.toJson(submissionDraft))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case 201    => Future.unit
          case 400    => Future.failed(new BadRequestException(response.body))
          case status => Future.failed(throw UpstreamErrorResponse(response.body, status, status))
        }
      }

  override def loadSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Option[SubmissionDraft]] =
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .get(saveAsDraftURL(referenceNumber))(headerCarrier)
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case 200    => Future.successful(Json.parse(response.body).asOpt[SubmissionDraft])
          case 404    => Future.successful(None)
          case status => Future.failed(throw UpstreamErrorResponse(response.body, status, status))
        }
      }

  override def deleteSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Int] =
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .delete(saveAsDraftURL(referenceNumber))(headerCarrier)
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case 200    => Future.successful((response.json \ "deletedCount").as[Int])
          case status => Future.failed(throw UpstreamErrorResponse(response.body, status, status))
        }
      }

@ImplementedBy(classOf[DefaultBackendConnector])
trait BackendConnector:

  def verifyCredentials(refNumber: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]

  def retrieveFORType(referenceNumber: String, hc: HeaderCarrier): Future[String]

  def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft, hc: HeaderCarrier): Future[Unit]

  def loadSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Option[SubmissionDraft]]

  def deleteSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Int]
