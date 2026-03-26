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

package uk.gov.hmrc.vo.unit.test.mock

import play.api.libs.json.{Json, Writes}
import play.api.test.Helpers.*
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.vo.unit.test.stub.RequestBuilderStub

import java.net.URL

/**
  * @author Yuriy Tumakha
  */
trait HttpClientMocks extends MockitoExtendedSugar:

  private def httpClientMethod(method: String)(using HeaderCarrier): (HttpClientV2, URL) => RequestBuilder =
    method match
      case GET    => _.get(_)
      case POST   => _.post(_)
      case PUT    => _.put(_)
      case PATCH  => _.patch(_)
      case DELETE => _.delete(_)

  def httpClientMock[T](method: String = GET, url: String = "any", responseBody: T, responseStatus: Int = OK)(using tjs: Writes[T]): HttpClientV2 =
    httpClientMockByRequestBuilder(method, url, RequestBuilderStub(responseStatus, Json.prettyPrint(Json.toJson[T](responseBody))))

  def httpClientFailedMock(method: String = GET, url: String = "any", returnFailure: Throwable): HttpClientV2 =
    httpClientMockByRequestBuilder(method, url, RequestBuilderStub(returnFailure = Option(returnFailure)))

  def httpClientMockByRequestBuilder(method: String = GET, url: String = "any", requestBuilderStub: RequestBuilderStub): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    val urlMatcher       = if url == "any" then any[URL] else eqTo(url"$url")
    when(
      httpClientMethod(method)(using any[HeaderCarrier]).apply(httpClientV2Mock, urlMatcher)
    ).thenReturn(requestBuilderStub)
    httpClientV2Mock
