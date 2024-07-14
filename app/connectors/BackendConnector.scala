/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.http.{Authorization, BadRequestException, HeaderCarrier, HttpClient, HttpReads, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultBackendConnector @Inject() (servicesConfig: ServicesConfig, appConfig: AppConfig, http: HttpClient)(
  implicit ec: ExecutionContext
) extends BackendConnector {

  private val serviceUrl         = servicesConfig.baseUrl("tenure-cost-and-trade-records")
  private val backendBaseUrl     = s"$serviceUrl/tenure-cost-and-trade-records"
  private val saveAsDraftBaseUrl = s"$backendBaseUrl/saveAsDraft"
  private val internalAuthToken  = appConfig.internalAuthToken

  private def cleanedRefNumber(refNumber: String) = refNumber.replaceAll("[^0-9]", "")

  private def saveAsDraftUrl(referenceNumber: String) = s"$saveAsDraftBaseUrl/${cleanedRefNumber(referenceNumber)}"

  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

  private def readsHack(implicit httpReads: HttpReads[FORLoginResponse]): HttpReads[FORLoginResponse] =
    (method: String, url: String, response: HttpResponse) =>
      response.status match {
        case 400 => throw new BadRequestException(response.body)
        case 401 => throw UpstreamErrorResponse(response.body, 401, 401)
        case _   => httpReads.read(method, url, response)
      }

  override def verifyCredentials(refNumber: String, postcode: String)(implicit
    hc: HeaderCarrier
  ): Future[FORLoginResponse] = {
    val credentials    = Credentials(cleanedRefNumber(refNumber), postcode)
    val wrtCredentials = implicitly[Writes[Credentials]]

    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = Some(Authorization(internalAuthToken)))

    http.POST[Credentials, FORLoginResponse](
      url("authenticate"),
      credentials,
      Seq.empty
    )(wrtCredentials, readsHack, headerCarrier, ec)
  }

  override def retrieveFORType(referenceNumber: String, hc: HeaderCarrier): Future[String] = {
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = Some(Authorization(internalAuthToken)))

    http
      .GET(url(s"${cleanedRefNumber(referenceNumber)}/forType"))(HttpReads.Implicits.readRaw, headerCarrier, ec)
      .map(res => (res.json \ "FORType").as[String])
  }

  override def saveAsDraft(
    referenceNumber: String,
    submissionDraft: SubmissionDraft,
    hc: HeaderCarrier
  ): Future[Unit] = {
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = Some(Authorization(internalAuthToken)))

    http.PUT(
      saveAsDraftUrl(referenceNumber),
      submissionDraft
    ) map { _ =>
      ()
    }
  }

  override def loadSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Option[SubmissionDraft]] = {
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = Some(Authorization(internalAuthToken)))

    http.GET[Option[SubmissionDraft]](
      saveAsDraftUrl(referenceNumber)
    )(HttpReads.Implicits.readOptionOfNotFound[SubmissionDraft], headerCarrier, ec)
  }

  override def deleteSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Int] = {
    implicit val headerCarrier: HeaderCarrier = hc.copy(authorization = Some(Authorization(internalAuthToken)))

    http
      .DELETE(saveAsDraftUrl(referenceNumber))
      .map(res => (res.json \ "deletedCount").as[Int])
  }
}

@ImplementedBy(classOf[DefaultBackendConnector])
trait BackendConnector {
  def verifyCredentials(refNumber: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]

  def retrieveFORType(referenceNumber: String, hc: HeaderCarrier): Future[String]

  def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft, hc: HeaderCarrier): Future[Unit]

  def loadSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Option[SubmissionDraft]]

  def deleteSubmissionDraft(referenceNumber: String, hc: HeaderCarrier): Future[Int]
}
