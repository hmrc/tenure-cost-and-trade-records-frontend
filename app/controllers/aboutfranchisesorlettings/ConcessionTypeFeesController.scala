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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.FeeReceivedForm.feeReceivedForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{ConcessionIncomeRecord, FeeReceived, FeeReceivedPerYear}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.ConcessionTypeFeesId
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.feeReceived

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConcessionTypeFeesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  view: feeReceived,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck(idx) { concession =>
      val years = financialYearEndDates.map(_.getYear.toString)
      audit.sendChangeLink("ConcessionTypeFees")

      Ok(
        view(
          concession.feeReceived
            .filter(_.feeReceivedPerYear.size == years.size)
            .fold(feeReceivedForm(years).fill(initialFeeReceived))(feeReceivedForm(years).fill),
          idx,
          concession.businessDetails.map(_.operatorName).getOrElse(""),
          backLink(idx).url
        )
      )
    }
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck(idx) { concession =>
      val yearEndDates = financialYearEndDates
      val years        = yearEndDates.map(_.getYear.toString)

      continueOrSaveAsDraft[FeeReceived](
        feeReceivedForm(years),
        formWithErrors =>
          BadRequest(
            view(
              formWithErrors,
              idx,
              concession.businessDetails.map(_.operatorName).getOrElse(""),
              backLink(idx).url
            )
          ),
        data =>
          if (data.feeReceivedPerYear.size == yearEndDates.size) {
            val feeReceivedSeq = (data.feeReceivedPerYear zip yearEndDates).map { case (feePerYear, finYearEnd) =>
              feePerYear.copy(financialYearEnd = finYearEnd)
            }

            val updatedFeeReceived = data.copy(feeReceivedPerYear = feeReceivedSeq)

            val updatedSections = request.sessionData.aboutFranchisesOrLettings
              .map(_.rentalIncome.getOrElse(IndexedSeq.empty))
              .map(_.updated(idx, concession.copy(feeReceived = Some(updatedFeeReceived))))
              .get

            val updatedData =
              updateAboutFranchisesOrLettings(_.copy(rentalIncome = Some(updatedSections)))
            session.saveOrUpdate(updatedData).map { _ =>
              Redirect(navigator.nextPage(ConcessionTypeFeesId, updatedData).apply(updatedData))
            }
          } else {
            Redirect(controllers.aboutfranchisesorlettings.routes.FeeReceivedController.show(idx))
          }
      )
    }
  }

  private def runWithSessionCheck(idx: Int)(
    action: ConcessionIncomeRecord => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result]                           =
    if (request.sessionData.aboutTheTradingHistoryPartOne.map(_.turnoverSections6045).exists(_.nonEmpty)) {
      request.sessionData.aboutFranchisesOrLettings
        .flatMap(_.rentalIncome.flatMap(_.lift(idx)))
        .collect { case record: ConcessionIncomeRecord => record }
        .filter(_ => request.sessionData.aboutTheTradingHistoryPartOne.exists(_.turnoverSections6045.nonEmpty))
        .fold(Future.successful(Redirect(backLink(idx))))(action)
    } else {
      Redirect(controllers.aboutthetradinghistory.routes.WhenDidYouFirstOccupyController.show())
    }
  private def initialFeeReceived(implicit request: SessionRequest[AnyContent]): FeeReceived =
    FeeReceived(
      request.sessionData.aboutTheTradingHistoryPartOne
        .flatMap(_.turnoverSections6045)
        .getOrElse(Seq.empty)
        .map(section => FeeReceivedPerYear(section.financialYearEnd, section.tradingPeriod))
    )

  private def financialYearEndDates(implicit request: SessionRequest[AnyContent]): Seq[LocalDate] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6045)
      .getOrElse(Seq.empty)
      .map(_.financialYearEnd)

  private def backLink(idx: Int): Call =
    controllers.aboutfranchisesorlettings.routes.ConcessionTypeDetailsController.show(idx)

}
