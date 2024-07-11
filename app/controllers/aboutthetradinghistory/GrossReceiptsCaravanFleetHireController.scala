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
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.GrossReceiptsCaravanFleetHireForm.grossReceiptsCaravanFleetHireForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{GrossReceiptsCaravanFleetHire, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossReceiptsCaravanFleetHireId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.grossReceiptsCaravanFleetHire6045

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * 6045/46 Gross receipts from static caravan fleet hire.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class GrossReceiptsCaravanFleetHireController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  grossReceiptsCaravanFleetHireView: grossReceiptsCaravanFleetHire6045,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        grossReceiptsCaravanFleetHireView(
          grossReceiptsCaravanFleetHireForm(years).fill(
            turnoverSections6045.map(_.grossReceiptsCaravanFleetHire getOrElse GrossReceiptsCaravanFleetHire())
          ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[GrossReceiptsCaravanFleetHire]](
        grossReceiptsCaravanFleetHireForm(years),
        formWithErrors => BadRequest(grossReceiptsCaravanFleetHireView(formWithErrors, getBackLink)),
        success => {
          val updatedSections =
            (success zip turnoverSections6045).map { case (data, previousSection) =>
              previousSection.copy(grossReceiptsCaravanFleetHire = Some(data))
            }

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6045 = Some(updatedSections)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA")
                .getOrElse(navigator.nextPage(GrossReceiptsCaravanFleetHireId, updatedData).apply(updatedData))
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

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case _     => aboutthetradinghistory.routes.CaravansOpenAllYearController.show().url
    }

}
