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
import form.aboutfranchisesorlettings.CateringOperationOrLettingAccommodationRentForm.cateringOperationOrLettingAccommodationRentForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.CateringOperationRentDetails
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationRentDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentDetails
import models.pages.Summary

import javax.inject.{Inject, Named, Singleton}

@Singleton
class CateringOperationDetailsRentController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(
      _.cateringOperationSections.lift(index)
    )
    existingSection.fold(
      Redirect(routes.CateringOperationDetailsController.show(None))
    ) { cateringOperationOrLettingAccommodationSection =>
      val rentDetailsForm =
        cateringOperationOrLettingAccommodationSection.cateringOperationRentDetails.fold(
          cateringOperationOrLettingAccommodationRentForm
        )(cateringOperationOrLettingAccommodationRentForm.fill)
      Ok(
        cateringOperationOrLettingAccommodationRentDetailsView(
          rentDetailsForm,
          index,
          "cateringOperationOrLettingAccommodationRentDetails",
          existingSection.get.cateringOperationDetails.operatorName,
          controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show().url,
          Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
        )
      )
    }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections).get(index)

    continueOrSaveAsDraft[CateringOperationRentDetails](
      cateringOperationOrLettingAccommodationRentForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationRentDetailsView(
            formWithErrors,
            index,
            "cateringOperationOrLettingAccommodationRentDetails",
            existingSection.cateringOperationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show().url,
            Summary(request.sessionData.referenceNumber, Some(request.sessionData.address))
          )
        ),
      data =>
        request.sessionData.aboutFranchisesOrLettings.fold(
          Redirect(routes.CateringOperationDetailsController.show(None))
        ) { aboutFranchisesOrLettings =>
          val existingSections = aboutFranchisesOrLettings.cateringOperationSections
          val updatedSections  = existingSections.updated(
            index,
            existingSections(index).copy(cateringOperationRentDetails = Some(data))
          )
          val updatedData      = updateAboutFranchisesOrLettings(_.copy(cateringOperationSections = updatedSections))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(CateringOperationRentDetailsPageId).apply(updatedData))
        }
    )
  }

}
