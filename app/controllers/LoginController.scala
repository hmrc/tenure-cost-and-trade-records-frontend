/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import connectors.BackendConnector
import views.html.{login, testSign}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class LoginController  @Inject()(
    mcc: MessagesControllerComponents,
    login: login,
    test: testSign,
    connector: BackendConnector
) extends FrontendController(mcc) with Logging {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(login()))
  }

  def submit(referenceNumber: String, postcode: String): Action[AnyContent] = Action.async { implicit request =>
    logger.debug(s"Signing in with: reference number : ${referenceNumber}, postcode: ${postcode}")
    val msg = connector.testConnection(referenceNumber, postcode)
    Future.successful(Ok(test(msg)))
  }

}
