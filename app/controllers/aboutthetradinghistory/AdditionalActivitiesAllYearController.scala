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
import form.aboutthetradinghistory.AdditionalActivitiesAllYearForm.additionalActivitiesAllYearForm
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, AdditionalActivitiesAllYear}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AdditionalActivitiesAllYearId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.additionalActivitiesAllYear

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class AdditionalActivitiesAllYearController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: additionalActivitiesAllYear,
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
            .flatMap(_.additionalActivities)
            .flatMap(_.additionalActivitiesAllYear) match {
            case Some(answers) => additionalActivitiesAllYearForm.fill(answers)
            case None          => additionalActivitiesAllYearForm
          },
          calculateBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AdditionalActivitiesAllYear](
      additionalActivitiesAllYearForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink
          )
        ),
      data => {

        val updatedSession = AboutTheTradingHistoryPartOne.updateAdditionalActivities { additionalActivities =>
          additionalActivities.copy(additionalActivitiesAllYear = Some(data))
        }
        session
          .saveOrUpdate(updatedSession)
          .map(_ =>
            Redirect(
              navigator
                .nextPage6045(AdditionalActivitiesAllYearId, updatedSession, navigator.cyaPageForAdditionalActivities)
                .apply(updatedSession)
            )
          )
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageForAdditionalActivities.url
      case _     => controllers.aboutthetradinghistory.routes.AdditionalActivitiesOnSiteController.show().url

    }

}
