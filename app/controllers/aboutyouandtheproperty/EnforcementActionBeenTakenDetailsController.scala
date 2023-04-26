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
import form.aboutyouandtheproperty.EnforcementActionDetailsForm.enforcementActionDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.EnforcementActionHasBeenTakenInformationDetails
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.EnforcementActionBeenTakenDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.enforcementActionBeenTakenDetails
import models.pages.Summary

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class EnforcementActionBeenTakenDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  enforcementActionBeenTakenDetailsView: enforcementActionBeenTakenDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        enforcementActionBeenTakenDetailsView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.enforcementActionHasBeenTakenInformationDetails) match {
            case Some(enforcementActionInformation) =>
              enforcementActionDetailsForm.fillAndValidate(enforcementActionInformation)
            case _                                  => enforcementActionDetailsForm
          },
          Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[EnforcementActionHasBeenTakenInformationDetails](
      enforcementActionDetailsForm,
      formWithErrors =>
        BadRequest(
          enforcementActionBeenTakenDetailsView(
            formWithErrors,
            Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
          )
        ),
      data => {
        val updatedData =
          updateAboutYouAndTheProperty(_.copy(enforcementActionHasBeenTakenInformationDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(EnforcementActionBeenTakenDetailsPageId).apply(updatedData))
      }
    )
  }

}
