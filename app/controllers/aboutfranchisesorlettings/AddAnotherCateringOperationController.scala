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
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationForm
import form.confirmableActionForm.confirmableActionForm
import models.ForTypes
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.AddAnotherCateringOperationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.addAnotherCateringOperationOrLettingAccommodation
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherCateringOperationController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val franchisesOrLettingsData = request.sessionData.aboutFranchisesOrLettings

    val addAnother = if (request.sessionData.forType == ForTypes.for6030) {
      franchisesOrLettingsData
        .flatMap(_.cateringOperationBusinessSections.flatMap(_.lift(index)))
        .flatMap(_.addAnotherOperationToProperty)
        .orElse(if (navigator.from == "CYA") Some(AnswerNo) else None)
    } else {
      franchisesOrLettingsData
        .flatMap(_.cateringOperationSections.lift(index))
        .flatMap(_.addAnotherOperationToProperty)
    }

    Future.successful(
      Ok(
        addAnotherCateringOperationOrLettingAccommodationView(
          addAnother.fold(addAnotherCateringOperationForm)(addAnotherCateringOperationForm.fill),
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
      val fromCYA =
        request.sessionData.aboutFranchisesOrLettings.flatMap(_.fromCYA).getOrElse(false) || navigator.from == "CYA"
      continueOrSaveAsDraft[AnswersYesNo](
        addAnotherCateringOperationForm,
        formWithErrors =>
          (BadRequest(
            addAnotherCateringOperationOrLettingAccommodationView(
              formWithErrors,
              index,
              "addAnotherConcession",
              "addAnotherCateringOperation",
              controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(index).url,
              request.sessionData.toSummary
            )
          )),
        data =>
          request.sessionData.aboutFranchisesOrLettings
            .map(_.cateringOperationSections)
            .filter(_.nonEmpty)
            .fold(
              Future.successful(
                Redirect(
                  if (data == AnswerNo && fromCYA == true) {
                    controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
                      .show()
                  } else if (data == AnswerYes) {
                    controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
                  } else {
                    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
                  }
                )
              )
            ) { existingSections =>
              val updatedSections = existingSections.updated(
                index,
                existingSections(index).copy(addAnotherOperationToProperty = Some(data))
              )
              val updatedData     = updateAboutFranchisesOrLettings(
                _.copy(
                  cateringOperationSections = updatedSections,
                  fromCYA = Some(fromCYA)
                )
              )
              session.saveOrUpdate(updatedData).flatMap { _ =>
                Redirect(navigator.nextPage(AddAnotherCateringOperationPageId, updatedData).apply(updatedData))
              }
            }
      )
    }
  }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings
      .flatMap(_.cateringOperationSections.lift(idx))
      .map { cateringOperationSections =>
        val name = cateringOperationSections.cateringOperationDetails.operatorName
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              name,
              "label.section.aboutTheFranchiseConcessions",
              request.sessionData.toSummary,
              idx,
              controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.performRemove(idx),
              controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0)))
  }

  def performRemove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutFranchisesOrLettings
          .flatMap(_.cateringOperationSections.lift(idx))
          .map { cateringOperationSections =>
            val name = cateringOperationSections.cateringOperationDetails.operatorName
            Future.successful(
              BadRequest(
                genericRemoveConfirmationView(
                  formWithErrors,
                  name,
                  "label.section.aboutTheFranchiseConcessions",
                  request.sessionData.toSummary,
                  idx,
                  controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.performRemove(idx),
                  controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0))
          ),
      {
        case AnswerYes =>
          request.sessionData.aboutFranchisesOrLettings.map(_.cateringOperationSections).map {
            cateringOperationSections =>
              val updatedSections = cateringOperationSections.patch(idx, Nil, 1)
              session.saveOrUpdate(
                updateAboutFranchisesOrLettings(
                  _.copy(cateringOperationCurrentIndex = 0, cateringOperationSections = updatedSections)
                )
              )
          }
          Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(idx))
      }
    )
  }

}
