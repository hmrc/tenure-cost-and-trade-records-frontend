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
import connectors.Audit
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.OccupationalInformationForm.occupationalInformationForm
import models.submissions.Form6010.MonthsYearDuration
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.OccupationalAndAccountingInformation
import models.ForType.*
import models.Session
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AboutYourTradingHistoryPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil.*
import views.html.aboutthetradinghistory.whenDidYouFirstOccupy as WhenDidYouFirstOccupyView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WhenDidYouFirstOccupyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  theView: WhenDidYouFirstOccupyView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AboutYourTradingHistory")
    Ok(
      theView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation) match {
          case Some(occupationAccounting) =>
            occupationalInformationForm.fill(occupationAccounting.firstOccupy)
          case _                          => occupationalInformationForm
        },
        getBackLink(request.sessionData)
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[MonthsYearDuration](
      occupationalInformationForm,
      formWithErrors => BadRequest(theView(formWithErrors, getBackLink(request.sessionData))),
      formData => {
        val occupationAndAccountingInfo = request.sessionData.aboutTheTradingHistory
          .flatMap(_.occupationAndAccountingInformation)
          .fold(OccupationalAndAccountingInformation(formData))(_.copy(firstOccupy = formData))

        val updatedData = updateAboutTheTradingHistory(
          _.copy(
            occupationAndAccountingInformation = Some(occupationAndAccountingInfo)
          )
        )
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator.cyaPage
              .filter(_ =>
                navigator.from == "CYA" && occupationAndAccountingInfo.currentFinancialYearEnd.isDefined
                  && (
                    financialYearsList(occupationAndAccountingInfo) == previousFinancialYears ||
                      financialYearsList(occupationAndAccountingInfo) == previousFinancialYears6076 ||
                      financialYearsList(occupationAndAccountingInfo) == previousFinancialYears6045 ||
                      financialYearsList(occupationAndAccountingInfo) == previousFinancialYears6048
                  )
              )
              .getOrElse(navigator.nextPage(AboutYourTradingHistoryPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case FOR6010 | FOR6011 | FOR6015 | FOR6016 | FOR6020 | FOR6030 | FOR6045 | FOR6046 | FOR6076 =>
        controllers.aboutthetradinghistory.routes.WhatYouWillNeedController.show().url
      case FOR6048                                                                                 => controllers.aboutthetradinghistory.routes.AreYouVATRegisteredController.show.url
    }

}
