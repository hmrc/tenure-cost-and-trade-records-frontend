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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.LowMarginFuelCardDetailsForm.lowMarginFuelCardDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, LowMarginFuelCardDetail, LowMarginFuelCardsDetails}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.LowMarginFuelCardsDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.lowMarginFuelCardsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LowMarginFuelCardDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: lowMarginFuelCardsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingLowMarginFuelCardDetails: Option[LowMarginFuelCardDetail] = for {
      requestedIndex      <- index
      existingBFCDetails  <-
        request.sessionData.aboutTheTradingHistory.map(_.lowMarginFuelCardsDetails.getOrElse(IndexedSeq.empty))
      requestedBFCDetails <- existingBFCDetails.lift(requestedIndex)
    } yield requestedBFCDetails.lowMarginFuelCardDetail
    Future.successful(
      Ok(
        view(
          existingLowMarginFuelCardDetails.fold(lowMarginFuelCardDetailsForm)(lowMarginFuelCardDetailsForm.fill),
          index,
          getBackLinkUrl(index),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LowMarginFuelCardDetail](
      lowMarginFuelCardDetailsForm,
      formWithErrors => BadRequest(view(formWithErrors, index, getBackLinkUrl(index), request.sessionData.toSummary)),
      data => {
        val ifLowMarginFuelCardsDetailsEmpty = AboutTheTradingHistory(lowMarginFuelCardsDetails =
          Some(IndexedSeq(LowMarginFuelCardsDetails(lowMarginFuelCardDetail = data)))
        )
        val updatedAboutTheTradingHistory    =
          request.sessionData.aboutTheTradingHistory.fold(ifLowMarginFuelCardsDetailsEmpty) { aboutTheTradingHistory =>
            val existingLowMarginFuelCardsDetails                            =
              aboutTheTradingHistory.lowMarginFuelCardsDetails.getOrElse(IndexedSeq.empty)
            val requestedDetails                                             = index.flatMap(existingLowMarginFuelCardsDetails.lift)
            val updatedDetails: (Int, IndexedSeq[LowMarginFuelCardsDetails]) = requestedDetails.fold {
              val defaultDetails  = LowMarginFuelCardsDetails(data)
              val appendedDetails = existingLowMarginFuelCardsDetails.appended(defaultDetails)
              appendedDetails.indexOf(defaultDetails) -> appendedDetails
            } { detailsToUpdate =>
              val indexToUpdate = existingLowMarginFuelCardsDetails.indexOf(detailsToUpdate)
              indexToUpdate -> existingLowMarginFuelCardsDetails
                .updated(indexToUpdate, detailsToUpdate.copy(lowMarginFuelCardDetail = data))
            }
            aboutTheTradingHistory.copy(lowMarginFuelCardsDetails = Some(updatedDetails._2))
          }
        val updatedSessionData               = updateAboutTheTradingHistory(_ => updatedAboutTheTradingHistory)
        session.saveOrUpdate(updatedSessionData).map { _ =>
          Redirect(navigator.nextPage(LowMarginFuelCardsDetailsId, updatedSessionData).apply(updatedSessionData))
        }
      }
    )
  }

  private def getBackLinkUrl(maybeIndex: Option[Int]) =
    maybeIndex match {
      case Some(idx) =>
        if (idx > 0) {
          controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(idx - 1).url
        } else {
          controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
        }
      case _         => controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
    }

}
