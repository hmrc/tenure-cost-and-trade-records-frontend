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
import form.aboutthetradinghistory.CaravansOpenAllYearForm.caravansOpenAllYearForm
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, Caravans, TurnoverSection6045}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.common.{AnswerNo, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CaravansOpenAllYearId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansOpenAllYear

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * 6045/6046 Trading history - Are your static caravans open all year.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class CaravansOpenAllYearController @Inject() (
  caravansOpenAllYearView: caravansOpenAllYear,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CaravansOpenAllYear")
    runWithSessionCheck { _ =>
      Ok(
        caravansOpenAllYearView(
          savedAnswer.fold(caravansOpenAllYearForm)(caravansOpenAllYearForm.fill),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      continueOrSaveAsDraft[(AnswersYesNo, Option[Int])](
        caravansOpenAllYearForm,
        formWithErrors => BadRequest(caravansOpenAllYearView(formWithErrors, getBackLink)),
        data => {
          val tradingPeriod   = data._2.getOrElse(52)
          val updatedSections =
            if data._1 == AnswerNo then turnoverSections6045.map(_.copy(tradingPeriod = tradingPeriod))
            else turnoverSections6045

          val updatedData = updateAboutTheTradingHistoryPartOne(tradingHistoryPartOne =>
            val caravans = tradingHistoryPartOne.caravans getOrElse Caravans()
            tradingHistoryPartOne.copy(
              turnoverSections6045 = Some(updatedSections),
              caravans = Some(
                caravans.copy(
                  openAllYear = Some(data._1),
                  weeksPerYear = data._2
                )
              )
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(CaravansOpenAllYearId, updatedData).apply(updatedData))
            }
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6045] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6045)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def savedAnswer(implicit
    request: SessionRequest[AnyContent]
  ): Option[(AnswersYesNo, Option[Int])] = request.sessionData.aboutTheTradingHistoryPartOne
    .flatMap(_.caravans)
    .flatMap(caravans => caravans.openAllYear.map((_, caravans.weeksPerYear)))

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.cyaPage
      .filter(_ => navigator.from == "CYA")
      .getOrElse(routes.StaticCaravansController.show())
      .url

}
