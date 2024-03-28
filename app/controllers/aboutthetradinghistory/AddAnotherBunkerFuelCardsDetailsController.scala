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
import controllers.FORDataCaptureController
import form.confirmableActionForm.confirmableActionForm
import form.aboutthetradinghistory.AddAnotherBunkerFuelCardsDetailsForm.addAnotherBunkerFuelCardsDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, BunkerFuelCardsDetails}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.addAnotherBunkerFuelCardDetails
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherBunkerFuelCardsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
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

  private def getCardName(idx: Int)(implicit request: SessionRequest[AnyContent]): Option[String] =
    aboutTheTradingHistoryData
      .flatMap(_.bunkerFuelCardsDetails.flatMap(_.lift(idx)))
      .map(_.bunkerFuelCardDetails.name)

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
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
          controllers.aboutthetradinghistory.routes.BunkerFuelCardDetailsController.show(Some(index)).url,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherBunkerFuelCardsDetailsForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            addAnotherBunkerFuelCardsDetailsView(
              formWithErrors,
              index,
              routes.BunkerFuelCardDetailsController.show(Some(index)).url,
              request.sessionData.toSummary
            )
          )
        ),
      data => {
        def updateAndRedirect(
          updatedCards: IndexedSeq[BunkerFuelCardsDetails],
          index: Int
        )(implicit request: SessionRequest[AnyContent]): Future[Result] = {
          val updatedTradingHistory = request.sessionData.aboutTheTradingHistory
            .getOrElse(AboutTheTradingHistory())
            .copy(bunkerFuelCardsDetails = Some(updatedCards))

          val updatedSessionData = request.sessionData.copy(aboutTheTradingHistory = Some(updatedTradingHistory))
          session.saveOrUpdate(updatedSessionData).map { _ =>
            if (updatedCards.lastOption.flatMap(_.addAnotherBunkerFuelCardDetails).contains(AnswerYes)) {
              Redirect(routes.BunkerFuelCardDetailsController.show())
            } else {
              Redirect(routes.CustomerCreditAccountsController.show())
            }
          }
        }

        val existingCards =
          request.sessionData.aboutTheTradingHistory.flatMap(_.bunkerFuelCardsDetails).getOrElse(IndexedSeq.empty)

        if (data == AnswerYes) {
          if (existingCards.isDefinedAt(index)) {
            val updatedCards =
              existingCards.updated(index, existingCards(index).copy(addAnotherBunkerFuelCardDetails = Some(data)))
            updateAndRedirect(updatedCards, index)
          } else {
            Redirect(routes.BunkerFuelCardDetailsController.show())
          }
        } else {
          val updatedCards =
            existingCards.updated(index, existingCards(index).copy(addAnotherBunkerFuelCardDetails = Some(data)))
          updateAndRedirect(updatedCards, index)
        }
      }
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

}
