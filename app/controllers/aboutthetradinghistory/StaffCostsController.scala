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
import form.aboutthetradinghistory.StaffCostsForm.staffCostsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{StaffCosts, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.StaffCostsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.staffCosts
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StaffCostsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: staffCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("StaffCosts")

    runWithSessionCheck { case (turnoverSections6076, years) =>
      val staffCosts = turnoverSections6076.flatMap(_.staffCosts)
      Ok(
        view(
          staffCostsForm(years).fill(staffCosts),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { case (turnoverSections6076, years) =>
      continueOrSaveAsDraft[Seq[StaffCosts]](
        staffCostsForm(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6076).map { case (updatedCosts, turnoverSection) =>
              turnoverSection.copy(staffCosts = Some(updatedCosts))
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
                .getOrElse(navigator.nextPage(StaffCostsId, updatedData).apply(updatedData))
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
    val intermittentCheck =
      request.sessionData.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))

    intermittentCheck match {
      case Some("intermittent") =>
        navigator.from match {
          case "CYA" =>
            aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
          case "IES" =>
            controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
          case _     =>
            aboutthetradinghistory.routes.CostOfSales6076IntermittentController.show().url
        }
      case Some("baseload")     =>
        navigator.from match {
          case "CYA" =>
            aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
          case "IES" =>
            controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
          case _     =>
            aboutthetradinghistory.routes.CostOfSales6076Controller.show().url
        }
      case _                    => controllers.routes.TaskListController.show().url
    }

}
