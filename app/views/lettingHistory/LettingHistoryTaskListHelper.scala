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
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*

object LettingHistoryTaskListHelper:

  def isTaskComplete(taskId: String)(using request: SessionRequest[?]): AnswersYesNo =
    val data = request.sessionData
    taskId match
      case "residential-tenants" =>
        (for
          hasPermanentResidents        <- hasPermanentResidents(data)
          doesNotHavePermanentResidents = !hasPermanentResidents
          permanentResidentList         = permanentResidents(data)
        yield
          if doesNotHavePermanentResidents then AnswerYes
          else if permanentResidentList.nonEmpty then AnswerYes
          else AnswerNo).getOrElse(AnswerNo)

      case "temporary-occupiers" =>
        (for
          hasCompletedLettings        <- hasCompletedLettings(data)
          doesNotHaveCompletedLettings = !hasCompletedLettings
          completedLettingsList        = completedLettings(data)
        yield
          if doesNotHaveCompletedLettings then AnswerYes
          else if completedLettingsList.nonEmpty then AnswerYes
          else AnswerNo).getOrElse(AnswerNo)

      case "intended-lettings" =>
        (
          for
            _                 <- intendedLettingsNights(data)
            isYearlyAvailable <- intendedLettingsIsYearlyAvailable(data)
          yield
            if isYearlyAvailable
            then AnswerYes
            else
              intendedLettingsTradingPeriod(data) match
                case Some(_) => AnswerYes
                case None    => AnswerNo
        ).getOrElse(AnswerNo)

      case "online-advertising" =>
        (for
          hasOnlineAdvertising        <- hasOnlineAdvertising(data)
          doesNotHaveOnlineAdvertising = !hasOnlineAdvertising
          onlineAdvertisingList        = onlineAdvertising(data)
        yield
          if doesNotHaveOnlineAdvertising then AnswerYes
          else if onlineAdvertisingList.nonEmpty then AnswerYes
          else AnswerNo).getOrElse(AnswerNo)

      case _ =>
        AnswerNo
