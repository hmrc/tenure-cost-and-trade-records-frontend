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
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.AddAnotherCateringOperationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.addAnotherCateringOperationOrLettingAccommodation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherCateringOperationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lift(index))

    Future.successful(
      Ok(
        addAnotherCateringOperationOrLettingAccommodationView(
          existingSection.flatMap(_.addAnotherOperationToProperty) match {
            case Some(addAnotherOperation) => addAnotherCateringOperationForm.fill(addAnotherOperation)
            case _                         => addAnotherCateringOperationForm
          },
          index,
          "addAnotherConcession",
          "addAnotherCateringOperation",
          controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    if (request.sessionData.aboutFranchisesOrLettings.exists(_.cateringOperationCurrentIndex >= 4)) {

      val redirectUrl = controllers.routes.MaxOfLettingsReachedController.show(Some("franchiseCatering")).url

      Future.successful(Redirect(redirectUrl))
    } else {

      val fromCYA = request.sessionData.aboutFranchisesOrLettings.flatMap(_.fromCYA).getOrElse(false)
      continueOrSaveAsDraft[AnswersYesNo](
        addAnotherCateringOperationForm,
        formWithErrors =>
          BadRequest(
            addAnotherCateringOperationOrLettingAccommodationView(
              formWithErrors,
              index,
              "addAnotherConcession",
              "addAnotherCateringOperation",
              controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(index).url,
              request.sessionData.toSummary
            )
          ),
        data =>
          request.sessionData.aboutFranchisesOrLettings
            .map(_.cateringOperationSections)
            .filter(_.nonEmpty)
            .fold(
              Future.successful(
              Redirect(
                if (data == AnswerYes) {
                  routes.CateringOperationDetailsController.show()
                } else if (data == AnswerNo && fromCYA == true) {
                  routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
                } else {
                  routes.LettingOtherPartOfPropertyController.show()
                }
              ))
            ) { existingSections =>
              val updatedSections = existingSections.updated(
                index,
                existingSections(index).copy(addAnotherOperationToProperty = Some(data))
              )
              val updatedData     = updateAboutFranchisesOrLettings(_.copy(cateringOperationSections = updatedSections))
              session.saveOrUpdate(updatedData).map{_ =>
              Redirect(navigator.nextPage(AddAnotherCateringOperationPageId, updatedData).apply(updatedData))}
            }
      )
    }
  }
  def remove(idx: Int)   = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections).map { cateringOperationSections =>
      val updatedSections = cateringOperationSections.patch(idx, Nil, 1)
      session.saveOrUpdate(
        updateAboutFranchisesOrLettings(
          _.copy(cateringOperationCurrentIndex = 0, cateringOperationSections = updatedSections)
        )
      )
    }
    Redirect(routes.AddAnotherCateringOperationController.show(0))
  }

}
