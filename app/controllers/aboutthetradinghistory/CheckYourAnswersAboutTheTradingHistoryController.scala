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

package controllers.aboutthetradinghistory

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistoryForm.checkYourAnswersAboutTheTradingHistoryForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.CheckYourAnswersAboutTheTradingHistory
import models.submissions.common.AnswerYes
import models.{ForTypes, Session}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CheckYourAnswersAboutTheTradingHistoryId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.checkYourAnswersAboutTheTradingHistory

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutTheTradingHistoryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  checkYourAnswersAboutTheTradingHistoryView: checkYourAnswersAboutTheTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutTheTradingHistoryView(
          request.sessionData.aboutTheTradingHistory.flatMap(_.checkYourAnswersAboutTheTradingHistory) match {
            case Some(checkYourAnswersAboutTheTradingHistory) =>
              checkYourAnswersAboutTheTradingHistoryForm.fill(checkYourAnswersAboutTheTradingHistory)
            case _                                            => checkYourAnswersAboutTheTradingHistoryForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAboutTheTradingHistory](
      checkYourAnswersAboutTheTradingHistoryForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAboutTheTradingHistoryView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(checkYourAnswersAboutTheTradingHistory = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url)
          )
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(CheckYourAnswersAboutTheTradingHistoryId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case ForTypes.for6010 | ForTypes.for6011 | ForTypes.for6016 =>
        controllers.aboutthetradinghistory.routes.TurnoverController.show().url
      case ForTypes.for6015 | ForTypes.for6030                    =>
        controllers.aboutthetradinghistory.routes.UnusualCircumstancesController.show().url
      case ForTypes.for6020                                       =>
        controllers.aboutthetradinghistory.routes.ElectricVehicleChargingPointsController.show().url
      case ForTypes.for6045 | ForTypes.for6046                    =>
        if (
          answers.aboutTheTradingHistoryPartOne
            .flatMap(_.caravans)
            .flatMap(_.anyStaticLeisureCaravansOnSite)
            .contains(AnswerYes)
        )
          // TODO: What is the current annual pitch fee for a single-unit static caravan
          controllers.aboutthetradinghistory.routes.TwinUnitCaravansOwnedByOperatorController.show().url
        else
          controllers.aboutthetradinghistory.routes.StaticCaravansController.show().url
      case ForTypes.for6076                                       =>
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      case _                                                      =>
        logger.warn(s"Back link for enforcement action page reached with unknown enforcement taken value")
        controllers.routes.TaskListController.show().url
    }
}
