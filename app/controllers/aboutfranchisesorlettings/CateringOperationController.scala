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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationForm.cateringOperationForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.AnswersYesNo
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodation
import models.pages.Summary

import javax.inject.{Inject, Named, Singleton}

@Singleton
class CateringOperationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      cateringOperationOrLettingAccommodationView(
        request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringConcessionOrFranchise) match {
          case Some(cateringOperationOrLettingAccommodation) =>
            cateringOperationForm.fillAndValidate(cateringOperationOrLettingAccommodation)
          case _                                             => cateringOperationForm
        },
        "cateringOperationOrLettingAccommodation",
        controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
        Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      cateringOperationForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationView(
            formWithErrors,
            "cateringOperationOrLettingAccommodation",
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url,
            Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
          )
        ),
      data => {
        val updatedData =
          updateAboutFranchisesOrLettings(_.copy(cateringConcessionOrFranchise = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(CateringOperationPageId).apply(updatedData))
      }
    )
  }

}
