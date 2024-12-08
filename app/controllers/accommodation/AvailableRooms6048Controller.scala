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

package controllers.accommodation

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.accommodation.AvailableRooms6048Form.availableRooms6048Form
import models.submissions.accommodation.{AccommodationDetails, AccommodationUnit, AvailableRooms}
import models.submissions.accommodation.AccommodationDetails.updateAccommodationUnit
import navigation.AccommodationNavigator
import navigation.identifiers.AvailableRoomsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.accommodation.availableRooms6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AvailableRooms6048Controller @Inject() (
  availableRoomsView: availableRooms6048,
  navigator: AccommodationNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      availableRoomsView(
        currentUnit
          .flatMap(_.availableRooms)
          .fold(availableRooms6048Form)(availableRooms6048Form.fill),
        currentUnitName,
        backLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AvailableRooms](
      availableRooms6048Form,
      formWithErrors => BadRequest(availableRoomsView(formWithErrors, currentUnitName, backLink)),
      data => {
        val updatedData = updateAccommodationUnit(
          navigator.idx,
          _.copy(
            availableRooms = Some(data)
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(
              navigator.nextPage(AvailableRoomsPageId, updatedData).apply(updatedData)
            ) // .url.replace("99", navigator.idx.toString)
          }
      }
    )
  }

  private def accommodationDetails(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationDetails] = request.sessionData.accommodationDetails

  private def currentUnit(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationUnit] =
    accommodationDetails
      .flatMap(_.accommodationUnits.lift(navigator.idx))

  private def currentUnitName(implicit
    request: SessionRequest[AnyContent]
  ): String =
    currentUnit.fold("")(_.unitName)

  private def backLink(implicit
    request: SessionRequest[AnyContent]
  ): String =
    controllers.accommodation.routes.AccommodationUnit6048Controller.show(navigator.idx).url

}
