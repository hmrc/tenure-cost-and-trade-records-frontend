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
import form.accommodation.IncludedTariffItems6048Form.includedTariffItems6048Form
import models.submissions.accommodation.AccommodationDetails.updateAccommodationUnit
import models.submissions.accommodation.{AccommodationDetails, AccommodationTariffItem, AccommodationUnit}
import navigation.AccommodationNavigator
import navigation.identifiers.IncludedTariffItemsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.accommodation.includedTariffItems6048

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IncludedTariffItems6048Controller @Inject() (
  includedTariffItems6048View: includedTariffItems6048,
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
      includedTariffItems6048View(
        currentUnit
          .flatMap(_.includedTariffItems)
          .fold(includedTariffItems6048Form)(includedTariffItems6048Form.fill),
        currentUnitName,
        backLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Seq[AccommodationTariffItem]](
      includedTariffItems6048Form,
      formWithErrors => BadRequest(includedTariffItems6048View(formWithErrors, currentUnitName, backLink)),
      data => {
        val updatedData = updateAccommodationUnit(
          navigator.idx,
          _.copy(
            includedTariffItems = Some(data)
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(
              navigator
                .nextPage(IncludedTariffItemsPageId, updatedData)
                .apply(updatedData)
                // TODO: navigator.nextPageWithParam(IncludedTariffItemsPageId, updatedData, s"idx=${navigator.idx}")
            )
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
    s"${controllers.accommodation.routes.HighSeasonTariff6048Controller.show.url}?idx=${navigator.idx}"

}
