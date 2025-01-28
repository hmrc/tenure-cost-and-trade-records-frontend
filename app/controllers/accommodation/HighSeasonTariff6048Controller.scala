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
import form.accommodation.HighSeasonTariff6048Form.highSeasonTariff6048Form
import models.submissions.accommodation.AccommodationDetails.updateAccommodationUnit
import models.submissions.accommodation.{AccommodationDetails, AccommodationUnit, AvailableRooms, HighSeasonTariff}
import navigation.AccommodationNavigator
import navigation.identifiers.HighSeasonTariffPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.DateUtilLocalised
import views.html.accommodation.highSeasonTariff6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class HighSeasonTariff6048Controller @Inject() (
  highSeasonTariff6048View: highSeasonTariff6048,
  navigator: AccommodationNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext, dateUtil: DateUtilLocalised)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      highSeasonTariff6048View(
        currentUnit
          .flatMap(_.highSeasonTariff)
          .fold(highSeasonTariff6048Form)(highSeasonTariff6048Form.fill),
        backLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[HighSeasonTariff](
      highSeasonTariff6048Form,
      formWithErrors => BadRequest(highSeasonTariff6048View(formWithErrors, backLink)),
      data => {
        val updatedData = updateAccommodationUnit(
          navigator.idx,
          _.copy(
            highSeasonTariff = Some(data)
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(
              navigator
                .nextPage(HighSeasonTariffPageId, updatedData)
                .apply(updatedData)
                // TODO: navigator.nextPageWithParam(HighSeasonTariffPageId, updatedData, s"idx=${navigator.idx}")
            )
          }
      }
    )
  }

  private def currentUnit(implicit
    request: SessionRequest[AnyContent]
  ): Option[AccommodationUnit] =
    request.sessionData.accommodationDetails
      .flatMap(_.accommodationUnits.lift(navigator.idx))

  private def backLink(implicit
    request: SessionRequest[AnyContent]
  ): String =
    s"${controllers.accommodation.routes.AccommodationLettingHistory6048Controller.show.url}?idx=${navigator.idx}"

}
