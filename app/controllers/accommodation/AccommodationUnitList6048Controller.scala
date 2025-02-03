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
import models.submissions.accommodation.{AccommodationDetails, AccommodationUnit}
import models.submissions.common.{AnswerYes, AnswersYesNo}
import navigation.AccommodationNavigator
import navigation.identifiers.AccommodationUnitListPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.accommodation.accommodationUnitList6048

import javax.inject.{Inject, Named, Singleton}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AccommodationUnitList6048Controller @Inject() (
  accommodationUnitListView: accommodationUnitList6048,
  navigator: AccommodationNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      accommodationUnitListView(
        accommodationUnitList6048Form,
        accommodationUnits,
        backLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      accommodationUnitList6048Form,
      formWithErrors => BadRequest(accommodationUnitListView(formWithErrors, accommodationUnits, backLink)),
      data =>
        if data == AnswerYes then
          Redirect(
            s"${controllers.accommodation.routes.AccommodationUnit6048Controller.show.url}?idx=${accommodationUnits.size}"
          )
        else
          Redirect(
            navigator
              .nextPage(AccommodationUnitListPageId, request.sessionData)
              .apply(request.sessionData)
          )
    )
  }

  def remove: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    if navigator.idx == 0 && accommodationUnits.size == 1 then Ok("Are you sure to remove last?")
    else performRemove
  }

  def removeLast: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    // if yes
    performRemove
    // if no
    Redirect(controllers.accommodation.routes.AccommodationUnitList6048Controller.show)
  }

  private def performRemove(implicit
    request: SessionRequest[AnyContent]
  ): Result =
    // TODO: remove accommodationUnit by navigator.idx
    if accommodationUnits.isEmpty then // accommodationUnits after removing - !!! inside .map()
      Redirect(s"${controllers.accommodation.routes.AccommodationUnit6048Controller.show.url}?idx=0")
    else Redirect(controllers.accommodation.routes.AccommodationUnitList6048Controller.show)

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

  private def backLink(implicit
    request: SessionRequest[AnyContent]
  ): String =
    s"${controllers.accommodation.routes.IncludedTariffItems6048Controller.show.url}?idx=${navigator.idx}"

}
