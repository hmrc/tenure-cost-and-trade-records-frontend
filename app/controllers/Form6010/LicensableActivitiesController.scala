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

package controllers.Form6010

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{licensableActivities, licensableActivitiesDetails, premisesLicense}
import form.Form6010.LicensableActivitiesInformationForm.licensableActivitiesDetailsForm
import form.Form6010.LicensableActivitiesForm.licensableActivitiesForm
import form.Form6010.PremisesLicenseForm.premisesLicenseForm
import models.submissions.Form6010.{LicensableActivitiesNo, LicensableActivitiesYes}
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LicensableActivitiesController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  licensableActivitiesView: licensableActivities,
  licensableActivitiesDetailsView: licensableActivitiesDetails,
  premisesLicenseView: premisesLicense,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        licensableActivitiesView(
          request.sessionData.aboutTheProperty.flatMap(_.licensableActivities) match {
            case Some(licensableActivities) => licensableActivitiesForm.fillAndValidate(licensableActivities)
            case _                          => licensableActivitiesForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    licensableActivitiesForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(licensableActivitiesView(formWithErrors))),
        {
          case data @ LicensableActivitiesYes =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(licensableActivities = Some(data))))
            Future.successful(Ok(licensableActivitiesDetailsView(licensableActivitiesDetailsForm)))
          case data @ LicensableActivitiesNo  =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(licensableActivities = Some(data))))
            Future.successful(Ok(premisesLicenseView(premisesLicenseForm)))
          case _                              => Future.successful(Ok(login(loginForm)))
        }
      )
  }

}
