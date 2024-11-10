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
import form.aboutthetradinghistory.AccountingCosts6048Form.accountingCosts6048Form
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AccountingCosts6048, TurnoverSection6048}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AccountingCosts6048Id
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.accountingCosts6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AccountingCosts6048Controller @Inject() (
  accountingCosts6048View: accountingCosts6048,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6048 =>
      val years = turnoverSections6048.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        accountingCosts6048View(
          accountingCosts6048Form(years).fill(
            turnoverSections6048.map(_.accountingCosts getOrElse AccountingCosts6048())
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6048 =>
      val years = turnoverSections6048.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[AccountingCosts6048]](
        accountingCosts6048Form(years),
        formWithErrors => BadRequest(accountingCosts6048View(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6048).map { case (accountingCosts, previousSection) =>
              previousSection.copy(
                accountingCosts = Some(accountingCosts)
              )
            }

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6048 = Some(updatedSections)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map(_ => navigator.nextPage(AccountingCosts6048Id, updatedData).apply(updatedData))
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
      case _     => aboutthetradinghistory.routes.FixedCosts6048Controller.show.url
    }

}
