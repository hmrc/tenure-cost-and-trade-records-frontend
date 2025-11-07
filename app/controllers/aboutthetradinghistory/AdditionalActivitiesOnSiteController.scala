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
import form.aboutthetradinghistory.AdditionalActivitiesOnSiteForm.additionalActivitiesOnSiteForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AdditionalActivitiesOnSiteId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.additionalActivitiesOnSite

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class AdditionalActivitiesOnSiteController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: additionalActivitiesOnSite,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AdditionalActivitiesOnSite")

    Future.successful(
      Ok(
        view(
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.additionalActivities)
            .flatMap(_.additionalActivitiesOnSite) match {
            case Some(answers) => additionalActivitiesOnSiteForm.fill(answers)
            case None          => additionalActivitiesOnSiteForm
          },
          calculateBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      additionalActivitiesOnSiteForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink
          )
        ),
      data => {
        val previousAnswer = request.sessionData.aboutTheTradingHistoryPartOne
          .flatMap(_.additionalActivities)
          .flatMap(_.additionalActivitiesOnSite)

        val updatedSession = AboutTheTradingHistoryPartOne.updateAdditionalActivities { additionalActivities =>
          additionalActivities.copy(additionalActivitiesOnSite = Some(data))
        }
        session
          .saveOrUpdate(updatedSession)
          .map { _ =>
            val nextPage = (previousAnswer, data, navigator.from) match {
              case (Some(AnswerYes), AnswerYes, "CYA") =>
                controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show()
              case (Some(AnswerNo), AnswerYes, _)      =>
                controllers.aboutthetradinghistory.routes.AdditionalShopsController.show()
              case _                                   =>
                navigator
                  .nextPage6045(
                    AdditionalActivitiesOnSiteId,
                    updatedSession,
                    navigator.cyaPageForAdditionalActivities
                  )
                  .apply(updatedSession)
            }
            Redirect(nextPage)
          }
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageForAdditionalActivities.url
      case "TL"  => controllers.routes.TaskListController.show().url + "#additional-activities-on-site"
      case _     => controllers.routes.TaskListController.show().url

    }
}
