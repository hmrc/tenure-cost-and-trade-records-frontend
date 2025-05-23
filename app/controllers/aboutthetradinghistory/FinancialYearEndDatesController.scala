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
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.FinancialYearEndDatesForm.financialYearEndDatesForm
import models.ForType.*
import models.Session
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, CostOfSales, OccupationalAndAccountingInformation}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndDatesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil.financialYearsList
import views.html.aboutthetradinghistory.financialYearEndDates

import java.time.{LocalDate, MonthDay}
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialYearEndDatesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  financialYearEndDatesView: financialYearEndDates,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("FinancialYearEndDates")

    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.map(_.currentFinancialYearEnd).isDefined)
      .filter(isTurnOverNonEmpty(_))
      .fold(Redirect(routes.WhenDidYouFirstOccupyController.show())) { aboutTheTradingHistory =>
        val occupationAndAccounting          = aboutTheTradingHistory.occupationAndAccountingInformation.get
        val financialYearEnd: Seq[LocalDate] =
          request.sessionData.forType match {
            case FOR6030 => aboutTheTradingHistory.turnoverSections6030.map(_.financialYearEnd)
            case _       => aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)
          }
        Ok(
          financialYearEndDatesView(
            financialYearEndDatesForm().fill(financialYearEnd),
            financialYearsList(occupationAndAccounting)
          )
        )
      }
  }

  private def isTurnOverNonEmpty(
    aboutTheTradingHistory: AboutTheTradingHistory
  )(implicit request: SessionRequest[AnyContent]): Boolean =
    request.sessionData.forType match {
      case FOR6030 => aboutTheTradingHistory.turnoverSections6030.nonEmpty
      case _       => aboutTheTradingHistory.turnoverSections.nonEmpty
    }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.map(_.currentFinancialYearEnd).isDefined)
      .filter(isTurnOverNonEmpty(_))
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show()))) { aboutTheTradingHistory =>
        val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
        continueOrSaveAsDraft[Seq[LocalDate]](
          financialYearEndDatesForm(Some(financialYearsList(occupationAndAccounting))),
          formWithErrors =>
            BadRequest(
              financialYearEndDatesView(
                formWithErrors,
                financialYearsList(occupationAndAccounting)
              )
            ),
          data => {
            val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
            val financialYearEnd        = occupationAndAccounting.currentFinancialYearEnd.get.toMonthDay

            val newOccupationAndAccounting =
              if (data.forall(d => MonthDay.of(d.getMonthValue, d.getDayOfMonth) == financialYearEnd)) {
                occupationAndAccounting
              } else {
                occupationAndAccounting.copy(financialYearEndHasChanged = Some(true))
              }

            val updatedData: Session = request.sessionData.forType match {
              case FOR6030 => buildUpdateData6030(aboutTheTradingHistory, data, newOccupationAndAccounting)
              case _       => buildUpdateData(aboutTheTradingHistory, data, newOccupationAndAccounting)
            }
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ =>
                    navigator.from == "CYA" && isTurnoverSectionStarted(updatedData.aboutTheTradingHistory.get)
                  )
                  .getOrElse(navigator.nextPage(FinancialYearEndDatesPageId, updatedData).apply(updatedData))
              )
              .map(Redirect)
          }
        )
      }
  }

  private def isTurnoverSectionStarted(
    aboutTheTradingHistory: AboutTheTradingHistory
  )(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.forType match {
      case FOR6030 => aboutTheTradingHistory.turnoverSections6030.headOption.flatMap(_.grossIncome).isDefined
      case _       => aboutTheTradingHistory.turnoverSections.headOption.flatMap(_.alcoholicDrinks).isDefined
    }

  private def buildUpdateData(
    aboutTheTradingHistory: AboutTheTradingHistory,
    data: Seq[LocalDate],
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]) = {
    val turnoverSections =
      (aboutTheTradingHistory.turnoverSections zip data).map { case (turnoverSection, finYearEnd) =>
        turnoverSection.copy(financialYearEnd = finYearEnd)
      }

    val costOfSales = if (aboutTheTradingHistory.costOfSales.size == turnoverSections.size) {
      (aboutTheTradingHistory.costOfSales zip turnoverSections.map(_.financialYearEnd)).map {
        case (costOfSales, finYearEnd) => costOfSales.copy(financialYearEnd = finYearEnd)
      }
    } else {
      turnoverSections.map(_.financialYearEnd).map(CostOfSales(_, None, None, None, None))
    }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections = turnoverSections,
        costOfSales = costOfSales
      )
    )
    updatedData
  }

  private def buildUpdateData6030(
    aboutTheTradingHistory: AboutTheTradingHistory,
    data: Seq[LocalDate],
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]) = {
    val turnoverSections6030 =
      (aboutTheTradingHistory.turnoverSections6030 zip data).map { case (turnoverSection6030, finYearEnd) =>
        turnoverSection6030.copy(financialYearEnd = finYearEnd)
      }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections6030 = turnoverSections6030
      )
    )
    updatedData
  }

}
