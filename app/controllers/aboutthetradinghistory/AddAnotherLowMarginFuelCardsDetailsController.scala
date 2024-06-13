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
import form.aboutthetradinghistory.AddAnotherLowMarginFuelCardsDetailsForm.addAnotherLowMarginFuelCardsDetailsForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AddAnotherLowMarginFuelCardsDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.addAnotherLowMarginFuelCardDetails
import views.html.genericRemoveConfirmation

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherLowMarginFuelCardsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  addAnotherLowMarginFuelCardsDetailsView: addAnotherLowMarginFuelCardDetails,
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
      .flatMap(_.lowMarginFuelCardsDetails.flatMap(_.lift(idx)))
      .map(_.lowMarginFuelCardDetail.name)

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val addAnother =
      aboutTheTradingHistoryData
        .flatMap(_.lowMarginFuelCardsDetails.flatMap(_.lift(index)))
        .flatMap(_.addAnotherLowMarginFuelCardDetails)
        .orElse(Option.when(navigator.from == "CYA")(AnswerNo))

    Future.successful(
      Ok(
        addAnotherLowMarginFuelCardsDetailsView(
          addAnother.fold(addAnotherLowMarginFuelCardsDetailsForm)(addAnotherLowMarginFuelCardsDetailsForm.fill),
          index,
          calculateBackLink(request, index),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherLowMarginFuelCardsDetailsForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            addAnotherLowMarginFuelCardsDetailsView(
              formWithErrors,
              index,
              calculateBackLink(request, index),
              request.sessionData.toSummary
            )
          )
        ),
      data =>
        aboutTheTradingHistoryData
          .flatMap(_.lowMarginFuelCardsDetails)
          .filter(_.isDefinedAt(index))
          .fold(Future.unit) { existingCards =>
            val updatedCards =
              existingCards.updated(index, existingCards(index).copy(addAnotherLowMarginFuelCardDetails = Some(data)))
            val updatedData  = updateAboutTheTradingHistory(_.copy(lowMarginFuelCardsDetails = Some(updatedCards)))
            session.saveOrUpdate(updatedData)
          }
          .map(_ =>
            if (data == AnswerYes) Redirect(routes.LowMarginFuelCardDetailsController.show())
            else
              Redirect(
                navigator
                  .nextPage(AddAnotherLowMarginFuelCardsDetailsId, request.sessionData)
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
              controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController
                .performRemove(idx),
              routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx)
            )
          )
        )
      }
      .getOrElse(
        Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0))
      )
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
                  controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController
                    .performRemove(idx),
                  routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0))
          ),
      {
        case AnswerYes =>
          aboutTheTradingHistoryData.flatMap(_.lowMarginFuelCardsDetails).map { businessSections =>
            val updatedSections = businessSections.patch(idx, Nil, 1)
            session.saveOrUpdate(
              updateAboutTheTradingHistory(_.copy(lowMarginFuelCardsDetails = Some(updatedSections)))
            )
          }
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent], index: Int) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#"
      case _     => controllers.aboutthetradinghistory.routes.LowMarginFuelCardDetailsController.show(Some(index)).url
    }

}
