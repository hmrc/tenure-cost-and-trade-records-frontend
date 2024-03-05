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
import controllers.FORDataCaptureController
import controllers.aboutthetradinghistory.routes
import form.aboutfranchisesorlettings.FeeReceivedForm.feeReceivedForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{CateringOperationBusinessSection, FeeReceived, FeeReceivedPerYear}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FeeReceivedPageId
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.feeReceived

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeeReceivedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  feeReceivedView: feeReceived,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck(idx) { currentSection =>
      val years = financialYearEndDates.map(_.getYear.toString)

      Ok(
        feeReceivedView(
          currentSection.feeReceived
            .filter(_.feeReceivedPerYear.size == years.size)
            .fold(feeReceivedForm(years).fill(initialFeeReceived))(feeReceivedForm(years).fill),
          idx,
          currentSection.cateringOperationBusinessDetails.operatorName,
          backLink(idx).url
        )
      )
    }
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck(idx) { currentSection =>
      val yearEndDates = financialYearEndDates
      val years        = yearEndDates.map(_.getYear.toString)

      continueOrSaveAsDraft[FeeReceived](
        feeReceivedForm(years),
        formWithErrors =>
          BadRequest(
            feeReceivedView(
              formWithErrors,
              idx,
              currentSection.cateringOperationBusinessDetails.operatorName,
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
              .map(_.cateringOperationBusinessSections.getOrElse(IndexedSeq.empty))
              .map(_.updated(idx, currentSection.copy(feeReceived = Some(updatedFeeReceived))))
              .get

            val updatedData =
              updateAboutFranchisesOrLettings(_.copy(cateringOperationBusinessSections = Some(updatedSections)))
            session.saveOrUpdate(updatedData).map { _ =>
              Redirect(navigator.nextPage(FeeReceivedPageId, updatedData).apply(updatedData))
            }
          } else {
            Redirect(controllers.aboutfranchisesorlettings.routes.FeeReceivedController.show(idx))
          }
      )
    }
  }

  private def runWithSessionCheck(idx: Int)(
    action: CateringOperationBusinessSection => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    if (request.sessionData.aboutTheTradingHistory.map(_.turnoverSections6030).exists(_.nonEmpty)) {
      request.sessionData.aboutFranchisesOrLettings
        .filter(_.cateringOperationBusinessSections.nonEmpty)
        .flatMap(_.cateringOperationBusinessSections.getOrElse(IndexedSeq.empty).lift(idx))
        .fold(Future.successful(Redirect(backLink(idx))))(action)
    } else {
      Redirect(routes.AboutYourTradingHistoryController.show())
    }

  private def initialFeeReceived(implicit request: SessionRequest[AnyContent]): FeeReceived =
    FeeReceived(
      request.sessionData.aboutTheTradingHistory
        .fold(Seq.empty[FeeReceivedPerYear])(
          _.turnoverSections6030.map(section => FeeReceivedPerYear(section.financialYearEnd, section.tradingPeriod))
        )
    )

  private def financialYearEndDates(implicit request: SessionRequest[AnyContent]): Seq[LocalDate] =
    request.sessionData.aboutTheTradingHistory.fold(Seq.empty[LocalDate])(
      _.turnoverSections6030.map(_.financialYearEnd)
    )

  private def backLink(idx: Int): Call =
    controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show(Some(idx))

}
