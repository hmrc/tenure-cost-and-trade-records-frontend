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

package controllers.aboutyouandtheproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.CommercialLettingQuestionForm.commercialLettingQuestionForm
import models.Session
import models.submissions.Form6010.MonthsYearDuration
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, OccupationalAndAccountingInformation}
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CommercialLettingQuestionId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.commercialLettingQuestion

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CommercialLettingQuestionController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: commercialLettingQuestion,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CommercialLettingQuestion")

    Ok(
      view(
        request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.commercialLetDate) match {
          case Some(data) => commercialLettingQuestionForm.fill(data)
          case _          => commercialLettingQuestionForm
        },
        calculateBackLink(request.sessionData)
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[MonthsYearDuration](
      commercialLettingQuestionForm,
      formWithErrors => BadRequest(view(formWithErrors, calculateBackLink(request.sessionData))),
      data => {
        val updatedSessionWithTradingHistory = updateAboutTheTradingHistory { theTradingHistory =>
          theTradingHistory.copy(
            occupationAndAccountingInformation = Option(
              theTradingHistory.occupationAndAccountingInformation.fold(
                OccupationalAndAccountingInformation(firstOccupy = data)
              )(_.copy(firstOccupy = data))
            )
          )
        }

        val updatedSessionWithProperty = updateAboutYouAndThePropertyPartTwo { aboutYou =>
          aboutYou.copy(
            commercialLetDate = Option(data),
            financialEndYearDates = calculateFinancialEndYearDates(data)
          )
        }

        val updatedSession = request.sessionData.copy(
          aboutYouAndThePropertyPartTwo = updatedSessionWithProperty.aboutYouAndThePropertyPartTwo,
          aboutTheTradingHistory = updatedSessionWithTradingHistory.aboutTheTradingHistory
        )

        session
          .saveOrUpdate(updatedSession)
          .map(_ => Redirect(navigator.nextPage(CommercialLettingQuestionId, updatedSession).apply(updatedSession)))
      }
    )
  }

  private def calculateFinancialEndYearDates(commercialLetDate: MonthsYearDuration): Option[Seq[LocalDate]] = {
    val commercialLet = commercialLetDate.toYearMonth.atEndOfMonth()

    val endDates = commercialLet match {
      case d if !d.isBefore(LocalDate.of(2023, 4, 1)) => Seq(LocalDate.of(2024, 3, 31))
      case d if !d.isBefore(LocalDate.of(2022, 4, 1)) => Seq(LocalDate.of(2024, 3, 31), LocalDate.of(2023, 3, 31))
      case _                                          => Seq(LocalDate.of(2024, 3, 31), LocalDate.of(2023, 3, 31), LocalDate.of(2022, 3, 31))
    }

    endDates match {
      case Nil   => None
      case dates => Option(dates)
    }
  }

  private def calculateBackLink(answers: Session)(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case "TL"  => s"${controllers.routes.TaskListController.show().url}#about-the-property"
      case _     =>
        answers.aboutYouAndTheProperty.flatMap(_.altDetailsQuestion.map(_.contactDetailsQuestion)) match {
          case Some(AnswerYes) =>
            controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
          case Some(AnswerNo)  => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
          case _               =>
            logger.warn(s"Back link for alternative contact page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
    }
}
