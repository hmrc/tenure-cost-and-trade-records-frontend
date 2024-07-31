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
import form.aboutthetradinghistory.TentingPitchesTotalForm.tentingPitchesTotalForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TentingPitchesTotalId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.tentingPitchesTotal

import javax.inject.{Inject, Named}
import scala.concurrent.Future

class TentingPitchesTotalController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: tentingPitchesTotal,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.touringAndTentingPitches)
            .flatMap(_.tentingPitchesTotal) match {
            case Some(answers) => tentingPitchesTotalForm.fill(answers)
            case None          => tentingPitchesTotalForm
          },
          calculateBackLink,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Int](
      tentingPitchesTotalForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {

        val updatedSession = AboutTheTradingHistoryPartOne.updateTouringAndTentingPitches { touringAndTentingPitches =>
          touringAndTentingPitches.copy(tentingPitchesTotal = Some(data))
        }

        session.saveOrUpdate(updatedSession)
        Redirect(navigator.nextPageForTentingPitches(TentingPitchesTotalId, updatedSession).apply(updatedSession))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersTentingPitchesController.show().url
      case _     => controllers.aboutthetradinghistory.routes.RallyAreasController.show().url

    }
}
