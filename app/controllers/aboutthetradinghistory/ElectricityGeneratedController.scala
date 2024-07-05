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
import form.aboutthetradinghistory.ElectricityGeneratedForm.electricityGeneratedForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.TurnoverSection6076
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.ElectricityGeneratedId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import util.AccountingInformationUtil.backLinkToFinancialYearEndDates
import views.html.aboutthetradinghistory.electricityGenerated6076

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Electricity generated - 6076.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class ElectricityGeneratedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  electricityGeneratedView: electricityGenerated6076,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val years = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        electricityGeneratedView(
          electricityGeneratedForm(years).fill(
            turnoverSections6076.map(t => (t.tradingPeriod, t.electricityGenerated.getOrElse("")))
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      val years = turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[(Int, String)]](
        electricityGeneratedForm(years),
        formWithErrors => BadRequest(electricityGeneratedView(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6076).map { case (data, previousSection) =>
              previousSection.copy(tradingPeriod = data._1, electricityGenerated = Some(data._2))
            }

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(ElectricityGeneratedId, updatedData).apply(updatedData))
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
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    backLinkToFinancialYearEndDates(navigator)

}
