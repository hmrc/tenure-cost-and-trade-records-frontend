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
import form.aboutthetradinghistory.BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, BunkerFuelCardDetails, BunkerFuelCardsDetails}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.BunkerFuelCardsDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.bunkerFuelCardsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BunkerFuelCardDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: bunkerFuelCardsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val existingBunkerFuelCardDetails: Option[BunkerFuelCardDetails] = for {
      requestedIndex      <- index
      existingBFCDetails  <-
        request.sessionData.aboutTheTradingHistory.map(_.bunkerFuelCardsDetails.getOrElse(IndexedSeq.empty))
      requestedBFCDetails <- existingBFCDetails.lift(requestedIndex)
    } yield requestedBFCDetails.bunkerFuelCardDetails
    Future.successful(
      Ok(
        view(
          existingBunkerFuelCardDetails.fold(bunkerFuelCardDetailsForm)(bunkerFuelCardDetailsForm.fill),
          index,
          getBackLinkUrl(index),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BunkerFuelCardDetails](
      bunkerFuelCardDetailsForm,
      formWithErrors => BadRequest(view(formWithErrors, index, getBackLinkUrl(index), request.sessionData.toSummary)),
      data => {
        val ifBunkerFuelCardsDetailsEmpty = AboutTheTradingHistory(bunkerFuelCardsDetails =
          Some(IndexedSeq(BunkerFuelCardsDetails(bunkerFuelCardDetails = data)))
        )
        val updatedAboutTheTradingHistory =
          request.sessionData.aboutTheTradingHistory.fold(ifBunkerFuelCardsDetailsEmpty) { aboutTheTradingHistory =>
            val existingBunkerFuelCardsDetails                            =
              aboutTheTradingHistory.bunkerFuelCardsDetails.getOrElse(IndexedSeq.empty)
            val requestedDetails                                          = index.flatMap(existingBunkerFuelCardsDetails.lift)
            val updatedDetails: (Int, IndexedSeq[BunkerFuelCardsDetails]) = requestedDetails.fold {
              val defaultDetails  = BunkerFuelCardsDetails(data)
              val appendedDetails = existingBunkerFuelCardsDetails.appended(defaultDetails)
              appendedDetails.indexOf(defaultDetails) -> appendedDetails
            } { detailsToUpdate =>
              val indexToUpdate = existingBunkerFuelCardsDetails.indexOf(detailsToUpdate)
              indexToUpdate -> existingBunkerFuelCardsDetails
                .updated(indexToUpdate, detailsToUpdate.copy(bunkerFuelCardDetails = data))
            }
            aboutTheTradingHistory.copy(bunkerFuelCardsDetails = Some(updatedDetails._2))
          }
        val updatedSessionData            = updateAboutTheTradingHistory(_ => updatedAboutTheTradingHistory)
        session.saveOrUpdate(updatedSessionData).map { _ =>
          Redirect(navigator.nextPage(BunkerFuelCardsDetailsId, updatedSessionData).apply(updatedSessionData))
        }
      }
    )
  }

  private def getBackLinkUrl(maybeIndex: Option[Int]) =
    maybeIndex match {
      case Some(idx) =>
        if (idx > 0) {
          controllers.aboutthetradinghistory.routes.AddAnotherBunkerFuelCardsDetailsController.show(idx - 1).url
        } else {
          controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
        }
      case _         => controllers.aboutthetradinghistory.routes.TotalFuelSoldController.show().url
    }

}
