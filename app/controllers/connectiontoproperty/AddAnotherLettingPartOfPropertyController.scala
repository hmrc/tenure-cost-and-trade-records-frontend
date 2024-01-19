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

package controllers.connectiontoproperty
import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.connectiontoproperty.AddAnotherLettingPartOfPropertyForm.addAnotherLettingForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AddAnotherLettingPartOfPropertyPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.addAnotherLettingPartOfProperty
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherLettingPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  addAnotherLettingPartOfPropertyView: addAnotherLettingPartOfProperty,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingSection = request.sessionData.stillConnectedDetails.flatMap(_.lettingPartOfPropertyDetails.lift(index))

    Future.successful(
      Ok(
        addAnotherLettingPartOfPropertyView(
          existingSection.flatMap(_.addAnotherLettingToProperty) match {
            case Some(addAnotherLettings) => addAnotherLettingForm.fill(addAnotherLettings)
            case _                        => addAnotherLettingForm
          },
          index,
          controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController.show(index).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    if (request.sessionData.stillConnectedDetails.exists(_.lettingPartOfPropertyDetailsIndex >= 4)) {

      val redirectUrl = controllers.routes.MaxOfLettingsReachedController.show(Some("connection")).url
      Future.successful(Redirect(redirectUrl))
    } else {
      continueOrSaveAsDraft[AnswersYesNo](
        addAnotherLettingForm,
        formWithErrors =>
          BadRequest(
            addAnotherLettingPartOfPropertyView(
              formWithErrors,
              index,
              controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController
                .show(index)
                .url,
              request.sessionData.toSummary
            )
          ),
        data =>
          request.sessionData.stillConnectedDetails
            .map(_.lettingPartOfPropertyDetails)
            .filter(_.nonEmpty)
            .fold(
              Future.successful(
                Redirect(
                  if (data.name == "yes") {
                    routes.LettingPartOfPropertyDetailsController.show()
                  } else {
                    routes.CheckYourAnswersConnectionToVacantPropertyController.show()
                  }
                )
              )
            ) { existingSections =>
              val updatedSections = existingSections.updated(
                index,
                existingSections(index).copy(addAnotherLettingToProperty = Some(data))
              )
              val updatedData     = updateStillConnectedDetails(_.copy(lettingPartOfPropertyDetails = updatedSections))
              session
                .saveOrUpdate(updatedData)
                .map { _ =>
                  navigator
                    .cyaPageDependsOnSession(updatedData)
                    .filter(_ => navigator.from == "CYA" && data == AnswerNo)
                    .getOrElse(
                      navigator
                        .nextWithoutRedirectToCYA(AddAnotherLettingPartOfPropertyPageId, updatedData)
                        .apply(updatedData)
                    )
                }
                .map(Redirect)
            }
      )
    }
  }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.stillConnectedDetails
      .flatMap(_.lettingPartOfPropertyDetails.lift(idx))
      .map { lettingSections =>
        val name = lettingSections.tenantDetails.name
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              name,
              "label.section.connectionToTheProperty",
              request.sessionData.toSummary,
              idx,
              routes.AddAnotherLettingPartOfPropertyController.performRemove(idx),
              routes.AddAnotherLettingPartOfPropertyController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(routes.AddAnotherLettingPartOfPropertyController.show(0)))
  }

  def performRemove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.stillConnectedDetails
          .flatMap(_.lettingPartOfPropertyDetails.lift(idx))
          .map { lettingSections =>
            val name = lettingSections.tenantDetails.name
            Future.successful(
              BadRequest(
                genericRemoveConfirmationView(
                  formWithErrors,
                  name,
                  "label.section.connectionToTheProperty",
                  request.sessionData.toSummary,
                  idx,
                  routes.AddAnotherLettingPartOfPropertyController.performRemove(idx),
                  routes.AddAnotherLettingPartOfPropertyController.show(idx)
                )
              )
            )
          }
          .getOrElse(Redirect(routes.AddAnotherLettingPartOfPropertyController.show(0))),
      {
        case AnswerYes =>
          request.sessionData.stillConnectedDetails.map(_.lettingPartOfPropertyDetails).map { lettingSections =>
            val updatedSections = lettingSections.patch(idx, Nil, 1)
            session.saveOrUpdate(
              updateStillConnectedDetails(
                _.copy(lettingPartOfPropertyDetailsIndex = 0, lettingPartOfPropertyDetails = updatedSections)
              )
            )
          }
          Redirect(routes.AddAnotherLettingPartOfPropertyController.show(0))
        case AnswerNo  =>
          Redirect(routes.AddAnotherLettingPartOfPropertyController.show(idx))
      }
    )
  }
}
