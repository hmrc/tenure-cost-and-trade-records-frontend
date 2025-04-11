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

package controllers.aboutthetradinghistory

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.RallyAreasTradingDataForm.rallyAreasTradingDataForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{RallyAreasTradingData, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.RallyAreasId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.rallyAreas

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class RallyAreasController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: rallyAreas,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("RallyAreas")

    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        view(
          rallyAreasTradingDataForm(years).fill(
            turnoverSections6045.map(section => section.rallyAreas.getOrElse(RallyAreasTradingData()))
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[RallyAreasTradingData]](
        rallyAreasTradingDataForm(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6045).map { case (data, previousSection) =>
              previousSection.copy(rallyAreas = Some(data))
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
                  RallyAreasId,
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
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" => navigator.cyaPageForTentingPitches.url
      case _     => controllers.aboutthetradinghistory.routes.PitchesForGlampingController.show().url
    }

}
