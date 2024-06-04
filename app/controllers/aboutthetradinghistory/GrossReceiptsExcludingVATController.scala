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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.GrossReceiptsExcludingVATForm.grossReceiptsExcludingVATForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne, GrossReceiptsExcludingVAT}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossReceiptsExcludingVatId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.grossReceiptsExcludingVAT

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GrossReceiptsExcludingVATController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: grossReceiptsExcludingVAT,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val grossReceiptsExcludingVAT =
          request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.grossReceiptsExcludingVAT).getOrElse(Seq.empty)
        Ok(
          view(
            grossReceiptsExcludingVATForm(
              years(request.sessionData.aboutTheTradingHistoryPartOne.getOrElse(new AboutTheTradingHistoryPartOne()))
            )
              .fill(grossReceiptsExcludingVAT),
            navigator.from
          )
        )
      }

  }

  def submit                                                                                                      = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        val aboutTheTradingHistoryPartOne =
          request.sessionData.aboutTheTradingHistoryPartOne.getOrElse(new AboutTheTradingHistoryPartOne())
        continueOrSaveAsDraft[Seq[GrossReceiptsExcludingVAT]](
          grossReceiptsExcludingVATForm(years(aboutTheTradingHistoryPartOne)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                navigator.from
              )
            ),
          success => {
            val grossReceipts =
              (success zip financialYearEndDates(aboutTheTradingHistoryPartOne)).map {
                case (grossReceipt, finYearEnd) =>
                  grossReceipt.copy(financialYearEnd = finYearEnd)
              }

            val updatedData =
              updateAboutTheTradingHistoryPartOne(_.copy(grossReceiptsExcludingVAT = Some(grossReceipts)))
            session
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(GrossReceiptsExcludingVatId, updatedData).apply(updatedData)))
          }
        )
      }
  }
  private def financialYearEndDates(aboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne): Seq[LocalDate] =
    aboutTheTradingHistoryPartOne.turnoverSections6076.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def years(aboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne): Seq[String] =
    financialYearEndDates(aboutTheTradingHistoryPartOne).map(_.getYear.toString)
}
