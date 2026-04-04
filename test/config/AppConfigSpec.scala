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

package config

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import test.TCTRAppSpec

class AppConfigSpec extends TCTRAppSpec:

  private val defaultConfig = Configuration(ConfigFactory.load())
  private val appConfig     = inject[AppConfig]

  "AppConfig" should {
    "return feedbackFrontendUrl" in {
      appConfig.feedbackPage.url shouldBe "/send-trade-and-cost-information/feedback"
      appConfig.feedbackPage.url shouldBe controllers.routes.FeedbackController.feedback.url
    }

    "return the correct boolean value for useDummyIp = true" in {
      val configuration = Configuration("useDummyTrueIP" -> true).withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.useDummyIp shouldBe true
    }

    "return the correct boolean value for useDummyIp = false" in {
      val configuration = Configuration("useDummyTrueIP" -> false).withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.useDummyIp shouldBe false
    }

    "return the correct boolean value for startPageRedirect" in {
      val configuration = Configuration("startPageRedirect" -> false).withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.startPageRedirect shouldBe false
    }

    "return the correct string value for govukStartPage" in {
      val configuration = Configuration("govukStartPage" -> "https://example.com").withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.govukStartPage shouldBe "https://example.com"
    }

    "return the correct value for internalAuthToken" in {
      val configuration = Configuration("internalAuthToken" -> "token").withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.internalAuthToken shouldBe "token"
    }

    "return the correct value for tctrFrontendUrl" in {
      val configuration = Configuration("urls.tctrFrontend" -> "https://frontend.com").withFallback(defaultConfig)
      val appConfig     = AppConfig(configuration)
      appConfig.tctrFrontendUrl shouldBe "https://frontend.com"
    }

  }
