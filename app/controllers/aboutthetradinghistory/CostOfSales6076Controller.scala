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
import form.aboutthetradinghistory.CostOfSales6076Form.costOfSales6076Form
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{CostOfSales6076Sum, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CostOfSales6076Id
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.costOfSales6076

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CostOfSales6076Controller @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: costOfSales6076,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CostOfSales6076")

    runWithSessionCheck { turnoverSections6076 =>
      val years                      = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)
      val costOfSales                = turnoverSections6076.flatMap(_.costOfSales6076Sum)
      val costOfSalesDetails: String =
        request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.otherSalesDetails).getOrElse("")
      Ok(
        view(
          costOfSales6076Form(years).fill((costOfSales, costOfSalesDetails)),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val years = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[(Seq[CostOfSales6076Sum], String)](
        costOfSales6076Form(years),
        formWithErrors => BadRequest(view(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success._1 zip turnoverSections6076).map { case (costOfSales, turnoverSection) =>
              turnoverSection.copy(costOfSales6076Sum = Some(costOfSales))
            }
          val details         = success._2

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections),
              otherSalesDetails = Option(details).filter(_.nonEmpty)
            )
          )
          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(CostOfSales6076Id, updatedData).apply(updatedData))
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
        aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "IES" =>
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummary6076Controller.show().url
      case _     =>
        aboutthetradinghistory.routes.OtherIncomeController.show().url
    }

}
