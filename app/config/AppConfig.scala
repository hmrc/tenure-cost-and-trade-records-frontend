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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.Call
import uk.gov.hmrc.vo.service.config.VOServiceConfig

@Singleton
class AppConfig @Inject() (val configuration: Configuration) extends VOServiceConfig:

  override def serviceID: String                    = "TCTR"
  override def serviceRoot: Call                    = controllers.routes.Application.index
  override def isWelshTranslationAvailable: Boolean = true

  // TODO: Remove and use feedbackFrontendUrl
  def feedbackTCTRUrl: String = controllers.routes.FeedbackController.feedback.url

  val useDummyIp: Boolean        = getBoolean("useDummyTrueIP")
  val startPageRedirect: Boolean = getBoolean("startPageRedirect")
  val govukStartPage: String     = getString("govukStartPage")

  val cookiesUrl: String            = "https://www.tax.service.gov.uk/help/cookies"
  val privacyNoticeUrl: String      = "https://www.tax.service.gov.uk/help/privacy"
  val termsAndConditionsUrl: String = "https://www.tax.service.gov.uk/help/terms-and-conditions"
  val helpUsingGovUkUrl: String     = "https://www.gov.uk/help"
  val contactGovUkUrl: String       = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact"
  val welshHelpUrl: String          = "https://www.gov.uk/cymraeg"
  val internalAuthToken: String     = getString("internalAuthToken")
  val tctrFrontendUrl: String       = getString("urls.tctrFrontend")

  private def getString(key: String): String =
    configuration.getOptional[String](key).getOrElse(throw ConfigSettingMissing(key))

  private def getBoolean(key: String): Boolean =
    configuration.getOptional[Boolean](key).getOrElse(throw ConfigSettingMissing(key))

case class ConfigSettingMissing(key: String) extends Exception(key)
