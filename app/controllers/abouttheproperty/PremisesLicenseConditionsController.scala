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
import controllers.LoginController.loginForm
import form.abouttheproperty.EnforcementActionForm.enforcementActionForm
import form.abouttheproperty.PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm
import form.abouttheproperty.PremisesLicenseConditionsForm.premisesLicenseConditionsForm
import models.submissions.abouttheproperty.PremisesLicensesConditionsYes
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import models.submissions.abouttheproperty.{PremisesLicensesConditionsNo, PremisesLicensesConditionsYes}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.abouttheproperty.{enforcementActionBeenTaken, premisesLicenseConditions, premisesLicenseConditionsDetails}
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PremisesLicenseConditionsController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  premisesLicenseView: premisesLicenseConditions,
  premisesLicenceDetailsView: premisesLicenseConditionsDetails,
  enforcementActionBeenTakenView: enforcementActionBeenTaken,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        premisesLicenseView(
          request.sessionData.aboutTheProperty.flatMap(_.premisesLicenseConditions) match {
            case Some(premisesLicense) => premisesLicenseConditionsForm.fillAndValidate(premisesLicense)
            case _                     => premisesLicenseConditionsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    premisesLicenseConditionsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(premisesLicenseView(formWithErrors))),
        {
          case data @ PremisesLicensesConditionsYes =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(premisesLicenseConditions = Some(data))))
            Future.successful(Ok(premisesLicenceDetailsView(premisesLicenceDetailsForm)))
          case data @ PremisesLicensesConditionsNo  =>
            session.saveOrUpdate(updateAboutTheProperty(_.copy(premisesLicenseConditions = Some(data))))
            Future.successful(Ok(enforcementActionBeenTakenView(enforcementActionForm)))
          case _                                    => Future.successful(Ok(login(loginForm)))
        }
      )
  }

}
