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
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm.checkYourAnswersAboutFranchiseOrLettingsForm
import models.{ForTypes, Session}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings
import views.html.taskList

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersAboutFranchiseOrLettingsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  checkYourAnswersAboutFranchiseOrLettingsView: checkYourAnswersAboutFranchiseOrLettings,
  taskListView: taskList,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutFranchiseOrLettingsView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.checkYourAnswersAboutFranchiseOrLettings) match {
            case Some(checkYourAnswersAboutFranchiseOrLettings) =>
              checkYourAnswersAboutFranchiseOrLettingsForm.fillAndValidate(checkYourAnswersAboutFranchiseOrLettings)
            case _                                              => checkYourAnswersAboutFranchiseOrLettingsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft(
      Ok(taskListView())
    )
  }

  private def getLettingsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.lettingCurrentIndex).getOrElse(0)

  private def getBackLink(
    answers: Session
  ): String = //TODO Look at the back link logic. Got it loading but I'll come back to it! - Pete
    answers.forType match {
      case ForTypes.for6010 | ForTypes.for6011 =>
        answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
          case Some("yes") => controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
          case Some("no")  =>
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
          case _           =>
            logger.warn(s"Back link for enforcement action page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
        answers.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty.map(_.name)) match {
          case Some("yes") =>
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show().url
          case Some("no")  =>
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show().url
          case _           =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }

      case ForTypes.for6015 | ForTypes.for6016 =>
        answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
          case Some("yes") =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case Some("no")  =>
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
          case _           =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case ForTypes.for6015 | ForTypes.for6016 =>
        answers.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty.map(_.name)) match {
          case Some("yes") =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case Some("no")  =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case _           =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case ForTypes.for6015 | ForTypes.for6016 =>
        val existingSection =
          answers.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(getLettingsIndex(answers)))
        existingSection.flatMap(_.addAnotherLettingToProperty).get.name match {
          case "yes" =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case "no"  =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case _     =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
    }
}
