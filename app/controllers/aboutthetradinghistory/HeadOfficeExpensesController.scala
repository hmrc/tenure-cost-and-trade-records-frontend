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
import form.aboutthetradinghistory.HeadOfficeExpensesForm.headOfficeExpensesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.TurnoverSection6076
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.HeadOfficeExpensesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.headOfficeExpenses6076

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Head office expenses - 6076.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class HeadOfficeExpensesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  headOfficeExpensesView: headOfficeExpenses6076,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("HeadOfficeExpenses")

    runWithSessionCheck { turnoverSections6076 =>
      val years                = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)
      val informationOrRemarks =
        request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.furtherInformationOrRemarks)

      Ok(
        headOfficeExpensesView(
          headOfficeExpensesForm(years).fill(
            (turnoverSections6076.map(_.headOfficeExpenses), informationOrRemarks)
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val years = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[(Seq[Option[BigDecimal]], Option[String])](
        headOfficeExpensesForm(years),
        formWithErrors => BadRequest(headOfficeExpensesView(formWithErrors, getBackLink)),
        success => {
          val updatedSections      =
            (success._1 zip turnoverSections6076).map { case (headOfficeExpenses, previousSection) =>
              previousSection.copy(headOfficeExpenses = headOfficeExpenses)
            }
          val informationOrRemarks = success._2

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections),
              furtherInformationOrRemarks = informationOrRemarks
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(HeadOfficeExpensesId, updatedData).apply(updatedData))
            }
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6076] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "IES" =>
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      case _     =>
        aboutthetradinghistory.routes.OperationalExpensesController.show().url
    }

}
