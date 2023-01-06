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
import views.html.form.{enforcementActionBeenTaken, premisesLicense, premisesLicenseConditions}
import form.Form6010.PremisesLicenseForm.premisesLicenseForm
import form.Form6010.PremisesLicenseDetailsForm.premisesLicenceDetailsForm
import form.Form6010.EnforcementActionForm.enforcementActionForm
import models.submissions.Form6010.{PremisesLicensesNo, PremisesLicensesYes}
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PremisesLicenseController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  premisesLicenseView: premisesLicense,
  premisesLicenceDetailsView: premisesLicenseConditions,
  enforcementActionBeenTakenView: enforcementActionBeenTaken,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        premisesLicenseView(
          request.sessionData.aboutTheProperty.flatMap(_.premisesLicense) match {
            case Some(premisesLicense) => premisesLicenseForm.fillAndValidate(premisesLicense)
            case _                     => premisesLicenseForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    premisesLicenseForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(premisesLicenseView(formWithErrors))),
        {
          case data @ PremisesLicensesYes =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(premisesLicense = Some(data))))
            Future.successful(Ok(premisesLicenceDetailsView(premisesLicenceDetailsForm)))
          case data @ PremisesLicensesNo  =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(premisesLicense = Some(data))))
            Future.successful(Ok(enforcementActionBeenTakenView(enforcementActionForm)))
          case _                          => Future.successful(Ok(login(loginForm)))
        }
      )
  }

}
