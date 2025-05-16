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
import models.submissions.{ConnectedSubmission, NotConnectedSubmission, RequestReferenceNumberSubmission}
import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, BadRequestException, HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HodSubmissionConnector @Inject() (config: ServicesConfig, appConfig: AppConfig, httpClientV2: HttpClientV2)(
  implicit ec: ExecutionContext
) extends SubmissionConnector:

  private val serviceUrl        = config.baseUrl("tenure-cost-and-trade-records")
  private val internalAuthToken = Some(Authorization(appConfig.internalAuthToken))

  private def cleanedRefNumber(refNumber: String) = refNumber.replaceAll("[^0-9]", "")

  private def connectedSubmissionURL(refNumber: String): URL =
    url"$serviceUrl/tenure-cost-and-trade-records/submissions/connected/${cleanedRefNumber(refNumber)}"

  private def notConnectedSubmissionURL(refNumber: String): URL =
    url"$serviceUrl/tenure-cost-and-trade-records/submissions/notConnected/${cleanedRefNumber(refNumber)}"

  private val requestRefNumURL: URL = url"$serviceUrl/tenure-cost-and-trade-records/submissions/requestRefNum"

  override def submitRequestReferenceNumber(submission: RequestReferenceNumberSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    sendSubmission(requestRefNumURL, submission)

  override def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    sendSubmission(notConnectedSubmissionURL(refNumber), submission)

  override def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    sendSubmission(connectedSubmissionURL(refNumber), submission)

  private def sendSubmission[T](url: URL, submission: T)(implicit
    tjs: Writes[T],
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = internalAuthToken)

    httpClientV2
      .put(url)(using headerCarrier)
      .withBody(Json.toJson(submission))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case 201    => Future.successful(response)
          case 400    => Future.failed(new BadRequestException(response.body))
          case status => Future.failed(new Exception(s"Unexpected response: $status"))
        }
      }

@ImplementedBy(classOf[HodSubmissionConnector])
trait SubmissionConnector:
  def submitRequestReferenceNumber(submission: RequestReferenceNumberSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse]

  def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse]

  def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse]
