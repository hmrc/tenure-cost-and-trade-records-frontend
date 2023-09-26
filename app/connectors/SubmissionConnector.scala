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

import javax.inject.{Inject, Singleton}
import models.submissions.{ConnectedSubmission, NotConnectedSubmission}

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpReads, HttpResponse, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class HodSubmissionConnector @Inject() (config: ServicesConfig, http: HttpClient)(implicit ec: ExecutionContext)
    extends SubmissionConnector {
  lazy val serviceUrl = config.baseUrl("tenure-cost-and-trade-records")

  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

  implicit def httpReads = new HttpReads[HttpResponse] {
    override def read(method: String, url: String, response: HttpResponse): HttpResponse =
      response.status match {
        case 400 => throw new BadRequestException(response.body)
        case 401 => throw new Upstream4xxResponse(response.body, 401, 401, response.headers)
        case 409 => throw new Upstream4xxResponse(response.body, 409, 409, response.headers)
        case _   => HttpReads.Implicits.readRaw.read(method, url, response)
      }
  }

  override def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http.PUT(s"${url(s"submissions/notConnected/$refNumber")}", submission).map(_ => ())

  override def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http.PUT(s"${url(s"submissions/connected/$refNumber")}", submission).map(_ => ())
}

@ImplementedBy(classOf[HodSubmissionConnector])
trait SubmissionConnector {
  def submitNotConnected(refNumber: String, submission: NotConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit]

  def submitConnected(refNumber: String, submission: ConnectedSubmission)(implicit
    hc: HeaderCarrier
  ): Future[Unit]
}
