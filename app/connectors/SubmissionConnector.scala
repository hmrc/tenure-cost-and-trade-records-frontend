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

import com.google.inject.ImplementedBy
import config.AppConfig

import javax.inject.{Inject, Singleton}
import models.submissions.{ConnectedSubmission, NotConnectedSubmission, RequestReferenceNumberSubmission}

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class HodSubmissionConnector @Inject() (config: ServicesConfig, appConfig: AppConfig, http: HttpClient)(implicit
  ec: ExecutionContext
) extends SubmissionConnector {

  val serviceUrl: String        = config.baseUrl("tenure-cost-and-trade-records")
  val internalAuthToken: String = appConfig.internalAuthToken
  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

  private def handleHttpResponse: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    override def read(method: String, url: String, response: HttpResponse): HttpResponse =
      response.status match {
        case 400 => throw new BadRequestException(response.body)
        case 401 => throw Upstream4xxResponse(response.body, 401, 401, response.headers)
        case 409 => throw Upstream4xxResponse(response.body, 409, 409, response.headers)
        case _   => HttpReads.Implicits.readRaw.read(method, url, response)
      }
  }

  private def cleanedRefNumber(refNumber: String) = refNumber.replaceAll("[^0-9]", "")

  override def submitRequestReferenceNumber(submission: RequestReferenceNumberSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http
      .PUT[RequestReferenceNumberSubmission, HttpResponse](
        url(s"submissions/requestRefNum}"),
        submission,
        Seq("Authorization" -> internalAuthToken)
      )
      .flatMap { response =>
        response.status match {
          case 201 => Future.successful(())
          case 400 => Future.failed(new BadRequestException(response.body))
          case _   => Future.failed(new Exception(s"Unexpected response: ${response.status}"))
        }
      }

  override def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http
      .PUT[NotConnectedSubmission, HttpResponse](
        url(s"submissions/notConnected/${cleanedRefNumber(refNumber)}"),
        submission,
        Seq("Authorization" -> internalAuthToken)
      )
      .flatMap { response =>
        response.status match {
          case 201 => Future.successful(())
          case 400 => Future.failed(new BadRequestException(response.body))
          case _   => Future.failed(new Exception(s"Unexpected response: ${response.status}"))
        }
      }

  override def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http
      .PUT[ConnectedSubmission, HttpResponse](
        url(s"submissions/connected/${cleanedRefNumber(refNumber)}"),
        submission,
        Seq("Authorization" -> internalAuthToken)
      )
      .flatMap { response =>
        response.status match {
          case 201 => Future.successful(())
          case 400 => Future.failed(new BadRequestException(response.body))
          // Handle other cases if necessary
          case _   => Future.failed(new Exception(s"Unexpected response: ${response.status}"))
        }
      }
}

@ImplementedBy(classOf[HodSubmissionConnector])
trait SubmissionConnector {
  def submitRequestReferenceNumber(submission: RequestReferenceNumberSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit]
  def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit]
  def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit hc: HeaderCarrier): Future[Unit]
}
