/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatestplus.play._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import play.api.Configuration

class AppConfigSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "AppConfig" should {

    "return the correct boolean value for useDummyIp" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[Boolean]("useDummyTrueIP")).thenReturn(Some(true))

      val appConfig = new AppConfig(mockConfig)
      appConfig.useDummyIp shouldBe true
    }

    "return the correct boolean value for startPageRedirect" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[Boolean]("startPageRedirect")).thenReturn(Some(false))

      val appConfig = new AppConfig(mockConfig)
      appConfig.startPageRedirect shouldBe false
    }

    "return the correct string value for govukStartPage" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[String]("govukStartPage")).thenReturn(Some("http://example.com"))

      val appConfig = new AppConfig(mockConfig)
      appConfig.govukStartPage shouldBe "http://example.com"
    }

    "return the correct value for internalAuthToken" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[String]("internalAuthToken")).thenReturn(Some("token"))

      val appConfig = new AppConfig(mockConfig)
      appConfig.internalAuthToken shouldBe "token"
    }

    "return the correct value for tctrFrontendUrl" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[String]("urls.tctrFrontend")).thenReturn(Some("http://frontend.com"))

      val appConfig = new AppConfig(mockConfig)
      appConfig.tctrFrontendUrl shouldBe "http://frontend.com"
    }

    "throw ConfigSettingMissing exception if boolean config is missing" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[Boolean]("useDummyTrueIP")).thenReturn(None)

      val appConfig = new AppConfig(mockConfig)
      a[ConfigSettingMissing] should be thrownBy {
        appConfig.useDummyIp
      }
    }

    "throw ConfigSettingMissing exception if string config is missing" in {
      val mockConfig = mock[Configuration]
      when(mockConfig.getOptional[String]("govukStartPage")).thenReturn(None)

      val appConfig = new AppConfig(mockConfig)
      a[ConfigSettingMissing] should be thrownBy {
        appConfig.govukStartPage
      }
    }

    "return the correct default URLs" in {
      val mockConfig = mock[Configuration]
      val appConfig  = new AppConfig(mockConfig)

      appConfig.cookiesUrl            shouldBe "https://www.tax.service.gov.uk/help/cookies"
      appConfig.privacyNoticeUrl      shouldBe "https://www.tax.service.gov.uk/help/privacy"
      appConfig.termsAndConditionsUrl shouldBe "https://www.tax.service.gov.uk/help/terms-and-conditions"
      appConfig.helpUsingGovUkUrl     shouldBe "https://www.gov.uk/help"
      appConfig.contactGovUkUrl       shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact"
      appConfig.welshHelpUrl          shouldBe "https://www.gov.uk/cymraeg"
    }
  }
}
