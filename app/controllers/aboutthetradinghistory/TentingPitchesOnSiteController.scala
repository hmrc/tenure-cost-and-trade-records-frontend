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
import form.aboutthetradinghistory.TentingPitchesOnSiteForm.tentingPitchesOnSiteForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.AnswersYesNo
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TentingPitchesOnSiteId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.tentingPitchesOnSite

import javax.inject.{Inject, Named}
import scala.concurrent.Future

class TentingPitchesOnSiteController @Inject() (
                                              mcc: MessagesControllerComponents,
                                              navigator: AboutTheTradingHistoryNavigator,
                                              view: tentingPitchesOnSite,
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
            .flatMap(_.tentingPitchesOnSite)
          match {
            case Some(answers) => tentingPitchesOnSiteForm.fill(answers)
            case None          => tentingPitchesOnSiteForm
          },
          calculateBackLink,
          request.sessionData.toSummary
        )
      )
    )
  }


  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      tentingPitchesOnSiteForm,
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
          touringAndTentingPitches.copy(tentingPitchesOnSite = Some(data))
        }

        session.saveOrUpdate(updatedSession)
        Redirect(navigator.nextPage(TentingPitchesOnSiteId, updatedSession).apply(updatedSession))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#" // TODO with TaskList ticket for 6045
      case _     => controllers.routes.TaskListController.show().url       // TODO with ticket for other holiday CYA page

    }

}