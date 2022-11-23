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

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import form.PastConnectionForm.pastConnectionForm
import play.api.i18n.I18nSupport
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{areYouStillConnectedNo, login}
import models.Session

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AreYouStillConnectedNoController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  areYouStillConnectedNoView: areYouStillConnectedNo,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport  {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(areYouStillConnectedNoView(pastConnectionForm)))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    pastConnectionForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(areYouStillConnectedNoView(formWithErrors))),
        data => {
          session.saveOrUpdate(Session(request.sessionData, data))
          Future.successful(Ok(login(loginForm)))
        }
      )
  }

}
