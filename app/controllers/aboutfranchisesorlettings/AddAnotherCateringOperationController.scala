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
import form.aboutfranchisesorlettings.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.AnswersYesNo
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.AddAnotherCateringOperationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.addAnotherCateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherCateringOperationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lift(index))

    Future.successful(
      Ok(
        addAnotherCateringOperationOrLettingAccommodationView(
          existingSection.flatMap(_.addAnotherOperationToProperty) match {
            case Some(addAnotherOperation) => addAnotherCateringOperationForm.fillAndValidate(addAnotherOperation)
            case _                         => addAnotherCateringOperationForm
          },
          index,
          "addAnotherCateringOperationOrLettingAccommodations",
          controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherCateringOperationForm,
      formWithErrors =>
        BadRequest(
          addAnotherCateringOperationOrLettingAccommodationView(
            formWithErrors,
            index,
            "addAnotherCateringOperationOrLettingAccommodations",
            controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(index).url,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutFranchisesOrLettings.fold(
          Redirect(routes.CateringOperationRentIncludesController.show(index))
        ) { aboutFranchisesOrLettings =>
          val existingSections = aboutFranchisesOrLettings.cateringOperationSections
          val updatedSections  = existingSections.updated(
            index,
            existingSections(index).copy(addAnotherOperationToProperty = Some(data))
          )
          val updatedData      = updateAboutFranchisesOrLettings(_.copy(cateringOperationSections = updatedSections))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(AddAnotherCateringOperationPageId).apply(updatedData))
        }
    )
  }

}
