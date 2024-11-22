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

package controllers.lettingHistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.lettingHistory.RentPeriodForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.byAddingOccupierRentalPeriod
import models.submissions.lettingHistory.{LettingHistory, LocalPeriod}
import navigation.LettingHistoryNavigator
import navigation.identifiers.RentalPeriodPageId
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.twirl.api.Html
import repositories.SessionRepo
import util.DateUtilLocalised
import views.html.lettingHistory.rentalPeriod as RentalPeriodView

import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

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
        completedLetting <- LettingHistory.completedLettings(request.sessionData).lift(index)
        rentalPeriod     <- completedLetting.rental
      yield theForm.fill(rentalPeriod)

    withOccupierAt(maybeIndex) { (partiallyAppliedView, _) =>
      successful(Ok(partiallyAppliedView.apply(filledForm.getOrElse(freshForm))))
    }
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withOccupierAt(index) { (partiallyAppliedView, index) =>
      continueOrSaveAsDraft[LocalPeriod](
        theForm,
        theFormWithErrors => successful(BadRequest(partiallyAppliedView.apply(theFormWithErrors))),
        rental =>
          given Session      = request.sessionData
          val updatedSession = byAddingOccupierRentalPeriod(index, rental)
          for savedSession <- repository.saveOrUpdateSession(updatedSession)
          yield navigator.redirect(currentPage = RentalPeriodPageId, savedSession)
      )
    }
  }

  private def withOccupierAt(
    maybeIndex: Option[Int]
  )(
    generateResult: (PartiallyAppliedView, Int) => Future[Result]
  )(using request: SessionRequest[AnyContent]): Future[Result] = {

    val result =
      for
        index          <- maybeIndex
        occupierDetail <- LettingHistory.completedLettings(request.sessionData).lift(index)
      yield {
        val partiallyAppliedView = theView.apply(_, occupierDetail.name, index, backLinkUrl(index))
        generateResult.apply(partiallyAppliedView, index)
      }

    result.getOrElse(successful(Redirect("/path/to/occupiers-list")))
  }

  private def backLinkUrl(index: Int)(using request: SessionRequest[AnyContent]): Option[String] =
    val navigationData = Map("index" -> index.toString)
    navigator.backLinkUrl(ofPage = RentalPeriodPageId, navigationData)
