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
import form.Form6010.LicensableActivitiesInformationForm.licensableActivitiesDetailsForm
import form.Form6010.PremisesLicenseForm.premisesLicenseForm
import models.submissions.SectionTwo.updateSectionTwo
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{licensableActivitiesDetails, premisesLicense}
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LicensableActivitiesDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  licensableActivitiesDetailsView: licensableActivitiesDetails,
  premisesLicenseView: premisesLicense,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        licensableActivitiesDetailsView(
          request.sessionData.sectionTwo.flatMap(_.licensableActivitiesInformationDetails) match {
            case Some(licensableActivitiesInformation) =>
              licensableActivitiesDetailsForm.fillAndValidate(licensableActivitiesInformation)
            case _                                     => licensableActivitiesDetailsForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    licensableActivitiesDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(licensableActivitiesDetailsView(formWithErrors))),
        data => {
          session.saveOrUpdate(updateSectionTwo(_.copy(licensableActivitiesInformationDetails = Some(data))))
          Future.successful(Ok(premisesLicenseView(premisesLicenseForm)))
        }
      )
  }

}
