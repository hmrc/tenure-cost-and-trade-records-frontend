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
import form.aboutyouandtheproperty.PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.PremisesLicenceConditionsDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutyouandtheproperty.premisesLicenseConditionsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PremisesLicenseConditionsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  premisesLicenseConditionsView: premisesLicenseConditionsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        premisesLicenseConditionsView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.premisesLicenseConditionsDetails) match {
            case Some(premisesLicenseInformation) =>
              premisesLicenceDetailsForm.fillAndValidate(premisesLicenseInformation)
            case _                                => premisesLicenceDetailsForm
          }
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    premisesLicenceDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(premisesLicenseConditionsView(formWithErrors))),
        data => {
          val updatedData = updateAboutYouAndTheProperty(_.copy(premisesLicenseConditionsDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(PremisesLicenceConditionsDetailsPageId).apply(updatedData)))
        }
      )
  }

}
