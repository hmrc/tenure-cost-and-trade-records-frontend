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

package controllers.abouttheproperty

import actions.WithSessionRefiner
import form.abouttheproperty.EnforcementActionForm.enforcementActionForm
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import navigation.AboutThePropertyNavigator
import navigation.identifiers.EnforcementActionBeenTakenPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.abouttheproperty.enforcementActionBeenTaken

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class EnforcementActionBeenTakenController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  enforcementActionBeenTakenView: enforcementActionBeenTaken,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(enforcementActionBeenTakenView(enforcementActionForm)))
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    enforcementActionForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(enforcementActionBeenTakenView(formWithErrors))),
        data => {
          val updatedData = updateAboutTheProperty(_.copy(enforcementAction = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(EnforcementActionBeenTakenPageId).apply(updatedData)))
        }
      )
  }
}
