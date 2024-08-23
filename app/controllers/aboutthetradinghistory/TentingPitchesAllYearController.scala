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
import form.aboutthetradinghistory.TentingPitchesAllYearForm.tentingPitchesAllYearForm
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, TentingPitchesAllYear}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TentingPitchesAllYearId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.tentingPitchesAllYear

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class TentingPitchesAllYearController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: tentingPitchesAllYear,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.touringAndTentingPitches)
            .flatMap(_.tentingPitchesAllYear) match {
            case Some(answers) => tentingPitchesAllYearForm.fill(answers)
            case None          => tentingPitchesAllYearForm
          },
          calculateBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TentingPitchesAllYear](
      tentingPitchesAllYearForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink
          )
        ),
      data => {

        val updatedSession = AboutTheTradingHistoryPartOne.updateTouringAndTentingPitches { touringAndTentingPitches =>
          touringAndTentingPitches.copy(tentingPitchesAllYear = Some(data))
        }
        session
          .saveOrUpdate(updatedSession)
          .map(_ =>
            Redirect(
              navigator
                .nextPage6045(TentingPitchesAllYearId, updatedSession, navigator.cyaPageForTentingPitches)
                .apply(updatedSession)
            )
          )
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageForTentingPitches.url
      case _     => controllers.aboutthetradinghistory.routes.TentingPitchesOnSiteController.show().url

    }

}
