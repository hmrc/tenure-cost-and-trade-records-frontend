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
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType.Single
import models.submissions.aboutthetradinghistory.Caravans.{CaravanLettingType, CaravanUnitType}
import models.submissions.aboutthetradinghistory.{Caravans, CaravansTrading6045, TurnoverSection6045}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.SingleCaravansOwnedByOperatorId
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansTrading6045

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * 6045/46 Trading history - single caravans owned by operator.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class SingleCaravansOwnedByOperatorController @Inject() (
  caravansTrading6045View: caravansTrading6045,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private val pageId                          = SingleCaravansOwnedByOperatorId
  private val unitType: CaravanUnitType       = Single
  private val lettingType: CaravanLettingType = OwnedByOperator

  private val messageKeyPrefix = s"turnover.6045.caravans.$unitType.$lettingType"
  private val showHelpText     = Option.when(lettingType == OwnedByOperator)(unitType)

  private def form(years: Seq[String])(implicit messages: Messages) = caravansTradingForm(years, unitType, lettingType)

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6045 =>
      val years = turnoverSections6045.map(_.financialYearEnd).map(_.getYear.toString)

      Ok(
        caravansTrading6045View(
          form(years).fill(getSavedAnswers(turnoverSections6045)),
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
          BadRequest(caravansTrading6045View(formWithErrors, messageKeyPrefix, showHelpText, getBackLink)),
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
    turnoverSections6045.map(_.singleCaravansOwnedByOperator.getOrElse(CaravansTrading6045()))

  private def updateAnswer(
    singleCaravansOwnedByOperator: CaravansTrading6045
  ): TurnoverSection6045 => TurnoverSection6045 =
    _.copy(singleCaravansOwnedByOperator = Some(singleCaravansOwnedByOperator))

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case _     => aboutthetradinghistory.routes.GrossReceiptsCaravanFleetHireController.show().url
    }

}
