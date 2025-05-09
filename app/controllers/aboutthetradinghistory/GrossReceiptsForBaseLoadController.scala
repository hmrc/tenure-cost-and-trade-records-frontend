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
import connectors.Audit
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.GrossReceiptsForBaseLoadForm.grossReceiptsForBaseLoadForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{GrossReceiptsForBaseLoad, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossReceiptsForBaseLoadId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.grossReceiptsForBaseLoad

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GrossReceiptsForBaseLoadController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: grossReceiptsForBaseLoad,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("GrossReceiptsForBaseLoad")

    runWithSessionCheck { case (turnoverSections6076, years) =>
      val grossReceiptsForBaseLoad = turnoverSections6076.flatMap(_.grossReceiptsForBaseLoad)
      Ok(
        view(
          grossReceiptsForBaseLoadForm(years).fill(grossReceiptsForBaseLoad),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { case (turnoverSections6076, years) =>
      continueOrSaveAsDraft[Seq[GrossReceiptsForBaseLoad]](
        grossReceiptsForBaseLoadForm(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6076).map { case (updatedGrossReceiptsForBaseLoad, turnoverSection) =>
              turnoverSection.copy(grossReceiptsForBaseLoad = Some(updatedGrossReceiptsForBaseLoad))
            }
          val updatedData     = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections)
            )
          )
          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(GrossReceiptsForBaseLoadId, updatedData).apply(updatedData))
            }
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: (Seq[TurnoverSection6076], Seq[String]) => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show()))) { turnoverSections6076 =>
        val years = turnoverSections6076.map(_.financialYearEnd.getYear.toString)
        action(turnoverSections6076, years)
      }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "IES" =>
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      case _     =>
        aboutthetradinghistory.routes.GrossReceiptsExcludingVATController.show().url
    }

}
