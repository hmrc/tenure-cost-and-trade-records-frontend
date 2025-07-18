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
import form.aboutthetradinghistory.BunkeredFuelQuestionForm.bunkeredFuelQuestionForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.common.AnswersYesNo
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.BunkeredFuelQuestionId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.bunkeredFuelQuestion

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BunkeredFuelQuestionController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: bunkeredFuelQuestion,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("BunkeredFuelQuestion")

    Future.successful(
      Ok(
        view(
          request.sessionData.aboutTheTradingHistory
            .flatMap(_.bunkeredFuelQuestion)
            .fold(bunkeredFuelQuestionForm)(bunkeredFuelQuestionForm.fill),
          calculateBackLink(using request),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      bunkeredFuelQuestionForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink(using request),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(bunkeredFuelQuestion = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(BunkeredFuelQuestionId, updatedData).apply(updatedData)))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#bunkered-fuel"
      case _     => controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
    }

}
