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
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.FinancialYearEndDateForm.financialYearEndDateForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne, CostOfSales, OccupationalAndAccountingInformation}
import models.{ForTypes, Session}
import navigation.AboutTheTradingHistoryNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.editFinancialYearEndDate

import java.time.{LocalDate, MonthDay}
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EditFinancialYearEndDateController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  editFinancialYearEndDateView: editFinancialYearEndDate,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.map(_.financialYear).isDefined)
      .filter(isTurnOverNonEmpty(_))
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val financialYearEnd: Seq[LocalDate] = {
          request.sessionData.forType match {
            case ForTypes.for6020 =>
              aboutTheTradingHistory.turnoverSections6020.fold(Seq.empty[LocalDate])(_.map(_.financialYearEnd))
            case ForTypes.for6030 => aboutTheTradingHistory.turnoverSections6030.map(_.financialYearEnd)
            case ForTypes.for6076 =>
              request.sessionData.aboutTheTradingHistoryPartOne
                .flatMap(_.turnoverSections6076)
                .fold(Seq.empty[LocalDate])(_.map(_.financialYearEnd))
            case _                => aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)
          }
        }
        val prefilledForm = financialYearEndDateForm.fill(financialYearEnd.lift(index).get)
        Ok(
          editFinancialYearEndDateView(
            prefilledForm,
            index
          )
        )
      }
  }

  private def isTurnOverNonEmpty(
    aboutTheTradingHistory: AboutTheTradingHistory
  )(implicit request: SessionRequest[AnyContent]): Boolean =
    request.sessionData.forType match {
      case ForTypes.for6020 => aboutTheTradingHistory.turnoverSections6020.exists(_.nonEmpty)
      case ForTypes.for6030 => aboutTheTradingHistory.turnoverSections6030.nonEmpty
      case ForTypes.for6076 =>
        request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6076).exists(_.nonEmpty)
      case _                => aboutTheTradingHistory.turnoverSections.nonEmpty
    }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val financialYearEnd: Seq[LocalDate] = {
      request.sessionData.forType match {
        case ForTypes.for6020 =>
          request.sessionData.aboutTheTradingHistory.get.turnoverSections6020
            .fold(Seq.empty[LocalDate])(_.map(_.financialYearEnd))
        case ForTypes.for6030 =>
          request.sessionData.aboutTheTradingHistory.get.turnoverSections6030.map(_.financialYearEnd)
        case ForTypes.for6076 =>
          request.sessionData.aboutTheTradingHistoryPartOne
            .flatMap(_.turnoverSections6076)
            .fold(Seq.empty[LocalDate])(_.map(_.financialYearEnd))
        case _                => request.sessionData.aboutTheTradingHistory.get.turnoverSections.map(_.financialYearEnd)
      }
    }
    val prefilledForm = financialYearEndDateForm.fill(financialYearEnd.lift(index).get)
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.map(_.financialYear).isDefined)
      .filter(isTurnOverNonEmpty(_))
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        continueOrSaveAsDraft[LocalDate](
          prefilledForm,
          formWithErrors =>
            BadRequest(
              editFinancialYearEndDateView(
                formWithErrors,
                index
              )
            ),
          data => {
            val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
            val financialYearEnd        = occupationAndAccounting.financialYear.get.toMonthDay

            val newOccupationAndAccounting =
              if (MonthDay.of(data.getMonthValue, data.getDayOfMonth) == financialYearEnd) {
                occupationAndAccounting
              } else {
                occupationAndAccounting.copy(yearEndChanged = Some(true))
              }

            val updatedData: Session = request.sessionData.forType match {
              case ForTypes.for6020 =>
                buildUpdateData6020(aboutTheTradingHistory, index, data, newOccupationAndAccounting)
              case ForTypes.for6030 =>
                buildUpdateData6030(aboutTheTradingHistory, index, data, newOccupationAndAccounting)
              case ForTypes.for6076 => buildUpdatedData6076(index, data, newOccupationAndAccounting)
              case _                => buildUpdateData(aboutTheTradingHistory, index, data, newOccupationAndAccounting)
            }
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ =>
                    navigator.from == "CYA" && isTurnoverSectionStarted(updatedData.aboutTheTradingHistory.get)
                  )
                  .getOrElse(routes.FinancialYearEndDatesSummaryController.show())
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
      case ForTypes.for6020 =>
        aboutTheTradingHistory.turnoverSections6020.flatMap(_.headOption).exists(_.shop.isDefined)
      case ForTypes.for6030 => aboutTheTradingHistory.turnoverSections6030.head.grossIncome.isDefined
      case ForTypes.for6076 =>
        request.sessionData.aboutTheTradingHistoryPartOne
          .flatMap(_.turnoverSections6076)
          .flatMap(_.headOption)
          .exists(_.electricityGenerated.isDefined)
      case _                => aboutTheTradingHistory.turnoverSections.head.alcoholicDrinks.isDefined
    }

  private def buildUpdateData(
    aboutTheTradingHistory: AboutTheTradingHistory,
    index: Int,
    data: LocalDate,
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]): Session = {
    val turnoverSections =
      aboutTheTradingHistory.turnoverSections
        .updated(index, aboutTheTradingHistory.turnoverSections(index).copy(financialYearEnd = data))

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

  private def buildUpdateData6020(
    aboutTheTradingHistory: AboutTheTradingHistory,
    index: Int,
    data: LocalDate,
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]): Session = {
    val turnoverSections6020    = aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty)
    val updatedTurnoverSections = turnoverSections6020.updated(
      index,
      turnoverSections6020(index).copy(financialYearEnd = data)
    )
    val updatedData             = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections6020 = Some(updatedTurnoverSections)
      )
    )
    updatedData
  }

  private def buildUpdateData6030(
    aboutTheTradingHistory: AboutTheTradingHistory,
    index: Int,
    data: LocalDate,
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]): Session = {
    val turnoverSections6030       = aboutTheTradingHistory.turnoverSections6030
    val updatedTurnoverSection6030 =
      turnoverSections6030.updated(index, turnoverSections6030(index).copy(financialYearEnd = data))

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections6030 = updatedTurnoverSection6030
      )
    )
    updatedData
  }

  private def buildUpdatedData6076(
    index: Int,
    data: LocalDate,
    newOccupationAndAccounting: OccupationalAndAccountingInformation
  )(implicit request: SessionRequest[AnyContent]): Session = {
    val turnoverSections6076    =
      request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.turnoverSections6076).getOrElse(Seq.empty)
    val updatedTurnoverSections = turnoverSections6076.updated(
      index,
      turnoverSections6076(index).copy(financialYearEnd = data)
    )

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting)
      )
    )
    updatedData.copy(
      aboutTheTradingHistoryPartOne = Some(
        updatedData.aboutTheTradingHistoryPartOne
          .getOrElse(AboutTheTradingHistoryPartOne())
          .copy(turnoverSections6076 = Some(updatedTurnoverSections))
      )
    )
  }

}
