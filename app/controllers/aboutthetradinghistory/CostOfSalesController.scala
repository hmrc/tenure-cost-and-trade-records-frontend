/*
 * Copyright 2023 HM Revenue & Customs
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
import form.aboutthetradinghistory.CostOfSalesForm.costOfSalesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, CostOfSales}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CostOfSalesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.costOfSales

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CostOfSalesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  costOfSalesView: costOfSales,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CostOfSales")

    runWithSessionCheck { aboutTheTradingHistory =>
      val costOfSales = if (aboutTheTradingHistory.costOfSales.size == aboutTheTradingHistory.turnoverSections.size) {
        (aboutTheTradingHistory.costOfSales zip financialYearEndDates(aboutTheTradingHistory)).map {
          case (costOfSales, finYearEnd) => costOfSales.copy(financialYearEnd = finYearEnd)
        }
      } else {
        financialYearEndDates(aboutTheTradingHistory).map(CostOfSales(_, None, None, None, None))
      }

      val updatedData = updateAboutTheTradingHistory(_.copy(costOfSales = costOfSales))
      session
        .saveOrUpdate(updatedData)
        .flatMap(_ =>
          Ok(costOfSalesView(costOfSalesForm(years(aboutTheTradingHistory)).fill(costOfSales), navigator.from))
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { aboutTheTradingHistory =>
      continueOrSaveAsDraft[Seq[CostOfSales]](
        costOfSalesForm(years(aboutTheTradingHistory)),
        formWithErrors => BadRequest(costOfSalesView(formWithErrors, navigator.from)),
        data => {
          val costOfSales =
            (data zip financialYearEndDates(aboutTheTradingHistory)).map { case (costOfSales, finYearEnd) =>
              costOfSales.copy(financialYearEnd = finYearEnd)
            }

          val updatedData = updateAboutTheTradingHistory(_.copy(costOfSales = costOfSales))
          session
            .saveOrUpdate(updatedData)
            .map(_ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && aboutTheTradingHistory.totalPayrollCostSections.nonEmpty)
                .getOrElse(navigator.nextPage(CostOfSalesId, updatedData).apply(updatedData))
            )
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: AboutTheTradingHistory => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .filter(_.turnoverSections.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)

  private def years(aboutTheTradingHistory: AboutTheTradingHistory): Seq[String] =
    financialYearEndDates(aboutTheTradingHistory).map(_.getYear.toString)

}
