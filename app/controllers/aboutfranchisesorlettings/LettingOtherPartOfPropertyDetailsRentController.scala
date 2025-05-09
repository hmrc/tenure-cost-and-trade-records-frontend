/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyRent6015Form.lettingOtherPartOfPropertyRent6015Form
import form.aboutfranchisesorlettings.IncomeRecordRentForm.incomeRecordRentForm
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{LettingOtherPartOfPropertyRent6015Details, PropertyRentDetails}
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
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationOrLettingAccommodationRentDetailsView: cateringOperationOrLettingAccommodationRentDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val forType = request.sessionData.forType

    if (forType == FOR6015 || forType == FOR6016) {
      val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))
      existingSection.fold(Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))) { lettingSection =>
        val lettingDetailsForm = lettingSection.lettingOtherPartOfPropertyRent6015Details.fold(
          lettingOtherPartOfPropertyRent6015Form
        )(lettingOtherPartOfPropertyRent6015Form.fill)
        audit.sendChangeLink("LettingOtherPartOfPropertyDetailsRent")

        Ok(
          cateringOperationOrLettingAccommodationRentDetailsView(
            lettingDetailsForm,
            index,
            "lettingOtherPartOfPropertyRentDetails",
            existingSection.get.lettingOtherPartOfPropertyInformationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
              .show(Some(index))
              .url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        )
      }
    } else {
      val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))
      existingSection.fold(Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))) { lettingSection =>
        val lettingDetailsForm = lettingSection.lettingOtherPartOfPropertyRentDetails.fold(
          incomeRecordRentForm
        )(incomeRecordRentForm.fill)
        Ok(
          cateringOperationOrLettingAccommodationRentDetailsView(
            lettingDetailsForm,
            index,
            "lettingOtherPartOfPropertyRentDetails",
            existingSection.get.lettingOtherPartOfPropertyInformationDetails.operatorName,
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
              .show(Some(index))
              .url,
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        )
      }
    }
  }
  def submit(index: Int)                   = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections).get(index)
    val forType         = request.sessionData.forType

    if (forType == FOR6015 || forType == FOR6016) {
      continueOrSaveAsDraft[LettingOtherPartOfPropertyRent6015Details](
        lettingOtherPartOfPropertyRent6015Form,
        formWithErrors =>
          BadRequest(
            cateringOperationOrLettingAccommodationRentDetailsView(
              formWithErrors,
              index,
              "lettingOtherPartOfPropertyRentDetails",
              existingSection.lettingOtherPartOfPropertyInformationDetails.operatorName,
              controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
                .show(Some(index))
                .url,
              request.sessionData.toSummary,
              request.sessionData.forType
            )
          ),
        data =>
          request.sessionData.aboutFranchisesOrLettings.fold(
            Redirect(routes.LettingOtherPartOfPropertyDetailsController.show(None))
          ) { aboutFranchiseOrLettings =>
            val existingSections = aboutFranchiseOrLettings.lettingSections
            val updatedSections  = existingSections
              .updated(index, existingSections(index).copy(lettingOtherPartOfPropertyRent6015Details = Some(data)))
            val updatedData      = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
            session.saveOrUpdate(updatedData)
            Redirect(navigator.nextPage(LettingAccommodationRentDetailsPageId, updatedData).apply(updatedData))
          }
      )
    } else {
      continueOrSaveAsDraft[PropertyRentDetails](
        incomeRecordRentForm,
        formWithErrors =>
          BadRequest(
            cateringOperationOrLettingAccommodationRentDetailsView(
              formWithErrors,
              index,
              "lettingOtherPartOfPropertyRentDetails",
              existingSection.lettingOtherPartOfPropertyInformationDetails.operatorName,
              controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
                .show(Some(index))
                .url,
              request.sessionData.toSummary,
              request.sessionData.forType
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
            Redirect(navigator.nextPage(LettingAccommodationRentDetailsPageId, updatedData).apply(updatedData))
          }
      )
    }
  }

}
