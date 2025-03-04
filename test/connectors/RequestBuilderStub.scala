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

import izumi.reflect.Tag
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSRequest}
import uk.gov.hmrc.http.client.{RequestBuilder, StreamHttpReads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
case class RequestBuilderStub(responseStatusOrFailure: Either[Throwable, Int], requestBody: String = "")
    extends RequestBuilder:

  override def transform(transform: WSRequest => WSRequest): RequestBuilder = this

  override def execute[A: HttpReads](implicit ec: ExecutionContext): Future[A] =
    responseStatusOrFailure match
      case Right(responseStatus) => Future.successful(HttpResponse(responseStatus, requestBody).asInstanceOf[A])
      case Left(failure)         => Future.failed(failure)

  override def stream[A: StreamHttpReads](implicit ec: ExecutionContext): Future[A] = ???

  override def setHeader(header: (String, String)*): RequestBuilder = this

  override def withProxy: RequestBuilder = this

  override def withBody[B: BodyWritable: Tag](body: B)(implicit ec: ExecutionContext): RequestBuilder =
    val bodyString = body match {
      case json: JsValue => Json.stringify(json)
      case b             => b.toString
    }
    this.copy(requestBody = bodyString)
