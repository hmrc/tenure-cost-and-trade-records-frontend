/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.Logging
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import views.html.helper.urlEncode
import sttp.client3._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BackendConnector@Inject()(config: ServicesConfig) extends Logging {

  lazy val serviceUrl = config.baseUrl("tenure-cost-and-trade-records")
  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records/$path"

  val backend = HttpClientSyncBackend()

  def testConnection(referenceNumber: String, postcode: String): String = {

    val parts = Seq(referenceNumber, postcode).map(urlEncode)

    val request = basicRequest.get(uri"${url(s"${parts.mkString("/")}/verify")}")

    val response = request.send(backend)

    var name = ""
    response.body match {
      case Left(f) => name = "Anonymous"
      case Right(n) => name = n
    }
    logger.debug(s"Connecting with: ${url(s"${parts.mkString("/")}/test")}, response: ${name}")
    if (name.startsWith("\"")) name = name.substring(1, name.length -1)
    name
  }

  def verifyCredentials(refNum: String, postcode: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] = {
    Future.successful("Anonymous")
  }
}
