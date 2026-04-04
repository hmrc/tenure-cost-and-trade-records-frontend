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
import play.api.mvc.{Call, RequestHeader}
import uk.gov.hmrc.vo.service.config.VOServiceConfig

@Singleton
class AppConfig @Inject() (val configuration: Configuration) extends VOServiceConfig:

  override def serviceLocalRoot: Call = controllers.routes.Application.index
  override def serviceMenuHome: Call  = controllers.routes.Application.index
  override def theFirstPage: Call     = controllers.routes.LoginController.show
  override def feedbackPage: Call     = controllers.routes.FeedbackController.feedback // TODO: Remove to use feedbackFrontendForm

  override def isWelshTranslationAvailable: Boolean = true

  override def stylesheet: Option[Call] = Some(controllers.routes.Assets.versioned("stylesheets/app.min.css"))

  override def notificationBannerEnabledOn: Set[Call] = Set(
    serviceMenuHome,
    controllers.routes.LoginController.show
  )

  // Timeout Dialog
  override def timeoutDialogEnabledExcept: Set[Call] = Set(
    serviceMenuHome,
    controllers.routes.LoginController.show // TODO: Add
  )

  override def signOutCall: Option[Call] = Some(controllers.routes.LoginController.logout)

  override def timeoutCall(using request: RequestHeader): Option[Call] = Some(controllers.routes.SaveAsDraftController.timeout(request.uri))

  val useDummyIp: Boolean        = getBoolean("useDummyTrueIP")
  val startPageRedirect: Boolean = getBoolean("startPageRedirect")
  val govukStartPage: String     = getString("govukStartPage")
  val internalAuthToken: String  = getString("internalAuthToken")
  val tctrFrontendUrl: String    = getString("urls.tctrFrontend")
