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

package uk.gov.hmrc.vo.integration.test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

/**
  * @author Yuriy Tumakha
  */
trait HttpWireMock extends BeforeAndAfterAll with BeforeAndAfterEach:

  this: Suite =>

  val wireMockServer: WireMockServer =
    val server = WireMockServer(
      options()
        .dynamicPort()
    )
    server.start()
    println(s"WireMock port: ${server.port}")
    server

  override def afterEach(): Unit =
    wireMockServer.resetAll()
    super.afterEach()

  override def afterAll(): Unit =
    wireMockServer.stop()
    super.afterAll()
