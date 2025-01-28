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
import form.aboutthetradinghistory.OtherIncomeForm.otherIncomeForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.TurnoverSection6076
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OtherIncomeId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.otherIncome6076
import controllers.toOpt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Other income - 6076.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class OtherIncomeController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  otherIncomeView: otherIncome6076,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("OtherIncome")

    runWithSessionCheck { turnoverSections6076 =>
      val years   = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)
      val details = request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.otherIncomeDetails).getOrElse("")

      Ok(
        otherIncomeView(
          otherIncomeForm(years).fill(
            (turnoverSections6076.map(_.otherIncome), details)
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val years = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[(Seq[Option[BigDecimal]], String)](
        otherIncomeForm(years),
        formWithErrors => BadRequest(otherIncomeView(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success._1 zip turnoverSections6076).map { case (otherIncome, previousSection) =>
              previousSection.copy(otherIncome = otherIncome)
            }
          val details         = success._2

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections),
              otherIncomeDetails = Option(details).filter(_.nonEmpty)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(OtherIncomeId, updatedData).apply(updatedData))
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
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    val intermittentCheck =
      request.sessionData.aboutYouAndTheProperty.flatMap(_.renewablesPlant.flatMap(_.renewablesPlant.name))

    intermittentCheck match {
      case Some("intermittent") =>
        navigator.from match {
          case "CYA" =>
            controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
          case "IES" =>
            controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
          case _     =>
            aboutthetradinghistory.routes.GrossReceiptsExcludingVATController.show().url
        }
      case Some("baseload")     =>
        navigator.from match {
          case "CYA" =>
            controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
          case "IES" =>
            controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
          case _     =>
            aboutthetradinghistory.routes.GrossReceiptsForBaseLoadController.show().url
        }
      case _                    => controllers.routes.TaskListController.show().url
    }

}
