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
import form.confirmableActionForm.confirmableActionForm
import form.aboutthetradinghistory.AddAnotherBunkerFuelCardsDetailsForm.addAnotherBunkerFuelCardsDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AddAnotherBunkerFuelCardsDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.addAnotherBunkerFuelCardDetails
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherBunkerFuelCardsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  addAnotherBunkerFuelCardsDetailsView: addAnotherBunkerFuelCardDetails,
  genericRemoveConfirmationView: genericRemoveConfirmation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def aboutTheTradingHistoryData(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutTheTradingHistory] =
    request.sessionData.aboutTheTradingHistory

  private def aboutTheTradingHistoryDataPartOne(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutTheTradingHistoryPartOne] =
    request.sessionData.aboutTheTradingHistoryPartOne

  private def getCardName(idx: Int)(implicit request: SessionRequest[AnyContent]): Option[String] =
    aboutTheTradingHistoryData
      .flatMap(_.bunkerFuelCardsDetails.flatMap(_.lift(idx)))
      .map(_.bunkerFuelCardDetails.name)

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AddAnotherBunkerFuelCardsDetails")

    val addAnother =
      aboutTheTradingHistoryData
        .flatMap(_.bunkerFuelCardsDetails.flatMap(_.lift(index)))
        .flatMap(_.addAnotherBunkerFuelCardDetails)
        .orElse(Option.when(navigator.from == "CYA")(AnswerNo))

    Future.successful(
      Ok(
        addAnotherBunkerFuelCardsDetailsView(
          addAnother.fold(addAnotherBunkerFuelCardsDetailsForm)(addAnotherBunkerFuelCardsDetailsForm.fill),
          index,
          calculateBackLink(request, index),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val fromCYA =
      aboutTheTradingHistoryDataPartOne.flatMap(_.fromCYA).getOrElse(false) || navigator.from == "CYA"

    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherBunkerFuelCardsDetailsForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            addAnotherBunkerFuelCardsDetailsView(
              formWithErrors,
              index,
              calculateBackLink(request, index),
              request.sessionData.toSummary
            )
          )
        ),
      data =>
        aboutTheTradingHistoryData
          .flatMap(_.bunkerFuelCardsDetails)
          .filter(_.isDefinedAt(index))
          .fold(Future.unit) { existingCards =>
            val updatedCards   =
              existingCards.updated(index, existingCards(index).copy(addAnotherBunkerFuelCardDetails = Some(data)))
            val updatedData    = updateAboutTheTradingHistory(
              _.copy(
                bunkerFuelCardsDetails = Some(updatedCards)
              )
            )
            val updatedDataCYA = updateAboutTheTradingHistoryPartOne(
              _.copy(
                fromCYA = Some(fromCYA)
              )
            )
            session.saveOrUpdate(updatedData)
            session.saveOrUpdate(updatedDataCYA)
          }
          .map(_ =>
            if (data == AnswerNo && fromCYA) {
              Redirect(
                routes.CheckYourAnswersAboutTheTradingHistoryController.show()
              )
            } else if (data == AnswerYes) {
              Redirect(
                routes.BunkerFuelCardDetailsController.show()
              )
            } else
              Redirect(
                navigator
                  .nextPage(AddAnotherBunkerFuelCardsDetailsId, request.sessionData)
                  .apply(request.sessionData)
              )
          )
    )
  }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    getCardName(idx)
      .map { cardName =>
        Future.successful(
          Ok(
            genericRemoveConfirmationView(
              confirmableActionForm,
              cardName,
              "label.section.aboutYourTradingHistory",
              request.sessionData.toSummary,
              idx,
              controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.performRemove(idx),
              routes.AddAnotherBunkerFuelCardsDetailsController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)))
  }

  def performRemove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        getCardName(idx)
          .map { cardName =>
            Future.successful(
              BadRequest(
                genericRemoveConfirmationView(
                  formWithErrors,
                  cardName,
                  "label.section.aboutYourTradingHistory",
                  request.sessionData.toSummary,
                  idx,
                  controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController
                    .performRemove(idx),
                  routes.AddAnotherBunkerFuelCardsDetailsController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0))
          ),
      {
        case AnswerYes =>
          aboutTheTradingHistoryData.flatMap(_.bunkerFuelCardsDetails).map { businessSections =>
            val updatedSections = businessSections.patch(idx, Nil, 1)
            session.saveOrUpdate(
              updateAboutTheTradingHistory(_.copy(bunkerFuelCardsDetails = Some(updatedSections)))
            )
          }
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(idx))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent], index: Int) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case _     => controllers.aboutthetradinghistory.routes.BunkerFuelCardDetailsController.show(Some(index)).url
    }

}
