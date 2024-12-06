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
import form.accommodation.AccommodationUnit6048Form.accommodationUnit6048Form
import models.submissions.accommodation.AccommodationDetails.updateAccommodationUnit
import models.submissions.accommodation.AccommodationDetails
import navigation.AccommodationNavigator
import navigation.identifiers.AccommodationUnitPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.accommodation.accommodationUnit6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AccommodationUnit6048Controller @Inject() (
  accommodationUnitView: accommodationUnit6048,
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
      accommodationUnitView(
        accommodationDetails
          .flatMap(_.accommodationUnits.lift(currentUnitIndex))
          .map(accommodation => (accommodation.unitName, accommodation.unitType))
          .fold(accommodationUnit6048Form)(accommodationUnit6048Form.fill)
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[(String, String)](
      accommodationUnit6048Form,
      formWithErrors => BadRequest(accommodationUnitView(formWithErrors)),
      data => {
        val updatedData = updateAccommodationUnit(
          currentUnitIndex,
          _.copy(
            unitName = data._1,
            unitType = data._2
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(navigator.nextPage(AccommodationUnitPageId, updatedData).apply(updatedData))
          }
      }
    )
  }

  private def currentUnitIndex(implicit
    request: SessionRequest[AnyContent]
  ): Int =
    // TODO: Get current index from request parameter
    accommodationDetails
      .map(_.accommodationUnits)
      .filter(_.nonEmpty)
      .map(_.size - 1)
      .getOrElse(0)

  private def accommodationDetails(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationDetails] = request.sessionData.accommodationDetails

}
