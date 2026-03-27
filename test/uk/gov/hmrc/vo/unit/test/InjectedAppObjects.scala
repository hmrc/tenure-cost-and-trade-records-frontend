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

import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContent, BodyParser, BodyParsers}
import play.api.test.{HasApp, Injecting}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
trait InjectedAppObjects extends Injecting:

  self: HasApp =>

  given hc: HeaderCarrier    = HeaderCarrier()
  given ec: ExecutionContext = inject[ExecutionContext]
  given wsClient: WSClient   = inject[WSClient]

  val configuration: Configuration   = inject[Configuration]
  val servicesConfig: ServicesConfig = inject[ServicesConfig]

  val messagesApi: MessagesApi = inject[MessagesApi]

  val bodyParser: BodyParser[AnyContent] = inject[BodyParsers.Default]
