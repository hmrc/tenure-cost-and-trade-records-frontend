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
import form.aboutfranchisesorlettings.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.AnswersYesNo
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.AddAnotherLettingAccommodationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings._

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherLettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(index))

    Future.successful(
      Ok(
        addAnotherCateringOperationOrLettingAccommodationView(
          existingSection.flatMap(_.addAnotherLettingToProperty) match {
            case Some(addAnotherLettings) => addAnotherLettingForm.fill(addAnotherLettings)
            case _                        => addAnotherLettingForm
          },
          index,
          "addAnotherLettingOtherPartOfProperty",
          controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController.show(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherLettingForm,
      formWithErrors =>
        BadRequest(
          addAnotherCateringOperationOrLettingAccommodationView(
            formWithErrors,
            index,
            "addAnotherLettingOtherPartOfProperty",
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController
              .show(index)
              .url,
            request.sessionData.toSummary
          )
        ),
      data =>
        request.sessionData.aboutFranchisesOrLettings
          .map(_.lettingSections)
          .filter(_.nonEmpty)
          .fold(
            Redirect(
              if (data.name == "yes") {
                routes.LettingOtherPartOfPropertyDetailsController.show()
              } else {
                routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
              }
            )
          ) { existingSections =>
            val updatedSections = existingSections.updated(
              index,
              existingSections(index).copy(addAnotherLettingToProperty = Some(data))
            )
            val updatedData     = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
            session.saveOrUpdate(updatedData)
            Redirect(navigator.nextPage(AddAnotherLettingAccommodationPageId, updatedData).apply(updatedData))
          }
    )
  }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections).map { lettingSections =>
      val updatedSections = lettingSections.patch(idx, Nil, 1)
      session.saveOrUpdate(
        updateAboutFranchisesOrLettings(
          _.copy(lettingCurrentIndex = 0, lettingSections = updatedSections)
        )
      )
    }
    Redirect(routes.AddAnotherLettingOtherPartOfPropertyController.show(0))
  }

}
