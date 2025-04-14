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

package controllers.accommodation

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.accommodation.AccommodationUnitList6048Form.accommodationUnitList6048Form
import form.accommodation.RemoveLastUnit6048Form.removeLastUnit6048Form
import models.submissions.accommodation.AccommodationDetails.updateAccommodationDetails
import models.submissions.accommodation.{AccommodationDetails, AccommodationUnit}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AccommodationNavigator
import navigation.identifiers.AccommodationUnitListPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.accommodation.accommodationUnitList6048
import views.html.accommodation.removeLastUnit6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AccommodationUnitList6048Controller @Inject() (
  accommodationUnitListView: accommodationUnitList6048,
  removeLastUnit6048View: removeLastUnit6048,
  navigator: AccommodationNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      accommodationUnitListView(
        accommodationUnitList6048Form,
        accommodationUnits
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      accommodationUnitList6048Form,
      formWithErrors => BadRequest(accommodationUnitListView(formWithErrors, accommodationUnits)),
      data =>
        (data == AnswerYes, accommodationUnits.size) match {
          case (true, size) if size >= AccommodationDetails.maxAccommodationUnits =>
            Redirect(controllers.accommodation.routes.AddedMaximumAccommodationUnits6048Controller.show)
          case (true, size)                                                       =>
            Redirect(s"${controllers.accommodation.routes.AccommodationUnit6048Controller.show.url}?idx=$size")
          case (false, 0)                                                         =>
            BadRequest(
              accommodationUnitListView(
                accommodationUnitList6048Form
                  .withError("addMoreAccommodationUnits", "error.accommodationUnits.isEmpty"),
                accommodationUnits
              )
            )
          case (false, _)                                                         =>
            Redirect(navigator.nextPage(AccommodationUnitListPageId, request.sessionData).apply(request.sessionData))
        }
    )
  }

  def remove: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    if navigator.idx == 0 && accommodationUnits.size == 1 then
      Ok(removeLastUnit6048View(removeLastUnit6048Form, selectedUnitName))
    else performRemove()
  }

  def removeLast: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      removeLastUnit6048Form,
      formWithErrors => BadRequest(removeLastUnit6048View(formWithErrors, selectedUnitName)),
      data =>
        if data == AnswerYes then performRemove(removeLastAllowed = true)
        else if navigator.from == "CYA" then Redirect(navigator.cyaPage.get)
        else Redirect(controllers.accommodation.routes.AccommodationUnitList6048Controller.show)
    )
  }

  private def performRemove(removeLastAllowed: Boolean = false)(implicit
    request: SessionRequest[AnyContent]
  ): Future[Result] =
    val updatedData = updateAccommodationDetails(accommodationDetails =>
      val accommodationUnits = accommodationDetails.accommodationUnits.patch(navigator.idx, Nil, 1)

      accommodationDetails.copy(
        accommodationUnits = accommodationUnits,
        exceededMaxUnits = None,
        sectionCompleted = Option.when(removeLastAllowed)(AnswerNo).orElse(accommodationDetails.sectionCompleted)
      )
    )

    session
      .saveOrUpdate(updatedData)
      .map { _ =>
        if updatedData.accommodationDetails.exists(_.accommodationUnits.nonEmpty) then
          if navigator.from == "CYA" then Redirect(navigator.cyaPage.get)
          else Redirect(controllers.accommodation.routes.AccommodationUnitList6048Controller.show)
        else Redirect(s"${controllers.accommodation.routes.AccommodationUnit6048Controller.show.url}?idx=0")
      }

  private def accommodationDetails(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationDetails] = request.sessionData.accommodationDetails

  private def accommodationUnits(implicit
    request: SessionRequest[AnyContent]
  ): List[AccommodationUnit] =
    accommodationDetails.fold(List.empty)(_.accommodationUnits)

  private def selectedUnit(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationUnit] =
    accommodationDetails
      .flatMap(_.accommodationUnits.lift(navigator.idx))

  private def selectedUnitName(implicit
    request: SessionRequest[AnyContent]
  ): String =
    selectedUnit.fold("")(_.unitName)

}
