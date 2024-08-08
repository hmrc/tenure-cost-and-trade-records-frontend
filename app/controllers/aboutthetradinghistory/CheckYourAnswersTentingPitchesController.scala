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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.CheckYourAnswersTentingPitchesForm.checkYourAnswersTentingPitchesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CheckYourAnswersTentingPitchesId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.checkYourAnswersTentingPitches

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersTentingPitchesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: checkYourAnswersTentingPitches,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.touringAndTentingPitches.flatMap(_.checkYourAnswersTentingPitches)) match {
            case Some(checkYourAnswersAboutTheTradingHistory) =>
              checkYourAnswersTentingPitchesForm.fill(checkYourAnswersAboutTheTradingHistory)
            case _                                            => checkYourAnswersTentingPitchesForm
          },
          calculateBackLink
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      checkYourAnswersTentingPitchesForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink
          )
        ),
      data => {

        val updatedSession = AboutTheTradingHistoryPartOne.updateTouringAndTentingPitches { touringAndTentingPitches =>
          touringAndTentingPitches.copy(checkYourAnswersTentingPitches = Some(data))
        }
        session.saveOrUpdate(updatedSession).map { _ =>
          Redirect(navigator.nextPage(CheckYourAnswersTentingPitchesId, updatedSession).apply(updatedSession))
        }
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.aboutTheTradingHistoryPartOne.flatMap(
      _.touringAndTentingPitches.flatMap(_.tentingPitchesOnSite)
    ) match {
      case Some(AnswerYes) => controllers.aboutthetradinghistory.routes.TentingPitchesCertificatedController.show().url
      case Some(AnswerNo)  => controllers.aboutthetradinghistory.routes.TentingPitchesOnSiteController.show().url
      case _               => controllers.routes.TaskListController.show().url
    }

}
