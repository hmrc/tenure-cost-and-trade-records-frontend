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
import form.aboutthetradinghistory.TentingPitchesTradingDataForm.tentingPitchesTradingDataForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{TentingPitchesTradingData, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.PitchesForCaravansId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.pitchesForCaravans

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class PitchesForCaravansController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: pitchesForCaravans,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        view(
          tentingPitchesTradingDataForm(years).fill(
            turnoverSections6045.map(_.pitchesForCaravans getOrElse TentingPitchesTradingData())
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[TentingPitchesTradingData]](
        tentingPitchesTradingDataForm(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6045).map { case (data, previousSection) =>
              previousSection.copy(pitchesForCaravans = Some(data))
            }

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6045 = Some(updatedSections)
            )
          )

          session.saveOrUpdate(updatedData).map { _ =>
            Redirect(
              navigator
                .nextPage6045(
                  PitchesForCaravansId,
                  updatedData,
                  navigator.cyaPageForTentingPitches
                )
                .apply(updatedData)
            )
          }
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6045] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6045)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" => navigator.cyaPageForTentingPitches.url
      case _     => controllers.aboutthetradinghistory.routes.TentingPitchesAllYearController.show().url
    }

}
