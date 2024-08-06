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
import form.aboutthetradinghistory.CaravansTradingForm
import form.aboutthetradinghistory.CaravansTradingForm.caravansTradingForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.Caravans.CaravanLettingType.OwnedByOperator
import models.submissions.aboutthetradinghistory.Caravans.CaravansTradingPage.*
import models.submissions.aboutthetradinghistory.Caravans.{CaravanLettingType, CaravanUnitType, CaravansTradingPage}
import models.submissions.aboutthetradinghistory.{Caravans, CaravansTrading6045, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.Identifier
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.*
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansTrading6045

import scala.concurrent.{ExecutionContext, Future}

/**
  * 6045/46 Caravans trading history - base controller.
  *
  * @author Yuriy Tumakha
  */
abstract class CaravansTrading6045Controller(
  tradingPage: CaravansTradingPage,
  mcc: MessagesControllerComponents
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  val pageId: Identifier              = tradingPage.pageId
  val unitType: CaravanUnitType       = tradingPage.unitType
  val lettingType: CaravanLettingType = tradingPage.lettingType

  val caravansTrading6045View: caravansTrading6045
  val navigator: AboutTheTradingHistoryNavigator
  val withSessionRefiner: WithSessionRefiner
  val session: SessionRepo

  implicit val ec: ExecutionContext = mcc.executionContext

  def getSavedAnswer: TurnoverSection6045 => Option[CaravansTrading6045]

  def updateAnswer(
    caravansTrading6045: CaravansTrading6045
  ): TurnoverSection6045 => TurnoverSection6045

  private val messageKeyPrefix = s"turnover.6045.caravans.$unitType.$lettingType"
  private val showHelpText     = Option.when(lettingType == OwnedByOperator)(unitType)

  private def form(years: Seq[String])(implicit messages: Messages) = caravansTradingForm(years, unitType, lettingType)

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        caravansTrading6045View(
          form(years).fill(getSavedAnswers(turnoverSections6045)),
          formAction,
          messageKeyPrefix,
          showHelpText,
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[CaravansTrading6045]](
        form(years),
        formWithErrors =>
          BadRequest(caravansTrading6045View(formWithErrors, formAction, messageKeyPrefix, showHelpText, getBackLink)),
        success => {
          val updatedSections = (success zip turnoverSections6045).map(updateAnswer(_).apply(_))

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(turnoverSections6045 = Some(updatedSections))
          )

          session
            .saveOrUpdate(updatedData)
            .map(_ => navigator.nextPage(pageId, updatedData).apply(updatedData))
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

  private def getSavedAnswers(turnoverSections6045: Seq[TurnoverSection6045]): Seq[CaravansTrading6045] =
    turnoverSections6045.map(getSavedAnswer(_).getOrElse(CaravansTrading6045()))

  private def formAction: Call =
    tradingPage match {
      case SingleCaravansOwnedByOperator => routes.SingleCaravansOwnedByOperatorController.submit()
      case SingleCaravansSublet          => routes.SingleCaravansSubletController.submit()
      case TwinCaravansOwnedByOperator   => routes.TwinUnitCaravansOwnedByOperatorController.submit()
      case TwinCaravansSublet            => routes.TwinUnitCaravansSubletController.submit()
    }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    (navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
      case _     =>
        tradingPage match {
          case SingleCaravansOwnedByOperator => routes.GrossReceiptsCaravanFleetHireController.show()
          case SingleCaravansSublet          => routes.SingleCaravansOwnedByOperatorController.show()
          case TwinCaravansOwnedByOperator   => routes.SingleCaravansAgeCategoriesController.show()
          case TwinCaravansSublet            => routes.TwinUnitCaravansOwnedByOperatorController.show()
        }
    }).url

}
