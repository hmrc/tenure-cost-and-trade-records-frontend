/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class AppConfig @Inject() (config: Configuration) {

  lazy val useDummyIp        = getBoolean("useDummyTrueIP")
  lazy val startPageRedirect = getBoolean("startPageRedirect")
  lazy val govukStartPage    = getString("govukStartPage")

  lazy val cookiesUrl: String            = "https://www.tax.service.gov.uk/help/cookies"
  lazy val privacyNoticeUrl: String      = "https://www.tax.service.gov.uk/help/privacy"
  lazy val termsAndConditionsUrl: String = "https://www.tax.service.gov.uk/help/terms-and-conditions"
  lazy val helpUsingGovUkUrl: String     = "https://www.gov.uk/help"
  lazy val contactGovUkUrl: String       = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact"
  lazy val welshHelpUrl: String          = "https://www.gov.uk/cymraeg"
  lazy val internalAuthToken: String     = getString("internalAuthToken")

  private def getString(key: String): String   =
    config.getOptional[String](key).getOrElse(throw ConfigSettingMissing(key))
  private def getBoolean(key: String): Boolean =
    config.getOptional[Boolean](key).getOrElse(throw ConfigSettingMissing(key))

}

case class ConfigSettingMissing(key: String) extends Exception(key)
