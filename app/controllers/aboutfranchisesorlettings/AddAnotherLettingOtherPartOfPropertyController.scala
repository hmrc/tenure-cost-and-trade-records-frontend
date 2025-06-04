/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutfranchisesorlettings.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.AddAnotherLettingAccommodationPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings._
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherLettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theListView: addAnotherCateringOperationOrLettingAccommodation,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val addAnother = request.sessionData.aboutFranchisesOrLettings
      .flatMap(_.lettingSections.lift(index))
      .flatMap(_.addAnotherLettingToProperty)

    audit.sendChangeLink("AddAnotherLettingOtherPartOfProperty")

    Future.successful(
      Ok(
        theListView(
          Option.when(navigator.from == "CYA")(AnswerNo).orElse(addAnother) match {
            case Some(addAnotherLettings) => addAnotherLettingForm.fill(addAnotherLettings)
            case _                        => addAnotherLettingForm
          },
          index,
          "addAnotherLettingConcessionOrFranchise",
          "addAnotherLetting",
          "addAnotherLettingOtherPartOfProperty"
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    if (request.sessionData.aboutFranchisesOrLettings.exists(_.lettingCurrentIndex >= 4) && navigator.from != "CYA") {

      val redirectUrl = controllers.routes.MaxOfLettingsReachedController.show(Some("franchiseLetting")).url
      Future.successful(Redirect(redirectUrl))
    } else {
      continueOrSaveAsDraft[AnswersYesNo](
        addAnotherLettingForm,
        formWithErrors =>
          BadRequest(
            theListView(
              formWithErrors,
              index,
              "addAnotherLettingConcessionOrFranchise",
              "addAnotherLetting",
              "addAnotherLettingOtherPartOfProperty"
            )
          ),
        formData =>
          request.sessionData.aboutFranchisesOrLettings
            .map(_.lettingSections)
            .filter(_.nonEmpty)
            .fold(
              Future.successful(
                Redirect(
                  if (formData.name == "yes") {
                    routes.LettingOtherPartOfPropertyDetailsController.show()
                  } else {
                    routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
                  }
                )
              )
            ) { existingSections =>
              val updatedSections = existingSections.updated(
                index,
                existingSections(index).copy(addAnotherLettingToProperty = Some(formData))
              )
              val updatedData     = updateAboutFranchisesOrLettings(_.copy(lettingSections = updatedSections))
              repository.saveOrUpdate(updatedData).map { _ =>
                Redirect(
                  navigator
                    .nextWithoutRedirectToCYA(AddAnotherLettingAccommodationPageId, updatedData)
                    .apply(updatedData)
                )
              }
            }
      )
    }
  }

  def remove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutFranchisesOrLettings
      .flatMap(_.lettingSections.lift(idx))
      .map { lettingSection =>
        val name = lettingSection.lettingOtherPartOfPropertyInformationDetails.operatorName
        Future.successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              name,
              controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
                .performRemove(idx),
              controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(idx)
            )
          )
        )
      }
      .getOrElse(
        Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0))
      )
  }

  def performRemove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutFranchisesOrLettings
          .flatMap(_.lettingSections.lift(idx))
          .map { lettingSection =>
            val name = lettingSection.lettingOtherPartOfPropertyInformationDetails.operatorName
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  name,
                  controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
                    .performRemove(idx),
                  controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(
              controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0)
            )
          ),
      {
        case AnswerYes =>
          request.sessionData.aboutFranchisesOrLettings.map(_.lettingSections).map { lettingSection =>
            val updatedSections = lettingSection.patch(idx, Nil, 1)
            repository.saveOrUpdate(
              updateAboutFranchisesOrLettings(
                _.copy(lettingCurrentIndex = 0, lettingSections = updatedSections)
              )
            )
          }
          Redirect(controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0))
        case AnswerNo  =>
          Redirect(
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(idx)
          )
      }
    )
  }
}
