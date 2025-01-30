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
import form.accommodation.AccommodationLettingHistory6048Form.accommodationLettingHistory6048Form
import models.submissions.accommodation.AccommodationDetails.updateAccommodationUnit
import models.submissions.accommodation.{AccommodationDetails, AccommodationLettingHistory, AccommodationUnit, AvailableRooms}
import navigation.AccommodationNavigator
import navigation.identifiers.AccommodationLettingHistoryPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil
import views.html.accommodation.accommodationLettingHistory6048

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AccommodationLettingHistory6048Controller @Inject() (
  accommodationLettingHistoryView: accommodationLettingHistory6048,
  navigator: AccommodationNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val yearEndDates = finYearEndDates
    val years        = yearEndDates.map(_.getYear.toString)

    Ok(
      accommodationLettingHistoryView(
        currentUnit
          .flatMap(_.lettingHistory)
          .filter(_.size == years.size)
          .fold(accommodationLettingHistory6048Form(years))(accommodationLettingHistory6048Form(years).fill),
        yearEndDates,
        currentUnitName,
        backLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val yearEndDates = finYearEndDates
    val years        = yearEndDates.map(_.getYear.toString)

    continueOrSaveAsDraft[Seq[AccommodationLettingHistory]](
      accommodationLettingHistory6048Form(years),
      formWithErrors =>
        BadRequest(accommodationLettingHistoryView(formWithErrors, yearEndDates, currentUnitName, backLink)),
      data => {
        val updatedLettingHistory =
          (data zip yearEndDates).map { case (lettingHistory, yearEnd) =>
            lettingHistory.copy(financialYearEnd = yearEnd)
          }

        val updatedData = updateAccommodationUnit(
          navigator.idx,
          _.copy(
            lettingHistory = Some(updatedLettingHistory)
          )
        )

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            Redirect(
              navigator.nextPageWithParam(AccommodationLettingHistoryPageId, updatedData, s"idx=${navigator.idx}")
            )
          }
      }
    )
  }

  private def finYearEndDates(implicit
    request: SessionRequest[AnyContent]
  ): Seq[LocalDate] =
    val firstOccupy = request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.commercialLetDate)
    AccountingInformationUtil.financialYearsRequiredAccommodation6048(firstOccupy, request.sessionData.isWelsh)

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
    s"${controllers.accommodation.routes.AvailableRooms6048Controller.show.url}?idx=${navigator.idx}"

}
