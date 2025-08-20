/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutthetradinghistory.AddAnotherBunkerFuelCardsDetailsForm.theForm
import models.pages.ListPageConfig.*
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AddAnotherBunkerFuelCardsDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.addAnotherBunkerFuelCardDetails as AddAnotherBunkerFuelCardDetailsView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherBunkerFuelCardsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  theListView: AddAnotherBunkerFuelCardDetailsView,
  theRemoveView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
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
        theListView(
          addAnother.fold(theForm)(theForm.fill),
          index
        )
      )
    )
  }

  def submit(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val fromCYA =
      aboutTheTradingHistoryDataPartOne.flatMap(_.fromCYA).getOrElse(false) || navigator.from == "CYA"

    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        Future.successful(
          BadRequest(
            theListView(
              formWithErrors,
              index
            )
          )
        ),
      formData =>
        aboutTheTradingHistoryData
          .flatMap(_.bunkerFuelCardsDetails)
          .filter(_.isDefinedAt(index))
          .fold(Future.unit) { existingCards =>
            val updatedCards   =
              existingCards.updated(index, existingCards(index).copy(addAnotherBunkerFuelCardDetails = Some(formData)))
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
            repository.saveOrUpdate(updatedData)
            repository.saveOrUpdate(updatedDataCYA)
          }
          .map(_ =>
            if (formData == AnswerNo && fromCYA) {
              Redirect(
                routes.CheckYourAnswersAboutTheTradingHistoryController.show()
              )
            } else if (formData == AnswerYes) {
              Redirect(
                if aboutTheTradingHistoryData
                    .flatMap(_.bunkerFuelCardsDetails)
                    .exists(_.size >= BunkerFuelCards.maxListItems)
                then controllers.routes.AddedMaximumListItemsController.show(BunkerFuelCards)
                else routes.BunkerFuelCardDetailsController.show()
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

  def remove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    getCardName(idx)
      .map { cardName =>
        Future.successful(
          Ok(
            theRemoveView(
              confirmableActionForm,
              cardName,
              controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.performRemove(idx),
              routes.AddAnotherBunkerFuelCardsDetailsController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)))
  }

  def performRemove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        getCardName(idx)
          .map { cardName =>
            Future.successful(
              BadRequest(
                theRemoveView(
                  formWithErrors,
                  cardName,
                  controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController
                    .performRemove(idx),
                  routes.AddAnotherBunkerFuelCardsDetailsController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            navigator.redirectBackToCYAor(
              controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(0)
            )
          ),
      {
        case AnswerYes =>
          aboutTheTradingHistoryData.flatMap(_.bunkerFuelCardsDetails).map { businessSections =>
            val updatedSections = businessSections.patch(idx, Nil, 1)
            repository.saveOrUpdate(
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
