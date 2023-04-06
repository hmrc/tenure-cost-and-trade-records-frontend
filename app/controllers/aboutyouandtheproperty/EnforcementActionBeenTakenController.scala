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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import form.aboutyouandtheproperty.EnforcementActionForm.enforcementActionForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutTheProperty.updateAboutTheProperty
import navigation.AboutThePropertyNavigator
import navigation.identifiers.EnforcementActionBeenTakenPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutyouandtheproperty.enforcementActionBeenTaken

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
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        enforcementActionBeenTakenView(
          request.sessionData.aboutTheProperty.flatMap(_.enforcementAction) match {
            case Some(enforcementAction) => enforcementActionForm.fillAndValidate(enforcementAction)
            case _                       => enforcementActionForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    enforcementActionForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(enforcementActionBeenTakenView(formWithErrors, getBackLink(request.sessionData)))
          ),
        data => {
          val updatedData = updateAboutTheProperty(_.copy(enforcementAction = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(EnforcementActionBeenTakenPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutTheProperty.flatMap(_.premisesLicenseConditions.map(_.name)) match {
      case Some("yes") =>
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show().url
      case Some("no")  => controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show().url
      case _           =>
        logger.warn(s"Back link for enforcement action page reached with unknown premises licence conditions value")
        controllers.routes.TaskListController.show().url
    }
}
