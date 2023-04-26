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
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyRentForm.lettingOtherPartOfPropertyRentForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.LettingOtherPartOfPropertyRentDetails
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingAccommodationRentDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationRentDetails

import javax.inject.{Inject, Named, Singleton}

@Singleton
class LettingOtherPartOfPropertyDetailsRentController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))
    existingSection.fold(Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))) { lettingSection =>
      val lettingDetailsForm = lettingSection.lettingOtherPartOfPropertyRentDetails.fold(
        lettingOtherPartOfPropertyRentForm
      )(lettingOtherPartOfPropertyRentForm.fill)
      Ok(
        cateringOperationOrLettingAccommodationRentDetailsView(
          lettingDetailsForm,
          index,
          "lettingOtherPartOfPropertyRentDetails",
          existingSection.get.lettingOtherPartOfPropertyInformationDetails.operatorName,
          controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show().url,
          request.sessionData.toSummary
        )
      )
    }
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections).get(index)

    continueOrSaveAsDraft[LettingOtherPartOfPropertyRentDetails](
      lettingOtherPartOfPropertyRentForm,
      formWithErrors =>
        BadRequest(
          cateringOperationOrLettingAccommodationRentDetailsView(
            formWithErrors,
            index,
            "lettingOtherPartOfPropertyRentDetails",
            existingSection.lettingOtherPartOfPropertyInformationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show().url,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutFranchisesOrLettings.fold(
          Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))
        ) { aboutFranchiseOrLettings =>
          val existingSections = aboutFranchiseOrLettings.lettingSections
          val updatedSections  = existingSections
            .updated(index, existingSections(index).copy(lettingOtherPartOfPropertyRentDetails = Some(data)))
          val updatedData      = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(LettingAccommodationRentDetailsPageId).apply(updatedData))
        }
    )
  }

}
