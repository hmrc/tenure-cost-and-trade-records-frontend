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
import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm.checkYourAnswersAboutFranchiseOrLettingsForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, CheckYourAnswersAboutFranchiseOrLettings}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import models.ForType.*
import models.Session
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CheckYourAnswersAboutFranchiseOrLettingsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.checkYourAnswersAboutFranchiseOrLettings

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutFranchiseOrLettingsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  checkYourAnswersAboutFranchiseOrLettingsView: checkYourAnswersAboutFranchiseOrLettings,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutFranchiseOrLettingsView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.checkYourAnswersAboutFranchiseOrLettings) match {
            case Some(checkYourAnswersAboutFranchiseOrLettings) =>
              checkYourAnswersAboutFranchiseOrLettingsForm.fill(checkYourAnswersAboutFranchiseOrLettings)
            case _                                              => checkYourAnswersAboutFranchiseOrLettingsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAboutFranchiseOrLettings](
      checkYourAnswersAboutFranchiseOrLettingsForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAboutFranchiseOrLettingsView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutFranchisesOrLettings(_.copy(checkYourAnswersAboutFranchiseOrLettings = Some(data)))
          .copy(lastCYAPageUrl =
            Some(
              controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
            )
          )
        session.saveOrUpdate(updatedData).flatMap { _ =>
          Future.successful(
            Redirect(navigator.nextPage(CheckYourAnswersAboutFranchiseOrLettingsId, updatedData).apply(updatedData))
          )
        }
      }
    )
  }

  private def getLettingsIndex(session: Session): Int  =
    session.aboutFranchisesOrLettings.map(_.lettingCurrentIndex).getOrElse(0)
  private def getCateringsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.cateringOperationCurrentIndex).getOrElse(0)
  private def getBackLink(
    answers: Session
  ): String = // TODO Look at the back link logic. Got it loading but I'll come back to it! - Pete
    answers.forType match {
      case FOR6010 | FOR6011 =>
        getBackUrlFor6010and6011(answers)

      case FOR6015 | FOR6016 =>
        answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
          case Some("yes") =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
          case _           =>
            controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
        }
      case FOR6020           =>
        val answersYesNo: Option[AnswersYesNo] =
          answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty)
        answersYesNo match {
          case Some(AnswerYes) =>
            val idx = answers.aboutFranchisesOrLettings.map(_.lettings.map(_.length).getOrElse(0)).getOrElse(0)
            controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController
              .show(if (idx > 0) idx - 1 else idx)
              .url
          case _               => controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url

        }
      case FOR6045 | FOR6046 =>
        answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match {
          case Some(AnswerYes) =>
            controllers.aboutfranchisesorlettings.routes.RentalIncomeListController
              .show(answers.aboutFranchisesOrLettings.map(_.rentalIncomeIndex).getOrElse(0))
              .url
          case _               => controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url
        }
      case _                 =>
        logger.warn(s"Back link reached with unknown enforcement taken value")
        controllers.routes.TaskListController.show().url
    }

  private def getBackUrlFor6010and6011(session: Session): String = {
    val aboutFranchiseOrLettings                          = session.aboutFranchisesOrLettings.get
    def isNoOrNone(answer: Option[AnswersYesNo]): Boolean =
      answer match {
        case Some(AnswerNo) | None => true
        case _                     => false
      }

    aboutFranchiseOrLettings match {
      case AboutFranchisesOrLettings(Some(AnswerYes), Some(AnswerYes), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)
          if aboutFranchiseOrLettings.lettingSections.nonEmpty =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
          .show(getLettingsIndex(session))
          .url

      case AboutFranchisesOrLettings(Some(AnswerYes), Some(AnswerYes), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) =>
        controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show().url

      case AboutFranchisesOrLettings(Some(AnswerYes), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)
          if isNoOrNone(aboutFranchiseOrLettings.lettingOtherPartOfProperty) =>
        controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url

      case AboutFranchisesOrLettings(Some(AnswerYes), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)
          if aboutFranchiseOrLettings.cateringConcessionOrFranchise.contains(
            AnswerYes
          ) && aboutFranchiseOrLettings.cateringOperationSections.nonEmpty =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController
          .show(getCateringsIndex(session))
          .url

      case AboutFranchisesOrLettings(Some(AnswerYes), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)
          if aboutFranchiseOrLettings.cateringConcessionOrFranchise.contains(AnswerYes) =>
        controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url

      case AboutFranchisesOrLettings(Some(AnswerNo), _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) =>
        controllers.aboutfranchisesorlettings.routes.FranchiseOrLettingsTiedToPropertyController.show().url

      case _ =>
        controllers.routes.TaskListController.show().url
    }
  }
}
