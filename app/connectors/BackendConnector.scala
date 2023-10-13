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
import models.{Credentials, FORLoginResponse, SubmissionDraft}
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpReads, HttpResponse, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultBackendConnector @Inject() (servicesConfig: ServicesConfig, appConfig: AppConfig, http: HttpClient)(
  implicit ec: ExecutionContext
) extends BackendConnector {

  private val serviceUrl                              = servicesConfig.baseUrl("tenure-cost-and-trade-records")
  private val backendBaseUrl                          = s"$serviceUrl/tenure-cost-and-trade-records"
  private val saveAsDraftBaseUrl                      = s"$backendBaseUrl/saveAsDraft"
  private val internalAuthToken                       = appConfig.internalAuthToken
  private def saveAsDraftUrl(referenceNumber: String) = s"$saveAsDraftBaseUrl/$referenceNumber"

  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

  def readsHack(implicit httpReads: HttpReads[FORLoginResponse]) =
    new HttpReads[FORLoginResponse] {
      override def read(method: String, url: String, response: HttpResponse): FORLoginResponse =
        response.status match {
          case 400 => throw new BadRequestException(response.body)
          case 401 => throw new Upstream4xxResponse(response.body, 401, 401, response.allHeaders)
          case _   => httpReads.read(method, url, response)
        }
    }

  override def verifyCredentials(refNumber: String, postcode: String)(implicit
    hc: HeaderCarrier
  ): Future[FORLoginResponse] = {
    val credentials    = Credentials(refNumber, postcode)
    val wrtCredentials = implicitly[Writes[Credentials]]
    http.POST[Credentials, FORLoginResponse](
      url("authenticate"),
      credentials,
      Seq("Authorization" -> internalAuthToken)
    )(wrtCredentials, readsHack, hc, ec)
  }

  override def retrieveFORType(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[String] =
    http
      .GET(url(s"$referenceNumber/forType"), headers = Seq("Authorization" -> internalAuthToken))
      .map(res => (res.json \ "FORType").as[String])

  override def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http.PUT(saveAsDraftUrl(referenceNumber), submissionDraft, headers = Seq("Authorization" -> internalAuthToken)) map { _ =>
      ()
    }

  override def loadSubmissionDraft(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[SubmissionDraft]] =
    http.GET[Option[SubmissionDraft]](saveAsDraftUrl(referenceNumber),headers = Seq("Authorization" -> internalAuthToken))

  override def deleteSubmissionDraft(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[Int]                     =
    http
      .DELETE(saveAsDraftUrl(referenceNumber), headers = Seq("Authorization" -> internalAuthToken))
      .map(res => (res.json \ "deletedCount").as[Int])

}

@ImplementedBy(classOf[DefaultBackendConnector])
trait BackendConnector {
  def verifyCredentials(refNumber: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]

  def retrieveFORType(referenceNumber: String)(implicit hc: HeaderCarrier): Future[String]

  def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft)(implicit hc: HeaderCarrier): Future[Unit]

  def loadSubmissionDraft(referenceNumber: String)(implicit hc: HeaderCarrier): Future[Option[SubmissionDraft]]

  def deleteSubmissionDraft(referenceNumber: String)(implicit hc: HeaderCarrier): Future[Int]
}
