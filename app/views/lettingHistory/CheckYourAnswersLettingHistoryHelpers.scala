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

package views.lettingHistory

import actions.SessionRequest
import controllers.lettingHistory.routes
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import play.api.i18n.Messages
import play.api.mvc.{AnyContent, Call}
import util.DateUtilLocalised
import views.includes.cards.{CardData, CardEntry}
import views.includes.summary.SummaryEntry

import java.time.{LocalDate, MonthDay}
import scala.collection.mutable

object CheckYourAnswersLettingHistoryHelpers:

  def permanentResidentsCardsData(
    fragment: String
  )(using request: SessionRequest[AnyContent], messages: Messages): Seq[CardData] =
    LettingHistory.permanentResidents(request.sessionData).zipWithIndex.map { (resident, index) =>
      CardData(
        index = index,
        removeAction = routes.ResidentListController.performRemove(index).withFromCheckYourAnswer(fragment),
        entries = Seq(
          CardEntry(
            key = messages("lettingHistory.checkYourAnswers.permanentResidents.cardKey1"),
            value = List(resident.name, resident.address.replace("\n", "<br>")).mkString("<br>"),
            changeAction = routes.ResidentDetailController.show(Some(index)).withFromCheckYourAnswer(fragment)
          )
        )
      )
    }

  def completedLettingsCardsData(
    fragment: String
  )(using request: SessionRequest[AnyContent], messages: Messages): Seq[CardData] =
    LettingHistory.completedLettings(request.sessionData).zipWithIndex.map { (occupier, index) =>
      CardData(
        index = index,
        removeAction = routes.OccupierListController.performRemove(index).withFromCheckYourAnswer(fragment),
        entries = Seq(
          CardEntry(
            key = messages("lettingHistory.checkYourAnswers.completedLettings.cardKey1"),
            value = List(
              Some(occupier.name),
              occupier.address.map(_.multiLine)
            ).filter(_.isDefined).map(_.get).mkString("<br>"),
            changeAction = routes.OccupierDetailController.show(Some(index)).withFromCheckYourAnswer(fragment)
          )
        )
      )
    }

  def onlineAdvertisingCardsData(
    fragment: String
  )(using request: SessionRequest[AnyContent], messages: Messages): Seq[CardData] =
    LettingHistory.onlineAdvertising(request.sessionData).zipWithIndex.map { (advertising, index) =>
      CardData(
        index = index,
        removeAction = routes.AdvertisingListController.performRemove(index).withFromCheckYourAnswer(fragment),
        entries = Seq(
          CardEntry(
            key = messages("lettingHistory.checkYourAnswers.onlineAdvertising.cardKey1"),
            value = List(advertising.websiteAddress, advertising.propertyReferenceNumber).mkString("<br>"),
            changeAction = routes.AdvertisingDetailController.show(Some(index)).withFromCheckYourAnswer(fragment)
          )
        )
      )
    }

  def intendedLettingsSummaryData(
    dateUtil: DateUtilLocalised,
    fragment: String
  )(using request: SessionRequest[AnyContent], messages: Messages): Seq[SummaryEntry] = {
    val data = mutable.Buffer(
      SummaryEntry(
        key = messages("lettingHistory.checkYourAnswers.intendedLettings.nights"),
        maybeValue = mapInt2String(LettingHistory.intendedLettingsNights(request.sessionData)),
        changeAction = routes.HowManyNightsController.show.withFromCheckYourAnswer(fragment)
      )
    )
    if LettingHistory.intendedLettingsNights(request.sessionData).isDefined
    then
      val meetsCriteria = IntendedLettings.doesMeetLettingCriteria(request.sessionData)
      // assert(meetsCriteria).isDefined
      if !meetsCriteria.get then
        data ++= Seq(
          SummaryEntry(
            key = messages("lettingHistory.checkYourAnswers.intendedLettings.hasStopped"),
            maybeValue = mapBool2String(LettingHistory.intendedLettingsHasStopped(request.sessionData)),
            changeAction = routes.HasStoppedLettingController.show.withFromCheckYourAnswer(fragment)
          ),
          SummaryEntry(
            key = messages("lettingHistory.checkYourAnswers.intendedLettings.whenWasLastLet"),
            maybeValue = mapDate2String(LettingHistory.intendedLettingsWhenWasLastLet(request.sessionData), dateUtil),
            changeAction = routes.WhenWasLastLetController.show.withFromCheckYourAnswer(fragment)
          )
        )

      // regardless of meeting lettings criteria, add the following entries

      val isYearlyAvailable = LettingHistory.intendedLettingsIsYearlyAvailable(request.sessionData)

      if isYearlyAvailable.getOrElse(false)
      then
        data += SummaryEntry(
          key = messages("lettingHistory.checkYourAnswers.intendedLettings.isYearlyAvailable"),
          maybeValue = mapBool2String(isYearlyAvailable),
          changeAction = routes.IsYearlyAvailableController.show.withFromCheckYourAnswer(fragment)
        )

      if isYearlyAvailable == Some(false)
      then
        data ++= Seq(
          SummaryEntry(
            key = messages("lettingHistory.checkYourAnswers.intendedLettings.isYearlyAvailable"),
            maybeValue = mapBool2String(LettingHistory.intendedLettingsIsYearlyAvailable(request.sessionData)),
            changeAction = routes.IsYearlyAvailableController.show.withFromCheckYourAnswer(fragment)
          ),
          SummaryEntry(
            key = messages("lettingHistory.checkYourAnswers.intendedLettings.tradingPeriod"),
            maybeValue = LettingHistory.intendedLettingsTradingPeriod(request.sessionData).map { t =>
              messages(
                "lettingHistory.CYA.tradingSeason.text",
                dateUtil.formatMonthDay(MonthDay.from(t.fromDate)),
                dateUtil.formatMonthDay(MonthDay.from(t.toDate))
              )
            },
            changeAction = routes.TradingSeasonController.show.withFromCheckYourAnswer(fragment)
          )
        )
      data.toSeq
    else data.toSeq
  }

  def mapBool2String(maybeBool: Option[Boolean])(using messages: Messages): Option[String] =
    maybeBool.map {
      case true  => messages("label.yes")
      case false => messages("label.no")
    }

  def mapInt2String(int: Option[Int])(using messages: Messages): Option[String] =
    int
      .map(i => i.toString)
      .orElse(Some(""))

  def mapDate2String(date: Option[LocalDate], dateUtil: DateUtilLocalised)(using messages: Messages): Option[String] =
    date
      .map(d => dateUtil.formatDate(d))
      .orElse(Some(""))

  extension (call: Call)
    def withFromCheckYourAnswer(fragment: String) =
      // Append the "from" query string parameter with a "single" value
      // Notice that the ";" semicolon character is just a "trick" intended to propagate the fragment
      call.copy(
        url = call.url + (if call.url.contains("?") then "&" else "?") + s"from=CYA;$fragment"
      )
