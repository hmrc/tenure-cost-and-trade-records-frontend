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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.OtherHolidayAccommodationDetailsForm.otherHolidayAccommodationDetailsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateOtherHolidayAccommodation
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistoryPartOne, OtherHolidayAccommodationDetails}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OtherHolidayAccommodationDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.otherHolidayAccommodationDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class OtherHolidayAccommodationDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: otherHolidayAccommodationDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          aboutTheTradingHistoryPartOne
            .flatMap(_.otherHolidayAccommodation)
            .flatMap(_.otherHolidayAccommodationDetails)
            .fold(otherHolidayAccommodationDetailsForm)(otherHolidayAccommodationDetailsForm.fill),
          request.sessionData.toSummary,
          backLink
        )
      )
    )
  }

  private def aboutTheTradingHistoryPartOne(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutTheTradingHistoryPartOne] = request.sessionData.aboutTheTradingHistoryPartOne

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OtherHolidayAccommodationDetails](
      otherHolidayAccommodationDetailsForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            request.sessionData.toSummary,
            backLink
          )
        ),
      data => {
        val updatedData = updateOtherHolidayAccommodation(_.copy(otherHolidayAccommodationDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(
          navigator
            .nextPageForOtherHolidayAccommodation(OtherHolidayAccommodationDetailsId, updatedData)
            .apply(updatedData)
        )
      }
    )
  }

  private def backLink(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA")
      navigator.cyaPageForOtherHolidayAccommodation.url
    else
      routes.OtherHolidayAccommodationController.show().url

}
