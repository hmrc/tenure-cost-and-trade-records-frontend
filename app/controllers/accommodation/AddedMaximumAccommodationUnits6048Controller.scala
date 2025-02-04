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
import form.accommodation.AddedMaximumAccommodationUnits6048Form.addedMaximumAccommodationUnits6048Form
import models.submissions.accommodation.AccommodationDetails
import models.submissions.accommodation.AccommodationDetails.updateAccommodationDetails
import navigation.AccommodationNavigator
import navigation.identifiers.AddedMaximumAccommodationUnitsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.accommodation.addedMaximumAccommodationUnits6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AddedMaximumAccommodationUnits6048Controller @Inject() (
  addedMaximumAccommodationUnits6048View: addedMaximumAccommodationUnits6048,
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
      addedMaximumAccommodationUnits6048View(
        accommodationDetails
          .map(_.exceededMaxUnits)
          .fold(addedMaximumAccommodationUnits6048Form)(addedMaximumAccommodationUnits6048Form.fill)
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Option[Boolean]](
      addedMaximumAccommodationUnits6048Form,
      formWithErrors => BadRequest(addedMaximumAccommodationUnits6048View(formWithErrors)),
      data => {
        val updatedData = updateAccommodationDetails(
          _.copy(
            exceededMaxUnits = data
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(navigator.nextPage(AddedMaximumAccommodationUnitsPageId, updatedData).apply(updatedData))
          }
      }
    )
  }

  private def accommodationDetails(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationDetails] = request.sessionData.accommodationDetails

}
