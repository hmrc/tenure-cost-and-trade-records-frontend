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
import form.aboutthetradinghistory.AddAnotherLowMarginFuelCardsDetailsForm.theForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AddAnotherLowMarginFuelCardsDetailsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.addAnotherLowMarginFuelCardDetails as AddAnotherLowMarginFuelCardDetailsView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddAnotherLowMarginFuelCardsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  theListView: AddAnotherLowMarginFuelCardDetailsView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
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
    audit.sendChangeLink("AddAnotherLowMarginFuelCardsDetails")

    val addAnother =
      aboutTheTradingHistoryData
        .flatMap(_.lowMarginFuelCardsDetails.flatMap(_.lift(index)))
        .flatMap(_.addAnotherLowMarginFuelCardDetails)
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
          .flatMap(_.lowMarginFuelCardsDetails)
          .filter(_.isDefinedAt(index))
          .fold(Future.unit) { existingCards =>
            val updatedCards =
              existingCards
                .updated(index, existingCards(index).copy(addAnotherLowMarginFuelCardDetails = Some(formData)))
            val updatedData  = updateAboutTheTradingHistory(_.copy(lowMarginFuelCardsDetails = Some(updatedCards)))
            repository.saveOrUpdate(updatedData)
          }
          .map(_ =>
            if (formData == AnswerYes) Redirect(routes.LowMarginFuelCardDetailsController.show())
            else
              Redirect(
                navigator
                  .nextPage(AddAnotherLowMarginFuelCardsDetailsId, request.sessionData)
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
            theConfirmationView(
              confirmableActionForm,
              cardName,
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

  def performRemove(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        getCardName(idx)
          .map { cardName =>
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  cardName,
                  controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController
                    .performRemove(idx),
                  routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            navigator.redirectBackToCYAor(
              controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0)
            )
          ),
      {
        case AnswerYes =>
          aboutTheTradingHistoryData.flatMap(_.lowMarginFuelCardsDetails).map { businessSections =>
            val updatedSections = businessSections.patch(idx, Nil, 1)
            repository.saveOrUpdate(
              updateAboutTheTradingHistory(_.copy(lowMarginFuelCardsDetails = Some(updatedSections)))
            )
          }
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0))
        case AnswerNo  =>
          Redirect(controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx))
      }
    )
  }

}
