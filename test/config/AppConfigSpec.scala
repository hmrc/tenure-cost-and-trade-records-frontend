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
import uk.gov.hmrc.vo.unit.test.BaseSpec

class AppConfigSpec extends BaseSpec:

  private val defaultConfig = Configuration(ConfigFactory.load())

  "AppConfig" should {

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

    "throw ConfigSettingMissing exception if config is empty" in {
      a[ConfigSettingMissing] should be thrownBy AppConfig(Configuration.empty)
    }

    "return the correct default URLs" in {
      val appConfig = AppConfig(defaultConfig)

      appConfig.cookiesUrl            shouldBe "https://www.tax.service.gov.uk/help/cookies"
      appConfig.privacyNoticeUrl      shouldBe "https://www.tax.service.gov.uk/help/privacy"
      appConfig.termsAndConditionsUrl shouldBe "https://www.tax.service.gov.uk/help/terms-and-conditions"
      appConfig.helpUsingGovUkUrl     shouldBe "https://www.gov.uk/help"
      appConfig.contactGovUkUrl       shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact"
      appConfig.welshHelpUrl          shouldBe "https://www.gov.uk/cymraeg"
    }

  }
