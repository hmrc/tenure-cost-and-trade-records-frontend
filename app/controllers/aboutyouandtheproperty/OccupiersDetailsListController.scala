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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.OccupiersDetailsListForm.theForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.OccupiersDetailsListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.occupiersDetailsList as OccupiersDetailsListView
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OccupiersDetailsListController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  theListView: OccupiersDetailsListView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("OccupiersDetailsList")
    Future.successful(
      Ok(
        theListView(
          theForm,
          index
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
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
        request.sessionData.aboutYouAndThePropertyPartTwo
          .map(_.occupiersList)
          .fold {
            Future.successful(
              if (formData == AnswerYes) {
                Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show())
              } else {
                Redirect(
                  navigator.nextPage(OccupiersDetailsListId, request.sessionData).apply(request.sessionData)
                )
              }
            )
          } { existingEntries =>
            if (existingEntries.isEmpty) {
              Redirect(
                navigator.nextPage(OccupiersDetailsListId, request.sessionData).apply(request.sessionData)
              )
            } else {
              val updatedServices = existingEntries.updated(
                index,
                existingEntries(index)
              )
              val updatedData     = updateAboutYouAndThePropertyPartTwo(
                _.copy(
                  occupiersList = updatedServices,
                  addAnotherPaidService = formData
                )
              )
              repository.saveOrUpdate(updatedData).map { _ =>
                if (formData == AnswerYes) {
                  Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show())
                } else {
                  Redirect(navigator.nextPage(OccupiersDetailsListId, updatedData).apply(updatedData))
                }
              }
            }
          }
    )
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutYouAndThePropertyPartTwo
      .flatMap(_.occupiersList.lift(index))
      .map { occupier =>
        Future.successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              occupier.name,
              controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.performRemove(index),
              controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(index)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(index)))
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        request.sessionData.aboutYouAndThePropertyPartTwo
          .flatMap(_.occupiersList.lift(index))
          .map { occupier =>
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  occupier.name,
                  controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.performRemove(index),
                  controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(index)
                )
              )
            )
          }
          .getOrElse(Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0))),
      {
        case AnswerYes =>
          request.sessionData.aboutYouAndThePropertyPartTwo.map(_.occupiersList).map { list =>
            val updatedServices = list.patch(index, Nil, 1)
            repository.saveOrUpdate(
              updateAboutYouAndThePropertyPartTwo(
                _.copy(occupiersList = updatedServices)
              )
            )
          }
          Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0))
      }
    )
  }
}
