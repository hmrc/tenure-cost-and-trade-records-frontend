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

package controllers.connectiontoproperty
import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.AddAnotherLettingPartOfPropertyForm.theForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AddAnotherLettingPartOfPropertyPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.addAnotherLettingPartOfProperty as AddAnotherLettingPartOfPropertyView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherLettingPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theListView: AddAnotherLettingPartOfPropertyView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AddAnotherLettingPartOfProperty")

    val existingSection = request.sessionData.stillConnectedDetails.flatMap(_.lettingPartOfPropertyDetails.lift(index))

    Future.successful(
      Ok(
        theListView(
          existingSection
            .flatMap(_.addAnotherLettingToProperty)
            .fold(theForm)(theForm.fill),
          index
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
        theForm,
        formWithErrors =>
          BadRequest(
            theListView(
              formWithErrors,
              index
            )
          ),
        formData =>
          request.sessionData.stillConnectedDetails
            .map(_.lettingPartOfPropertyDetails)
            .filter(_.nonEmpty)
            .fold(
              Future.successful(
                Redirect(
                  if (formData == AnswerYes) {
                    routes.LettingPartOfPropertyDetailsController.show()
                  } else {
                    navigator
                      .cyaPageDependsOnSession(request.sessionData)
                      .filter(_ => navigator.from == "CYA" && formData == AnswerNo)
                      .getOrElse(
                        navigator
                          .nextWithoutRedirectToCYA(AddAnotherLettingPartOfPropertyPageId, request.sessionData)
                          .apply(request.sessionData)
                      )
                  }
                )
              )
            ) { existingSections =>
              val updatedSections = existingSections.updated(
                index,
                existingSections(index).copy(addAnotherLettingToProperty = Some(formData))
              )
              val updatedData     = updateStillConnectedDetails(_.copy(lettingPartOfPropertyDetails = updatedSections))
              repository
                .saveOrUpdate(updatedData)
                .map { _ =>
                  navigator
                    .cyaPageDependsOnSession(updatedData)
                    .filter(_ => navigator.from == "CYA" && formData == AnswerNo)
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
            theConfirmationView(
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
                theConfirmationView(
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
            val updatedSections     = lettingSections.patch(idx, Nil, 1)
            val isRentReceivedYesNo = AnswersYesNo(updatedSections.nonEmpty)

            repository.saveOrUpdate(
              updateStillConnectedDetails(
                _.copy(
                  lettingPartOfPropertyDetailsIndex = 0,
                  isAnyRentReceived = Some(isRentReceivedYesNo),
                  lettingPartOfPropertyDetails = updatedSections
                )
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
