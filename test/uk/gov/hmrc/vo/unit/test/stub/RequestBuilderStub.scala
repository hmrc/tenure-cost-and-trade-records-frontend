/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.unit.test.stub

import izumi.reflect.Tag
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSRequest}
import uk.gov.hmrc.http.client.{RequestBuilder, StreamHttpReads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
case class RequestBuilderStub(
  /**
    * The HTTP response status code to be returned upon execution.
    */
  responseStatus: Int = 200,
  /**
    * The HTTP response body to be returned upon execution.
    */
  responseBody: String = "{}",
  /**
    * When specified, this value causes a failure to be returned, overriding both responseStatus and responseBody.
    */
  returnFailure: Option[Throwable] = None
) extends RequestBuilder:

  /**
    * Represents the headers received as part of the incoming request.
    */
  var requestHeaders: Map[String, String] = Map.empty

  /**
    * Represents the body content received as part of the incoming request.
    */
  var requestBody: String = ""

  override def transform(transform: WSRequest => WSRequest): RequestBuilder = this

  override def execute[A: HttpReads](using ec: ExecutionContext): Future[A] =
    returnFailure.fold(Future.successful(HttpResponse(responseStatus, responseBody).asInstanceOf[A]))(Future.failed)

  override def stream[A: StreamHttpReads](using ec: ExecutionContext): Future[A] = execute[A]

  override def setHeader(headers: (String, String)*): RequestBuilder =
    requestHeaders ++= headers.toMap
    this

  override def withProxy: RequestBuilder = this

  override def withBody[B: {BodyWritable, Tag}](body: B)(using ec: ExecutionContext): RequestBuilder =
    val bodyString = body match
      case json: JsValue => Json.stringify(json)
      case b             => b.toString
    requestBody = bodyString
    this
