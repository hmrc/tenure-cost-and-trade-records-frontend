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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import models.submissions.aboutthetradinghistory.TurnoverSection
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TurnoverPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.turnover

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}

@Singleton
class TurnoverController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  turnoverView: turnover,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory.fold(Redirect(routes.AboutYourTradingHistoryController.show())) {
      aboutTheTradingHistory =>
        if (aboutTheTradingHistory.turnoverSection.isEmpty) {
          (for {
            financialYearEnd    <- aboutTheTradingHistory.aboutYourTradingHistory.map(_.financialYear)
            now                  = LocalDate.now()
            currentFinancialYear =
              if (now.isBefore(LocalDate.of(now.getYear, financialYearEnd.months, financialYearEnd.days))) {
                now.getYear
              } else now.getYear + 1
            firstOccupy         <- aboutTheTradingHistory.aboutYourTradingHistory.map(_.firstOccupy)
            yearDifference       = currentFinancialYear - firstOccupy.years
            numberOfSections     = 1 to (if (yearDifference > 3) 3 else yearDifference)
          } yield Ok(
            turnoverView(
              numberOfSections.map { yearsAgo =>
                TurnoverSection(
                  financialYearEnd =
                    LocalDate.of(currentFinancialYear - yearsAgo, financialYearEnd.months, financialYearEnd.days),
                  tradingPeriod = 52
                )
              }
            )
          )).getOrElse(Redirect(routes.AboutYourTradingHistoryController.show()))
        } else Ok(turnoverView(aboutTheTradingHistory.turnoverSection))
    }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft {
      val updatedData = request.sessionData
      Redirect(navigator.nextPage(TurnoverPageId).apply(updatedData))
    }
  }

}
