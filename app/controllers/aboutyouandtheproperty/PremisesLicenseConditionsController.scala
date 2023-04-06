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
import form.aboutyouandtheproperty.PremisesLicenseConditionsForm.premisesLicenseConditionsForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutTheProperty.updateAboutTheProperty
import navigation.AboutThePropertyNavigator
import navigation.identifiers.PremisesLicenceConditionsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutyouandtheproperty.premisesLicenseConditions

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PremisesLicenseConditionsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  premisesLicenseView: premisesLicenseConditions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        premisesLicenseView(
          request.sessionData.aboutTheProperty.flatMap(_.premisesLicenseConditions) match {
            case Some(premisesLicense) => premisesLicenseConditionsForm.fillAndValidate(premisesLicense)
            case _                     => premisesLicenseConditionsForm
          },
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    premisesLicenseConditionsForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(premisesLicenseView(formWithErrors, getBackLink(request.sessionData)))),
        data => {
          val updatedData = updateAboutTheProperty(_.copy(premisesLicenseConditions = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(PremisesLicenceConditionsPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutTheProperty.flatMap(_.licensableActivities.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show().url
      case Some("no")  => controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show().url
      case _           =>
        logger.warn(s"Back link for premises licence conditions page reached with unknown licence activity value")
        controllers.routes.TaskListController.show().url
    }
}
