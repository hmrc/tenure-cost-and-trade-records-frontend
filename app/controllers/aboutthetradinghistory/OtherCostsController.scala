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
import form.aboutthetradinghistory.OtherCostsForm.form
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, OtherCost, OtherCosts}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OtherCostsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.otherCosts

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OtherCostsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  otherCostsView: otherCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("OtherCosts")

    runWithSessionCheckForOtherCosts { aboutTheTradingHistory =>
      val yearEndDates       = financialYearEndDates(aboutTheTradingHistory)
      val existingOtherCosts = aboutTheTradingHistory.otherCosts.getOrElse(OtherCosts())
      val updatedOtherCosts  = if (existingOtherCosts.otherCosts.size != yearEndDates.size) {
        val newOtherCosts = yearEndDates.map(date => OtherCost(date, None, None))
        OtherCosts(newOtherCosts)
      } else {
        existingOtherCosts
      }

      val updatedData = updateAboutTheTradingHistory(_.copy(otherCosts = Some(updatedOtherCosts)))
      val filledForm  = form.fill(updatedOtherCosts)
      session
        .saveOrUpdate(updatedData)
        .flatMap(_ => Ok(otherCostsView(filledForm, navigator.from)))
    }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheckForOtherCosts { aboutTheTradingHistory =>
      continueOrSaveAsDraft[OtherCosts](
        form,
        formWithErrors => {
          val updatedOtherCosts = aboutTheTradingHistory.otherCosts.getOrElse(OtherCosts(Seq.empty)).otherCosts

          val updatedErrors         = formWithErrors.errors.map { error =>
            if (error.key.startsWith("otherCosts[")) {
              val index            = error.key.split("\\[")(1).split("\\]")(0).toInt
              val financialYearEnd = updatedOtherCosts.lift(index).map(_.financialYearEnd).getOrElse(LocalDate.now)
              error.copy(args = Seq(financialYearEnd))
            } else if (error.key.isEmpty && error.message == "error.otherCostDetails.required") {
              error.copy(key = "otherCostDetails")
            } else {
              error
            }
          }
          val updatedFormWithErrors = formWithErrors.copy(errors = updatedErrors)

          BadRequest(otherCostsView(updatedFormWithErrors, navigator.from))
        },
        data => {
          val updatedOtherCostWithDate = (data.otherCosts zip financialYearEndDates(aboutTheTradingHistory)).map {
            case (otherCost, finYearEnd) => otherCost.copy(financialYearEnd = finYearEnd)
          }

          val updatedData =
            updateAboutTheTradingHistory(_.copy(otherCosts = Some(data.copy(otherCosts = updatedOtherCostWithDate))))
          session
            .saveOrUpdate(updatedData)
            .map(_ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && aboutTheTradingHistory.totalPayrollCostSections.nonEmpty)
                .getOrElse(navigator.nextPage(OtherCostsId, updatedData).apply(updatedData))
            )
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheckForOtherCosts(
    action: AboutTheTradingHistory => Future[Result]
  )(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory) =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)

}
