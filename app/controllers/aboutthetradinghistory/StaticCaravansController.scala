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
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.StaticCaravansForm.staticCaravansForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateCaravans
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.StaticCaravansId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.staticCaravans

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * 6045/6046 Trading history - Static caravans page.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class StaticCaravansController @Inject() (
  staticCaravansView: staticCaravans,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("StaticCaravans")

    Ok(
      staticCaravansView(
        savedAnswer.fold(staticCaravansForm)(staticCaravansForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      staticCaravansForm,
      formWithErrors => BadRequest(staticCaravansView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateCaravans(_.copy(anyStaticLeisureCaravansOnSite = Some(data)))

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ => navigator.from == "CYA" && (data == AnswerNo || savedAnswer.contains(AnswerYes)))
              .getOrElse(navigator.nextWithoutRedirectToCYA(StaticCaravansId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }

  private def savedAnswer(implicit
    request: SessionRequest[AnyContent]
  ): Option[AnswersYesNo] = request.sessionData.aboutTheTradingHistoryPartOne
    .flatMap(_.caravans)
    .flatMap(_.anyStaticLeisureCaravansOnSite)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        navigator.cyaPage
          .getOrElse(aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show())
          .url
      case _     => aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
    }

}
