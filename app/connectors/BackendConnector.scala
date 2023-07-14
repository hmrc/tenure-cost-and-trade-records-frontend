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
import models.{FORLoginResponse, SubmissionDraft}
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpReads, HttpResponse, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import views.html.helper.urlEncode

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultBackendConnector @Inject() (servicesConfig: ServicesConfig, http: ForHttp)(implicit ec: ExecutionContext)
    extends BackendConnector {

  private val serviceUrl         = servicesConfig.baseUrl("tenure-cost-and-trade-records")
  private val backendBaseUrl     = s"$serviceUrl/tenure-cost-and-trade-records"
  private val saveAsDraftBaseUrl = s"$backendBaseUrl/saveAsDraft"

  private def saveAsDraftUrl(referenceNumber: String) = s"$saveAsDraftBaseUrl/$referenceNumber"

  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

//  val backend = HttpClientSyncBackend()
//
//  def testConnection(referenceNumber: String, postcode: String): String = {
//
//    val parts = Seq(referenceNumber, postcode).map(urlEncode)
//
//    val request = basicRequest.get(uri"${url(s"${parts.mkString("/")}/verify")}")
//
//    val response = request.send(backend)
//
//    var name = ""
//    response.body match {
//      case Left(f) => name = "Anonymous"
//      case Right(n) => name = n
//    }
//    logger.debug(s"Connecting with: ${url(s"${parts.mkString("/")}/test")}, response: ${name}")
//    if (name.startsWith("\"")) name = name.substring(1, name.length -1)
//    name
//  }

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
    val parts = Seq(refNumber, postcode).map(urlEncode)
    http.GET[FORLoginResponse](url(s"${parts.mkString("/")}/verify"))(readsHack, hc, ec)
  }

  override def retrieveFORType(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[String] =
    http.GET(url(s"$referenceNumber/forType")).map(res => (res.json \ "FORType").as[String])

  override def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    http.PUT(saveAsDraftUrl(referenceNumber), submissionDraft) map { _ => () }

  override def loadSubmissionDraft(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[SubmissionDraft]] =
    http.GET[Option[SubmissionDraft]](saveAsDraftUrl(referenceNumber))

  override def deleteSubmissionDraft(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[Int] =
    http.DELETE(saveAsDraftUrl(referenceNumber)).map(res => (res.json \ "deletedCount").as[Int])

}

@ImplementedBy(classOf[DefaultBackendConnector])
trait BackendConnector {
  def verifyCredentials(refNumber: String, postcode: String)(implicit hc: HeaderCarrier): Future[FORLoginResponse]

  def retrieveFORType(refNumber: String)(implicit hc: HeaderCarrier): Future[String]

  def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft)(implicit hc: HeaderCarrier): Future[Unit]

  def loadSubmissionDraft(referenceNumber: String)(implicit hc: HeaderCarrier): Future[Option[SubmissionDraft]]

  def deleteSubmissionDraft(referenceNumber: String)(implicit hc: HeaderCarrier): Future[Int]
}
