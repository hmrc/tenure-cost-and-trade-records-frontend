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
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.LicensableActivitiesInformationForm.licensableActivitiesDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.LicensableActivitiesInformationDetails
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.LicensableActivityDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.licensableActivitiesDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LicensableActivitiesDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  licensableActivitiesDetailsView: licensableActivitiesDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        licensableActivitiesDetailsView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.licensableActivitiesInformationDetails) match {
            case Some(licensableActivitiesInformation) =>
              licensableActivitiesDetailsForm.fillAndValidate(licensableActivitiesInformation)
            case _                                     => licensableActivitiesDetailsForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LicensableActivitiesInformationDetails](
      licensableActivitiesDetailsForm,
      formWithErrors =>
        BadRequest(
          licensableActivitiesDetailsView(
            formWithErrors,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(licensableActivitiesInformationDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(LicensableActivityDetailsPageId).apply(updatedData))
      }
    )
  }

}
