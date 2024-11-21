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
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.OperationalCosts6048Form.operationalCosts6048Form
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{OperationalCosts6048, TurnoverSection6048}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OperationalCosts6048Id
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.operationalCosts6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class OperationalCosts6048Controller @Inject() (
  operationalCosts6048View: operationalCosts6048,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6048 =>
      val years   = turnoverSections6048.map(_.financialYearEnd).map(_.getYear.toString)
      val details =
        request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.otherOperationalExpensesDetails).getOrElse("")

      Ok(
        operationalCosts6048View(
          operationalCosts6048Form(years).fill(
            (turnoverSections6048.map(_.operationalCosts getOrElse OperationalCosts6048()), details)
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6048 =>
      val years = turnoverSections6048.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[(Seq[OperationalCosts6048], String)](
        operationalCosts6048Form(years),
        formWithErrors => BadRequest(operationalCosts6048View(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success._1 zip turnoverSections6048).map { case (operationalCosts, previousSection) =>
              previousSection.copy(
                operationalCosts = Some(operationalCosts)
              )
            }
          val details         = success._2

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6048 = Some(updatedSections),
              otherOperationalExpensesDetails = Option(details).filter(_.nonEmpty)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map(_ => navigator.nextPage(OperationalCosts6048Id, updatedData).apply(updatedData))
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6048] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6048)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case _     => aboutthetradinghistory.routes.AdministrativeCosts6048Controller.show.url
    }

}
