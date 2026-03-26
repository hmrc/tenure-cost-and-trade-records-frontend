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

package uk.gov.hmrc.vo.unit.test

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContent, BodyParser, BodyParsers}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Injecting}

import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
abstract class BaseAppSpec extends BaseSpec with GuiceOneAppPerSuite with Injecting:

  val getRequest    = FakeRequest()
  val postRequest   = FakeRequest(POST, "/")
  val putRequest    = FakeRequest(PUT, "/")
  val patchRequest  = FakeRequest(PATCH, "/")
  val deleteRequest = FakeRequest(DELETE, "/")

  given ExecutionContext = inject[ExecutionContext]

  val configuration: Configuration = inject[Configuration]

  val messagesApi: MessagesApi = inject[MessagesApi]

  val bodyParser: BodyParser[AnyContent] = inject[BodyParsers.Default]
