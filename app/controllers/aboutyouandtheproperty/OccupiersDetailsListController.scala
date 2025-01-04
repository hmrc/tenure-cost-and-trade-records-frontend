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

package controllers.aboutyouandtheproperty

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.OccupiersDetailsListForm.occupiersDetailsListForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.OccupiersDetailsListId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.occupiersDetailsList
import views.html.genericRemoveConfirmation
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OccupiersDetailsListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  view: occupiersDetailsList,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          occupiersDetailsListForm,
          index,
          getBackLink
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      occupiersDetailsListForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            index,
            getBackLink
          )
        ),
      data =>
        request.sessionData.aboutYouAndThePropertyPartTwo
          .map(_.occupiersList)
          .fold {
            Future.successful(
              if (data == AnswerYes) {
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
              val updatedData = updateAboutYouAndThePropertyPartTwo(
                _.copy(
                  occupiersList = updatedServices,
                  addAnotherPaidService = data
                )
              )
              session.saveOrUpdate(updatedData).map { _ =>
                if (data == AnswerYes) {
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
            genericRemoveConfirmationView(
              confirmableActionForm,
              occupier.name,
              "label.section.aboutTheProperty",
              request.sessionData.toSummary,
              index,
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
                genericRemoveConfirmationView(
                  formWithErrors,
                  occupier.name,
                  "label.section.aboutTheProperty",
                  request.sessionData.toSummary,
                  index,
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
            session.saveOrUpdate(
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

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case _     =>
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsController
          .show(Option(request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.occupiersListIndex).getOrElse(0)))
          .url
    }
}
