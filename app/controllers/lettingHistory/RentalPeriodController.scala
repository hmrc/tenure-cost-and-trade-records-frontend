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

package controllers.lettingHistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.lettingHistory.RentalPeriodForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LocalPeriod, OccupierDetail}
import navigation.LettingHistoryNavigator
import navigation.identifiers.RentalPeriodPageId
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.twirl.api.Html
import repositories.SessionRepo
import util.DateUtilLocalised
import views.html.lettingHistory.rentalPeriod as RentalPeriodView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RentalPeriodController @Inject (
  mcc: MessagesControllerComponents,
  dateUtil: DateUtilLocalised,
  navigator: LettingHistoryNavigator,
  theView: RentalPeriodView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  given DateUtilLocalised = dateUtil
  type PartiallyAppliedView = Form[LocalPeriod] => Html

  def show(maybeIndex: Option[Int]): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        index            <- maybeIndex
        completedLetting <- completedLettings(request.sessionData).lift(index)
        rentalPeriod     <- completedLetting.rentalPeriod
      yield theForm.fill(rentalPeriod)

    withOccupierAt(maybeIndex) { (thePartiallyAppliedView, _, _) =>
      successful(Ok(thePartiallyAppliedView.apply(filledForm.getOrElse(freshForm))))
    }
  }

  def submit(maybeIndex: Option[Int]): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withOccupierAt(maybeIndex) { (thePartiallyAppliedView, occupier, index) =>
      continueOrSaveAsDraft[LocalPeriod](
        theForm,
        theFormWithErrors => badRequestWith(thePartiallyAppliedView, theFormWithErrors),
        rental =>
          given Session = request.sessionData
          if hasBeenAlreadyEntered(occupier.copy(rentalPeriod = Some(rental)), at = Some(index))
          then
            badRequestWith(
              thePartiallyAppliedView,
              theForm
                .fill(rental)
                .withError("duplicate", "lettingHistory.occupierDetail.duplicate")
            )
          else
            for
              newSession   <- successful(byUpdatingOccupierRentalPeriod(index, rental))
              savedSession <- repository.saveOrUpdateSession(newSession)
            yield navigator.redirect(currentPage = RentalPeriodPageId, savedSession)
      )
    }
  }

  private def withOccupierAt(
    maybeIndex: Option[Int]
  )(
    generateResult: (PartiallyAppliedView, OccupierDetail, Int) => Future[Result]
  )(using request: SessionRequest[AnyContent]): Future[Result] = {

    val result =
      for
        index          <- maybeIndex
        occupierDetail <- completedLettings(request.sessionData).lift(index)
      yield {
        val thePartiallyAppliedView = theView.apply(_, occupierDetail.name, index, backLinkUrl(index))
        generateResult.apply(thePartiallyAppliedView, occupierDetail, index)
      }

    result.getOrElse(successful(Redirect(routes.OccupierListController.show)))
  }

  private def backLinkUrl(index: Int)(using request: SessionRequest[AnyContent]): Option[String] =
    val navigationData = Map("index" -> index)
    navigator.backLinkUrl(ofPage = RentalPeriodPageId, navigationData)

  private def badRequestWith(thePartiallyAppliedView: PartiallyAppliedView, theFormWithErrors: Form[LocalPeriod]) =
    successful(
      BadRequest(
        thePartiallyAppliedView.apply(
          theFormWithErrors
        )
      )
    )
